package ru.teamdroid.colibripost.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.channels_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_bottom_navigation.*
import kotlinx.android.synthetic.main.fragment_channels_settings.*
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
import ru.teamdroid.colibripost.ui.core.BaseFragment

class ChannelsSettingsFragment: BaseFragment(){

    lateinit var channelsViewModel: ChannelsViewModel

    override val layoutId = R.layout.fragment_channels_settings

    private lateinit var addedChannelsRView: RecyclerView
    private lateinit var avChannelsRView: RecyclerView
    protected lateinit var lm: RecyclerView.LayoutManager

    private val channelsAdapter : ChannelsAdapter by lazy {
        ChannelsAdapter(
            deleteChannel = {
                deleteChannel(it)
            }
        )
    }
    val avChannelsAdapter = AvailableChannelsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle(getString(R.string.channels))

        setUpFragmentUi(view)

        channelsViewModel = viewModel {
            onSuccess<List<ChannelEntity>,
                    SingleLiveData<List<ChannelEntity>>>(addedChannelsData, ::handleAddedChannels)
            onSuccess<List<ChannelEntity>,
                    SingleLiveData<List<ChannelEntity>>>(avChannelsData, ::handleAvChannels)
            onSuccess<None,
                    SingleLiveData<None>>(setChannelsData, ::refreshChannelsListsData)
            onSuccess<None,
                    SingleLiveData<None>>(deleteChannelData, ::refreshChannelsListsData)
            onFailure<SingleLiveData<Failure>>(failureData, ::handleFailure)
        }

        channelsViewModel.getAddedChannels()

    }

    fun setUpFragmentUi(view: View){
        addedChannelsRView =view.findViewById<RecyclerView>(R.id.rvChannels).apply{
            layoutManager = LinearLayoutManager(context)
            adapter = channelsAdapter
        }
        addedChannelsRView.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

        avChannelsRView =view.findViewById<RecyclerView>(R.id.rvAvailableChannels).apply{
            layoutManager = LinearLayoutManager(context)
            adapter = avChannelsAdapter
        }

        setUpBottomSheet()
    }

    fun setUpBottomSheet(){
        val bottomSheet = BottomSheetBehavior.from(include_bottom_sheet_dialog)
        (requireActivity() as MainActivity).apply {


            bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState){
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            hideTransparentView()
                            transpBackground.visibility = View.GONE
                            bottomNavigation.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

            })
            bottomSheet.apply {
                state = BottomSheetBehavior.STATE_HIDDEN

                btn_add_channels.setOnClickListener {
                    channelsViewModel.setChannels(avChannelsAdapter.getCheckedChannels())
                    state = BottomSheetBehavior.STATE_HIDDEN
                    bottomNavigation.visibility = View.VISIBLE
                }
                btn_cancel_channels.setOnClickListener {
                    state = BottomSheetBehavior.STATE_HIDDEN
                    bottomNavigation.visibility = View.VISIBLE
                    refreshAvChannels()
                }
                btnShowAvChannels.setOnClickListener{
                    state = BottomSheetBehavior.STATE_EXPANDED
                    showTransparentView()
                    transpBackground.visibility = View.VISIBLE
                    bottomNavigation.visibility = View.GONE
                }
                transpView.setOnClickListener {
                    state = BottomSheetBehavior.STATE_HIDDEN
                    bottomNavigation.visibility = View.VISIBLE
                    refreshAvChannels()
                }
                transpBackground.setOnClickListener{
                    state = BottomSheetBehavior.STATE_HIDDEN
                    bottomNavigation.visibility = View.VISIBLE
                    refreshAvChannels()
                }
            }
        }
    }

    fun refreshAvChannels(){
        avChannelsAdapter.checked = BooleanArray(avChannelsAdapter.itemCount)
        avChannelsAdapter.notifyDataSetChanged()
        btn_add_channels.isEnabled = false
        btn_add_channels.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.accentEnabledButton)
    }

    fun deleteChannel(idChannel: Long){
        channelsViewModel.deleteChannel(idChannel)
    }

    fun refreshChannelsListsData(none:None?){
        channelsViewModel.getAddedChannels()
    }

    private fun handleAddedChannels(channels: List<ChannelEntity>?){
        if(channels != null){
            if(lrChannelsEmpty.isVisible)
                lrChannelsEmpty.visibility = View.GONE
            channelsAdapter.submitList(channels)
            channelsViewModel.getAvChannels()
        }
    }

    private fun handleAvChannels(channels: List<ChannelEntity>?){
        if(channels != null){
            avChannelsAdapter.submitList(channels)
        }
    }



    override fun handleFailure(failure: Failure?) {
        when(failure){
            is Failure.ChannelsListIsEmptyError -> {
                lrChannelsEmpty.visibility = View.VISIBLE
                channelsAdapter.clear()
                channelsViewModel.getAvChannels()
            }
            else -> super.handleFailure(failure)
        }
    }

    companion object {
        const val TAG = "ChannelsSettingsFragment"
    }


}