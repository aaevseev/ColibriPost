package ru.teamdroid.colibripost.presentation.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.TdApi
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.data.Chats
import ru.teamdroid.colibripost.data.Messages
import ru.teamdroid.colibripost.presentation.ui.core.BaseFragment
import ru.teamdroid.colibripost.presentation.ui.settings.ChannelsSettingsFragment
import javax.inject.Inject

class MainFragment : BaseFragment() {


    @Inject
    lateinit var chats: Chats

    @Inject
    lateinit var messages: Messages

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectMainFragment(this)
    }

    override val layoutId = R.layout.fragment_main

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        send_delayed_message.setOnClickListener {
        }
        get_chats.setOnClickListener {
            lifecycleScope.launch {
                val chats1: List<TdApi.Chat> = chats.getChats()
                chats1.forEach {
                    Log.d("MainFragment", "onViewCreated: chat: ${it.title} id ${it.id}")
                }

            }
        }
        get_Channels.setOnClickListener {
            lifecycleScope.launch {
                val channels: List<TdApi.SupergroupFullInfo> = chats.getChannels()
                channels.forEach {
                    Log.d("MainFragment", "onViewCreated: chat: ${it.description} count ${it.memberCount}")
                }

            }
        }
        get_messages.setOnClickListener {
            lifecycleScope.launch {
                val chats1 = chats.getChats()
                val find = chats1.find { it.id == -1001264815755 }
                Log.d("MainFragment", "onViewCreated: $find")
                if (find != null) {
                    val messages1 = messages.getMessages(find.id)
                    messages1.forEach {
                        Log.d("MainFragment", "onViewCreated: messages ${it.content}")
                    }
                }
            }
        }
        send_message.setOnClickListener {
            val msg = messages.createExtendedMessage()
            lifecycleScope.launch {
                -1001264815755
                val messages = messages.getMessages(-1001264815755, fromMessageId = 0, limit = 10)

                messages.forEachIndexed { index, message ->
                    Log.d("NewPostViewModel", "getMessages: ${message.replyMarkup}")
                }
            }
        }


    }

    companion object {
        const val TAG = "MainFragment"
    }
}