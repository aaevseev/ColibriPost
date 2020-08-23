package ru.teamdroid.colibripost.presentation.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.TdApi
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.data.Chats
import ru.teamdroid.colibripost.data.Messages
import ru.teamdroid.colibripost.domain.ChannelEntity
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val channels: List<ChannelEntity> =
                chats.getChannelsFullInfo()
            channels.forEach {
                Log.d("MainFragment", "title: ${it.title} description: ${it.description} count ${it.memberCount}")
            }
        }
    }

    companion object {
        const val TAG = "MainFragment"
    }
}