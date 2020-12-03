package ru.teamdroid.colibripost.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.di.viewmodel.ChannelsViewModel
import ru.teamdroid.colibripost.di.viewmodel.PostViewModel
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.other.SingleLiveData
import ru.teamdroid.colibripost.other.onFailure
import ru.teamdroid.colibripost.other.onSuccess
import ru.teamdroid.colibripost.remote.Messages
import ru.teamdroid.colibripost.remote.channels.ChatsRequests
import ru.teamdroid.colibripost.ui.core.BaseFragment
import ru.teamdroid.colibripost.ui.settings.channels.AvailableChannelsAdapter
import javax.inject.Inject

class MainFragment : BaseFragment() {

    @Inject
    lateinit var chatsRequests: ChatsRequests

    @Inject
    lateinit var messages: Messages

    lateinit var postViewModel: PostViewModel

    lateinit var channelsViewModel: ChannelsViewModel

    private lateinit var schedulePostsRView: RecyclerView
    val postAdapter = PostAdapter()

    override val layoutId = R.layout.fragment_main
    override val toolbarTitle = R.string.main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postViewModel = viewModel {
            onSuccess(postsData, ::handleSchedulePosts)
            onFailure(failureData, ::handleFailure)
        }

        channelsViewModel = viewModel {
            onSuccess(addedChannelsData, ::handleAddedChannels)
            onSuccess(getPostsChannelPhotoData, ::handlePostsChannelPhoto)
            onFailure(failureData, ::handleFailure)
        }

        schedulePostsRView = view.findViewById<RecyclerView>(R.id.rvSchedulePosts).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }

        channelsViewModel.getAddedChannels()
    }

    //region Handle events

    private fun handleAddedChannels(channels: List<ChannelEntity>?) {
        postViewModel.getScheduledPosts(channels!!.map { it.chatId })
    }

    private fun handleSchedulePosts(schedulePosts:List<PostEntity>?){
        channelsViewModel.getPostsChannelPhoto(schedulePosts!!)
    }

    private fun handlePostsChannelPhoto(schedulePosts:List<PostEntity>?){
        Toast.makeText(requireContext(), "Отлож. Посты: ${schedulePosts!!.size}", Toast.LENGTH_SHORT).show()
        if(schedulePosts != null) {
            val scheduleList: MutableList<PostEntity> = mutableListOf()
            scheduleList.add(PostEntity())
            scheduleList.addAll(schedulePosts)
            scheduleList.add(PostEntity())
            postAdapter.submitList(scheduleList)
        }
    }

    override fun handleFailure(failure: Failure?) {
        when(failure){
            is Failure.ChannelsListIsEmptyError -> Toast.makeText(requireContext(), "добавь каналы",
                    Toast.LENGTH_SHORT).show()
            else -> super.handleFailure(failure)
        }

    }

    //endregion

    companion object {
        const val TAG = "MainFragment"
    }
}