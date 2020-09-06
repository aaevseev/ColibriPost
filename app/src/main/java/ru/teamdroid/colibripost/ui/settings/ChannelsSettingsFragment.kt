package ru.teamdroid.colibripost.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.di.viewmodel.ChannelsViewModel
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.other.SingleLiveData
import ru.teamdroid.colibripost.other.onFailure
import ru.teamdroid.colibripost.other.onSuccess
import ru.teamdroid.colibripost.ui.core.BaseFragment

class ChannelsSettingsFragment: BaseFragment() {

    lateinit var channelsViewModel: ChannelsViewModel

    override val layoutId = R.layout.fragment_channels_settings

    private lateinit var recyclerView: RecyclerView
    protected lateinit var lm: RecyclerView.LayoutManager

    val viewAdapter = ChannelsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle(getString(R.string.channels))


        recyclerView =view.findViewById<RecyclerView>(R.id.rvChannels).apply{
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }
        recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))

        channelsViewModel = viewModel {
            onSuccess<List<ChannelEntity>, SingleLiveData<List<ChannelEntity>>>(channelsData, ::handleChannels)
            onFailure<SingleLiveData<Failure>>(failureData, ::handleFailure)
        }

        channelsViewModel.getChannels()

    }

    private fun handleChannels(channels: List<ChannelEntity>?){
        if(channels != null){
            viewAdapter.submitList(channels)
        }
    }

    companion object {
        const val TAG = "ChannelsSettingsFragment"
    }
}