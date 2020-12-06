package ru.teamdroid.colibripost.ui.main

import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_bottom_navigation.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.MainActivity
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.di.viewmodel.ChannelsViewModel
import ru.teamdroid.colibripost.di.viewmodel.PostViewModel
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.other.onFailure
import ru.teamdroid.colibripost.other.onSuccess
import ru.teamdroid.colibripost.remote.Messages
import ru.teamdroid.colibripost.remote.channels.ChatsRequests
import ru.teamdroid.colibripost.ui.core.BaseFragment
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
    override val toolbarTitle = R.string.splash_colibri_post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).setSupportActionBar(scheduleToolbar )

        (requireActivity() as MainActivity).setSupportActionBar(scheduleToolbar)
        toolbar_layout.title = getString(R.string.splash_colibri_post)

        appBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout!!.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true
                    toolbar_layout.isTitleEnabled = true
                } else if(isShow){
                    isShow = false
                    toolbar_layout.isTitleEnabled = false
                }
            }
        })

        postViewModel = viewModel {
            onSuccess(postsData, ::handleSchedulePosts)
            onFailure(failureData, ::handleFailure)
        }

        channelsViewModel = viewModel {
            onSuccess(addedChannelsData, ::handleAddedChannels)
            onSuccess(getPostsChannelPhotoData, ::handlePostsChannelPhoto)
            onSuccess(progressData, ::updateRefresh)
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

    private fun handleSchedulePosts(schedulePosts: List<PostEntity>?){
        channelsViewModel.getPostsChannelPhoto(schedulePosts!!)
    }

    private fun handlePostsChannelPhoto(schedulePosts: List<PostEntity>?){
        if(schedulePosts != null) {
            val scheduleList: MutableList<PostEntity> = mutableListOf()
            //scheduleList.add(PostEntity())
            scheduleList.addAll(schedulePosts)
            //scheduleList.add(PostEntity())
            tvEmpty.visibility = View.GONE
            postAdapter.submitList(scheduleList)
        }
        updateRefresh(false)
    }

    override fun handleFailure(failure: Failure?) {
        when(failure){
            is Failure.ChannelsListIsEmptyError -> Toast.makeText(requireContext(), "добавь каналы",
                    Toast.LENGTH_SHORT).show()
            is Failure.PostsListIsEmptyError -> tvEmpty.visibility = View.VISIBLE
            else -> super.handleFailure(failure)
        }
        updateRefresh(false)

    }

    override fun updateRefresh(status: Boolean?) {
        if (status == true) {
            postProgressBar.visibility = View.VISIBLE
        } else {
            postProgressBar.visibility = View.GONE
        }
    }

    //endregion

    override fun onDestroyView() {
        super.onDestroyView()
        base { setMainToolbar() }
    }



    companion object {
        const val TAG = "MainFragment"
    }
}