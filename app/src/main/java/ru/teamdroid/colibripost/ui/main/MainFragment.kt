package ru.teamdroid.colibripost.ui.main

import android.os.Bundle
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.remote.channels.ChatsRequests
import ru.teamdroid.colibripost.remote.Messages
import ru.teamdroid.colibripost.ui.core.BaseFragment
import javax.inject.Inject

class MainFragment : BaseFragment() {

    @Inject
    lateinit var chatsRequests: ChatsRequests

    @Inject
    lateinit var messages: Messages

    override val layoutId = R.layout.fragment_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.inject(this)
    }

    companion object {
        const val TAG = "MainFragment"
    }
}