package ru.teamdroid.colibripost.presentation.ui.settings

import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.presentation.ui.core.BaseFragment

class ChannelsSettingsFragment: BaseFragment() {
    override val layoutId = R.layout.fragment_channels_settings
    override val titleToolbar = R.string.channels

    companion object {

        const val TAG = "ChannelsSettingsFragment"
    }

}