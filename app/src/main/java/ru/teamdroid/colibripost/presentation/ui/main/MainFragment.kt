package ru.teamdroid.colibripost.presentation.ui.main

import android.os.Bundle
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.data.Chats
import ru.teamdroid.colibripost.data.Messages
import ru.teamdroid.colibripost.presentation.ui.core.BaseFragment
import javax.inject.Inject

class MainFragment : BaseFragment() {

    @Inject
    lateinit var chats: Chats

    @Inject
    lateinit var messages: Messages

    override val layoutId = R.layout.fragment_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectMainFragment(this)
    }

    companion object {
        const val TAG = "MainFragment"
    }
}