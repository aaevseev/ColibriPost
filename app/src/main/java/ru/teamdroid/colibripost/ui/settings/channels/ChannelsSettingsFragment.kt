
package ru.teamdroid.colibripost.ui.settings.channels

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.launch
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.MainActivity
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.FragmentChannelsSettingsBinding
import ru.teamdroid.colibripost.di.viewmodel.ChannelsViewModel
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.other.onFailure
import ru.teamdroid.colibripost.other.onSuccess
import ru.teamdroid.colibripost.remote.core.NetworkHandler
import ru.teamdroid.colibripost.remote.core.setNetworkCallback
import ru.teamdroid.colibripost.ui.bottomnavigation.BottomNavigationFragment
import ru.teamdroid.colibripost.ui.core.BaseFragment
import ru.teamdroid.colibripost.ui.core.getColorFromResource
import ru.teamdroid.colibripost.ui.core.getColorState
import javax.inject.Inject

class ChannelsSettingsFragment : BaseFragment() {

    @Inject
    lateinit var networkHandler: NetworkHandler

    lateinit var channelsViewModel: ChannelsViewModel

    lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override val layoutId = R.layout.fragment_channels_settings

    override val toolbarTitle = R.string.channels

    private lateinit var addedChannelsRView: RecyclerView
    private lateinit var availableChannelsRView: RecyclerView
    protected lateinit var lm: RecyclerView.LayoutManager

    private lateinit var bottomSheet: BottomSheetBehavior<View>

    private var _binding:FragmentChannelsSettingsBinding? = null
    private val binding: FragmentChannelsSettingsBinding
        get() = _binding!!

    private val channelsAdapter: ChannelsAdapter by lazy {
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentChannelsSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        base { supportActionBar?.setDisplayHomeAsUpEnabled(true) }
        if (networkHandler.isConnected != null)
            setNetworkAvailbleUi(false)
        else setNetworkLostUi()

        setUpFragmentUi(view)

        channelsViewModel = viewModel {
            onSuccess(addedChannelsData, ::handleAddedChannels)
            onSuccess(avChannelsData, ::handleAvailableChannels)
            onSuccess(setChannelsData, ::refreshChannelsListsData)
            onSuccess(deleteChannelData, ::refreshChannelsListsData)
            onSuccess(progressData, ::updateRefresh)
            onFailure(failureData, ::handleFailure)
        }

        (requireActivity() as MainActivity).connectivityManager.let {
            networkCallback = it.setNetworkCallback({ setNetworkAvailbleUi(true) }, ::setNetworkLostUi)
        }

        channelsViewModel.getAddedChannels()

    }

    //region UI control

    fun setUpFragmentUi(view: View) {
        addedChannelsRView = binding.rvChannels.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = channelsAdapter
        }
        addedChannelsRView.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

        availableChannelsRView = view.findViewById<RecyclerView>(R.id.rvAvailableChannels).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = availableChannelsAdapter
        }

        setUpBottomSheet()
    }

    fun setUpBottomSheet() {
        bottomSheet = BottomSheetBehavior.from(include_bottom_sheet_dialog)
        (requireActivity() as MainActivity).apply {
            bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_SETTLING) {
                        setTranspViewVisibility(false)
                        binding.transpBackground.visibility = View.GONE
                        bottomNavigation.visibility = View.VISIBLE
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (binding.transpBackground.visibility == View.VISIBLE)
                        if (slideOffset <= 0.1) {
                            bottomNavigation.visibility = View.VISIBLE
                        } else bottomNavigation.visibility = View.GONE
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
                binding.btnShowAvailableChannels.setOnClickListener {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    setTranspViewVisibility(true)
                    binding.transpBackground.visibility = View.VISIBLE
                    bottomNavigation.visibility = View.GONE
                }
                transpView.setOnClickListener {
                    state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomNavigation.visibility = View.VISIBLE
                    refreshAvailableChannels()
                }
                binding.transpBackground.setOnClickListener {
                    state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomNavigation.visibility = View.VISIBLE
                    refreshAvailableChannels()
                }
            }
        }
    }

    private fun bottomSheetSetPlaceHolder() {
        tvTextError.text = resources.getString(R.string.need_create_channels_error)
        lrChannelsNotExist.visibility = View.VISIBLE
        availableChannelsAdapter.clear()
        refreshAvailableChannels()
    }

    private fun refreshAvailableChannels() {
        availableChannelsAdapter.checked = BooleanArray(availableChannelsAdapter.itemCount)
        availableChannelsAdapter.notifyDataSetChanged()
        btn_add_channels.isEnabled = false
        btn_add_channels.backgroundTintList = requireContext().getColorState(R.color.accentEnabled)
    }

    private fun showDeleteChannelDialog(idChannel: Long) {
        DeleteChannelDialog(idChannel) {
            channelsViewModel.deleteChannel(it)
        }.show((requireActivity() as MainActivity).supportFragmentManager, DeleteChannelDialog.TAG)
    }

    fun refreshChannelsListsData(none: None?) {
        channelsViewModel.getAddedChannels()
    }

    override fun setNetworkAvailbleUi(isCallback: Boolean) {
        lifecycleScope.launch {
            setToolbarTitle()
            if (isCallback) {
                setBtnShowAvailableChannelsState(true)
                channelsViewModel.getAvChannels()
            }
        }
    }

    override fun setNetworkLostUi() {
        lifecycleScope.launch {
            super.setNetworkLostUi()
            setBtnShowAvailableChannelsState(false)
        }
    }

    fun setBtnShowAvailableChannelsState(isEnable: Boolean) {
        if (isEnable) {
            binding.btnShowAvailableChannels.isEnabled = true
            binding.btnShowAvailableChannels.setTextColor(requireContext().getColorFromResource(R.color.accent))
            binding.btnShowAvailableChannels.iconTint = requireContext().getColorState(R.color.accent)
        } else {
            binding.btnShowAvailableChannels.isEnabled = false
            binding.btnShowAvailableChannels.setTextColor(requireContext().getColorFromResource(R.color.accentEnabled))
            binding.btnShowAvailableChannels.iconTint =
                requireContext().getColorState(R.color.accentEnabled)
        }
    }

    //endregion

    //region Handle events
    private fun handleAddedChannels(channels: List<ChannelEntity>?) {
        updateRefresh(false)
        if (channels != null) {
            if (binding.lrChannelsEmpty.isVisible)
                binding.lrChannelsEmpty.visibility = View.GONE
            binding.tvChannelsCount.text = this.resources.getQuantityString(
                R.plurals.channels_count,
                channels.size,
                channels.size
            )
            channelsAdapter.submitList(channels)

            channelsViewModel.getAvChannels()
        }
    }

    private fun handleAvailableChannels(channels: List<ChannelEntity>?) {
        if (channels != null) {
            if (lrChannelsNotExist.isVisible)
                lrChannelsNotExist.visibility = View.GONE
            availableChannelsAdapter.submitList(channels)
        }
    }

    override fun handleFailure(failure: Failure?) {
        updateRefresh(false)
        when (failure) {
            is Failure.ChannelsListIsEmptyError -> {
                hideRefreshing()
                binding.lrChannelsEmpty.visibility = View.VISIBLE
                channelsAdapter.clear()
                val count = this.resources.getQuantityString(R.plurals.channels_count, 0, 0)
                binding.tvChannelsCount.text = count
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

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).connectivityManager.unregisterNetworkCallback(networkCallback)
        _binding = null
    }

    companion object {
        const val TAG = "ChannelsSettingsFragment"
    }
}