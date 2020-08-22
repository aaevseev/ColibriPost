package ru.teamdroid.colibripost.presentation.ui.settings

import android.os.Bundle
import android.view.View
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.presentation.ui.core.BaseFragment

class ChannelsSettingsFragment: BaseFragment() {

    override val layoutId = R.layout.fragment_channels_settings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle(getString(R.string.channels))
    }

    companion object {
        const val TAG = "ChannelsSettingsFragment"
    }
}