package ru.teamdroid.colibripost.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.calendar_view.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.MainActivity
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.FragmentMainBinding
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
import ru.teamdroid.colibripost.ui.main.calendar.Week
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject

class MainFragment : BaseFragment() {

    @Inject
    lateinit var chatsRequests: ChatsRequests

    @Inject
    lateinit var messages: Messages

    lateinit var postViewModel: PostViewModel

    lateinit var channelsViewModel: ChannelsViewModel

    private lateinit var schedulePostsRView: RecyclerView
    private lateinit var calendar: ru.teamdroid.colibripost.ui.main.calendar.CalendarView

    private lateinit var setUpDays:(week:Week, postExisting:List<Boolean>)->Unit

    val postAdapter = PostAdapter()

    override val layoutId = R.layout.fragment_main
    override val toolbarTitle = R.string.splash_colibri_post

    private var times:List<Long> = listOf()
    private var isScreenLaunchLoad:Boolean = true
    private var currentWeek: Week = Week(1)
    private var indicateCount: Int = 0

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).setSupportActionBar(scheduleToolbar )

        (requireActivity() as MainActivity).setSupportActionBar(scheduleToolbar)
        toolbar_layout.title = getString(R.string.splash_colibri_post)

        binding.appBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout!!.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true
                    toolbar_layout.isTitleEnabled = true
                    filterRelativeLayout.visibility = View.GONE
                } else if(isShow){
                    isShow = false
                    toolbar_layout.isTitleEnabled = false
                    //channelFilterTextView.isEnabled = true
                    filterRelativeLayout.visibility = View.VISIBLE
                }
            }
        })


        binding.calendarView.adapter.loadPostsByData = {
            channelsViewModel.getAddedChannels()}
        binding.calendarView.adapter.remoteIndicateDaysOfWeek = {times, setUpDays ->
            if(isScreenLaunchLoad){
                this.times = times
                this.setUpDays = setUpDays
                channelsViewModel.getAddedChannelsForWeek()
            } else {
                /*val week1 = currentWeek.toString()
                val week2 = calendar.adapter.currentWeek.toString()
                if(indicateCount == 0 && week1.equals(week2)){*/

                this.times = times
                this.setUpDays = setUpDays
                indicateCount++
                lifecycleScope.launch{ channelsViewModel.getAddedChannelsForWeek() }
                    /*}else {
                        calendar.adapter.currentWeek = calendar.adapter.currentWeek.previousWeek()
                        calendar.adapter
                        indicateCount = 0
                    }*/
            }
        }

        postViewModel = viewModel {
            onSuccess(postsData, ::handleSchedulePosts)
            onSuccess(weekExistData, ::handlePostsExisting)
            onFailure(failureData, ::handleFailure)
        }

        channelsViewModel = viewModel {
            onSuccess(addedChannelsData, ::handleAddedChannels)
            onSuccess(weekAddedChannelsData, ::handleAddedChannelsForWeek)
            onSuccess(getPostsChannelPhotoData, ::handlePostsChannelPhoto)
            onSuccess(progressData, ::updateRefresh)
            onFailure(failureData, ::handleFailure)
        }

        schedulePostsRView =binding.rvSchedulePosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }
    }

    //region Handle events

    private fun handleAddedChannels(channels: List<ChannelEntity>?) {
        val time = binding.calendarView.selectedDay.time
        val date = Date(time)
        postViewModel.getScheduledPosts(channels!!.map { it.chatId }, time, getDay(date), getMonth(date), getYear(date))
    }

    private fun handleAddedChannelsForWeek(channels: List<ChannelEntity>?){
        postViewModel.getPostExistingOnDay(channels!!.map { it.chatId }, times)
    }

    private fun handleSchedulePosts(schedulePosts: List<PostEntity>?){
        channelsViewModel.getPostsChannelPhoto(schedulePosts!!)
    }

    private fun handlePostsExisting(existingPostsOnWeek:List<Boolean>?){
        val week = with(binding){
            calendarView.adapter.getWeek(calendarView.adapter.currentPosition)
        }
        setUpDays(week, existingPostsOnWeek!!)
        if(isScreenLaunchLoad) {
            isScreenLaunchLoad = false
            channelsViewModel.getAddedChannels()
        } else updateRefresh(false)
    }

    fun getDay(scheduleDay: Date):Int{
        val calendar = Calendar.getInstance()
        calendar.time = scheduleDay
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun getMonth(scheduleDay: Date):Int{
        val calendar = Calendar.getInstance()
        calendar.time = scheduleDay
        return calendar.get(Calendar.MONTH)
    }

    fun getYear(scheduleDay: Date):Int{
        val calendar = Calendar.getInstance()
        calendar.time = scheduleDay
        return calendar.get(Calendar.YEAR)
    }

    private fun handlePostsChannelPhoto(schedulePosts: List<PostEntity>?){
        if(schedulePosts != null) {
            val scheduleList: MutableList<PostEntity> = mutableListOf()
            scheduleList.addAll(schedulePosts)
            scheduleList.add(PostEntity())
            binding.tvEmpty.visibility = View.GONE
            postAdapter.submitList(scheduleList)
        }
        updateRefresh(false)
    }

    override fun handleFailure(failure: Failure?) {
        when(failure){
            is Failure.ChannelsListIsEmptyError,
            is Failure.PostsListIsEmptyError -> {
                binding.tvEmpty.visibility = View.VISIBLE
                postAdapter.submitList(listOf())
            }
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