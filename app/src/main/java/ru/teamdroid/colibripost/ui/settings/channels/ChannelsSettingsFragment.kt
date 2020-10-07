package ru.teamdroid.colibripost.ui.settings.channels

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.channels_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_bottom_navigation.*
import kotlinx.android.synthetic.main.fragment_channels_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.MainActivity
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.di.viewmodel.ChannelsViewModel
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.other.SingleLiveData
import ru.teamdroid.colibripost.other.onFailure
import ru.teamdroid.colibripost.other.onSuccess
import ru.teamdroid.colibripost.remote.core.NetworkHandler
import ru.teamdroid.colibripost.remote.core.setNetworkCallback
import ru.teamdroid.colibripost.ui.core.BaseFragment
import ru.teamdroid.colibripost.ui.core.getColorFromResource
import ru.teamdroid.colibripost.ui.core.getColorState
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.System.out
import java.net.URL
import javax.inject.Inject

class ChannelsSettingsFragment: BaseFragment(){

    @Inject
    lateinit var networkHandler: NetworkHandler

    lateinit var channelsViewModel: ChannelsViewModel

    override val layoutId = R.layout.fragment_channels_settings

    private lateinit var addedChannelsRView: RecyclerView
    private lateinit var availableChannelsRView: RecyclerView
    protected lateinit var lm: RecyclerView.LayoutManager

    private lateinit var bottomSheet: BottomSheetBehavior<View>

    private val channelsAdapter : ChannelsAdapter by lazy {
        ChannelsAdapter(
            showDeleteChannelDialog = {
                showDeleteChannelDialog(it)
            }
        )
    }
    val availableChannelsAdapter = AvailableChannelsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(networkHandler.isConnected != null)
            setNetworkAvailbleUi(false)
        else setNetworkLostUi()

        setUpFragmentUi(view)

        channelsViewModel = viewModel {
            onSuccess<List<ChannelEntity>,
                    SingleLiveData<List<ChannelEntity>>>(addedChannelsData, ::handleAddedChannels)
            onSuccess<List<ChannelEntity>,
                    SingleLiveData<List<ChannelEntity>>>(avChannelsData, ::handleAvailableChannels)
            onSuccess<None,
                    SingleLiveData<None>>(setChannelsData, ::refreshChannelsListsData)
            onSuccess<None,
                    SingleLiveData<None>>(deleteChannelData, ::refreshChannelsListsData)
            onSuccess(progressData, ::updateRefresh)
            onFailure<SingleLiveData<Failure>>(failureData, ::handleFailure)
        }

        (requireActivity() as MainActivity).connectivityManager.let {
            it.setNetworkCallback({setNetworkAvailbleUi(true)}, ::setNetworkLostUi)
        }

        channelsViewModel.getAddedChannels()

    }

    //region UI control

    fun setUpFragmentUi(view: View){
        addedChannelsRView =view.findViewById<RecyclerView>(R.id.rvChannels).apply{
            layoutManager = LinearLayoutManager(context)
            adapter = channelsAdapter
        }
        addedChannelsRView.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

        availableChannelsRView =view.findViewById<RecyclerView>(R.id.rvAvailableChannels).apply{
            layoutManager = LinearLayoutManager(context)
            adapter = availableChannelsAdapter
        }

        setUpBottomSheet()
    }

    fun setUpBottomSheet(){
        bottomSheet = BottomSheetBehavior.from(include_bottom_sheet_dialog)
        (requireActivity() as MainActivity).apply {
            bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if(newState == BottomSheetBehavior.STATE_SETTLING)
                    {
                        setTranspViewVisibility(false)
                        transpBackground.visibility = View.GONE
                        bottomNavigation.visibility = View.VISIBLE
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if(transpBackground.visibility == View.VISIBLE)
                        if(slideOffset <= 0.1) {
                            bottomNavigation.visibility = View.VISIBLE
                        }else bottomNavigation.visibility = View.GONE
                }

            })
            bottomSheet.apply {

                btn_add_channels.setOnClickListener {
                    channelsViewModel.setChannels(availableChannelsAdapter.getCheckedChannels())
                    state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomNavigation.visibility = View.VISIBLE
                }
                btn_cancel_channels.setOnClickListener {
                    state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomNavigation.visibility = View.VISIBLE
                    refreshAvailableChannels()
                }
                btnShowAvailableChannels.setOnClickListener{
                    state = BottomSheetBehavior.STATE_EXPANDED
                    setTranspViewVisibility(true)
                    transpBackground.visibility = View.VISIBLE
                    bottomNavigation.visibility = View.GONE
                }
                transpView.setOnClickListener {
                    state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomNavigation.visibility = View.VISIBLE
                    refreshAvailableChannels()
                }
                transpBackground.setOnClickListener{
                    state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomNavigation.visibility = View.VISIBLE
                    refreshAvailableChannels()
                }
            }
        }
    }

    private fun bottomSheetSetPlaceHolder(){
        tvTextError.text = resources.getString(R.string.need_create_channels_error)
        lrChannelsNotExist.visibility = View.VISIBLE
        availableChannelsAdapter.clear()
        refreshAvailableChannels()
    }

    private fun refreshAvailableChannels(){
        availableChannelsAdapter.checked = BooleanArray(availableChannelsAdapter.itemCount)
        availableChannelsAdapter.notifyDataSetChanged()
        btn_add_channels.isEnabled = false
        btn_add_channels.backgroundTintList = requireContext().getColorState(R.color.accentEnabled)
    }

    private fun showDeleteChannelDialog(idChannel: Long){
        DeleteChannelDialog(idChannel) {
            channelsViewModel.deleteChannel(it)
        }.show((requireActivity() as MainActivity).supportFragmentManager, DeleteChannelDialog.TAG)
    }

    fun refreshChannelsListsData(none: None?){
        channelsViewModel.getAddedChannels()
    }

    override fun setNetworkAvailbleUi(isCallback:Boolean) {
        lifecycleScope.launch {
            setToolbarTitle(getString(R.string.channels))
            if(isCallback){
                setBtnShowAvailableChannelsState(true)
                channelsViewModel.getAvChannels()
            }
        }
    }

    override fun setNetworkLostUi() {
        lifecycleScope.launch {
            setBtnShowAvailableChannelsState(false)
        }
    }

    fun setBtnShowAvailableChannelsState(isEnable:Boolean){
        if(isEnable){
            btnShowAvailableChannels.isEnabled = true
            btnShowAvailableChannels.setTextColor(requireContext().getColorFromResource(R.color.accent))
            btnShowAvailableChannels.iconTint = requireContext().getColorState(R.color.accent)
        }else{
            btnShowAvailableChannels.isEnabled = false
            btnShowAvailableChannels.setTextColor(requireContext().getColorFromResource(R.color.accentEnabled))
            btnShowAvailableChannels.iconTint = requireContext().getColorState(R.color.accentEnabled)
        }
    }

    //endregion

    //region Handle events
    private fun handleAddedChannels(channels: List<ChannelEntity>?){
        updateRefresh(false)
        if(channels != null){
            if(lrChannelsEmpty.isVisible)
                lrChannelsEmpty.visibility = View.GONE
            tvChannelsCount.text = this.resources.getQuantityString(R.plurals.channels_count, channels.size,  channels.size )
            channelsAdapter.submitList(channels)

            channelsViewModel.getAvChannels()
        }
    }

    private fun handleAvailableChannels(channels: List<ChannelEntity>?){
        if(channels != null){
            if(lrChannelsNotExist.isVisible)
                lrChannelsNotExist.visibility = View.GONE
            availableChannelsAdapter.submitList(channels)
        }
    }

    override fun handleFailure(failure: Failure?) {
        updateRefresh(false)
        when(failure){
            is Failure.ChannelsListIsEmptyError -> {
                hideRefreshing()
                lrChannelsEmpty.visibility = View.VISIBLE
                channelsAdapter.clear()
                val count = this.resources.getQuantityString(R.plurals.channels_count, 0, 0)
                tvChannelsCount.text = count
                channelsViewModel.getAvChannels()
            }
            is Failure.ChannelsNotCreatedError -> {
                bottomSheetSetPlaceHolder()
            }
            is Failure.NetworkPlaceHolderConnectionError -> {
                //bottomSheetSetPlaceHolder(true)
            }
            else -> super.handleFailure(failure)
        }
    }
    //endregion

    companion object {
        const val TAG = "ChannelsSettingsFragment"
    }


}