package ru.teamdroid.colibripost.ui.main

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.pchmn.materialchips.model.Chip
import kotlinx.android.synthetic.main.channel_dropdown_item.view.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.item_av_channel.view.*
import kotlinx.android.synthetic.main.item_av_channel.view.imgCheck
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
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.other.onFailure
import ru.teamdroid.colibripost.other.onSuccess
import ru.teamdroid.colibripost.remote.Messages
import ru.teamdroid.colibripost.remote.channels.ChatsRequests
import ru.teamdroid.colibripost.ui.core.BaseFragment
import ru.teamdroid.colibripost.ui.core.CircleTransform
import ru.teamdroid.colibripost.ui.core.PicassoHelper
import ru.teamdroid.colibripost.ui.core.getColorFromResource
import ru.teamdroid.colibripost.ui.main.calendar.Week
import java.util.*
import java.util.concurrent.ExecutorService
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

    private lateinit var channelChipList: MutableList<ChannelChip>
    private lateinit var channelFilterList: MutableList<ChannelEntity>
    private lateinit var channelChipsFilterList: MutableList<View>

    private val postAdapter : PostAdapter by lazy {
         PostAdapter(
                 deleteSchedulePost = {
                     deleteSchedulePost(it)
                 },
                 duplicateSchedulePost = {
                     duplicateSchedulePost(it)
                 }
         )
    }

    override val layoutId = R.layout.fragment_main
    override val toolbarTitle = R.string.splash_colibri_post

    private var times:List<Long> = listOf()
    private var isScreenLaunchLoad:Boolean = true
    private var isFirstSpinnerLoad:Boolean = true

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    val myService: ExecutorService = Executors.newFixedThreadPool(3)

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

        channelFilterList = mutableListOf()
        channelChipsFilterList = mutableListOf()

        binding.appBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout!!.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true
                    binding.toolbarLayout.isTitleEnabled = true
                    binding.filterRelativeLayout.visibility = View.GONE
                } else if(isShow){
                    isShow = false
                    binding.toolbarLayout.isTitleEnabled = false
                    //channelFilterTextView.isEnabled = true
                    binding.filterRelativeLayout.visibility = View.VISIBLE
                }
            }
        })

        binding.calendarView.adapter.loadPostsByData = {
            channelsViewModel.getAddedChannels()
        }
        binding.calendarView.adapter.remoteIndicateDaysOfWeek = {times, setUpDays ->
                this.times = times
                this.setUpDays = setUpDays
                myService.submit {
                    channelsViewModel.getAddedChannelsForWeek()
                }

        }

        binding.dropdownSpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
            }

        }

        binding.ivChannelDropDown.setOnClickListener {
            binding.dropdownSpinner.performClick()
        }

        channelChipList = mutableListOf()

        postViewModel = viewModel {
            onSuccess(postsData, ::handleSchedulePosts)
            onSuccess(weekExistData, ::handlePostsExisting)
            onSuccess(deleteSchedulePostData, ::handleDeletePost)
            onSuccess(postAlbumData, ::handleAlbumPost)
            onSuccess(postDuplicateData, ::handleDuplicatePost)
            onFailure(failureData, ::handleFailure)
        }

        channelsViewModel = viewModel {
            onSuccess(addedChannelsData, ::handleAddedChannels)
            onSuccess(weekAddedChannelsData, ::handleAddedChannelsForWeek)
            onSuccess(chipChannelsData, ::handleChipChannels)
            onSuccess(getPostsChannelPhotoData, ::handlePostsChannelPhoto)
            onSuccess(progressData, ::updateRefresh)
            onFailure(failureData, ::handleFailure)
        }

        channelsViewModel.getAddedChannelsForChips()

        schedulePostsRView =binding.rvSchedulePosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }
    }

    private fun deleteSchedulePost(post:PostEntity){
        postViewModel.deletePost(post)
    }

    private fun duplicateSchedulePost(post: PostEntity){
        postViewModel.getScheduledAlbumPost(post)
    }

    //region Filter UI

    fun onSpinnerChannelSelected(position:Int){
        Log.d("CheckSpinner", "Click")
        if(binding.dropdownSpinner.adapter != null) {
            Log.d("CheckSpinner", "Adapter not Null")
            if(binding.dropdownSpinner.adapter.count != 0){
                Log.d("CheckSpinner", isFirstSpinnerLoad.toString())
/*                if(!isFirstSpinnerLoad) {

                }*/
                addChannelChip((binding.dropdownSpinner.adapter.getItem(position)) as ChannelEntity)
            }
            //isFirstSpinnerLoad = false
        }
    }

    fun addChannelChip(channel:ChannelEntity){
        channelFilterList.add(channel)
        val chip = com.google.android.material.chip.Chip(binding.channelChips.context)
        channelChipsFilterList.add(chip)
        with(chip){
            text = channel.title
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT )
            isCloseIconVisible = true
            chipBackgroundColor = AppCompatResources.getColorStateList(requireContext(), R.color.accent)
            setTextColor(AppCompatResources.getColorStateList(requireContext(), R.color.white))
            val bitmap = Drawable.createFromPath(channel.photoPath)?.toBitmap()
            val drawable = CircleTransform().transform(bitmap!!).toDrawable(resources)
            chipIcon = drawable
            closeIconTint = AppCompatResources.getColorStateList(requireContext(), R.color.white)
            isClickable = true
            isFocusable = true
            isCheckable = false
            setOnCloseIconClickListener {
                removeChannelChip(chip, it, channel)
            }
            binding.tvFilterHint.visibility = View.GONE
            binding.channelChips.addView(this)
            channelsViewModel.getAddedChannels()
        }
    }

    fun setCheckedOrNotBackground(root: RelativeLayout, isChecked:Boolean){
        root.apply {
            if (isChecked) {
                this.imgCheck.visibility = View.VISIBLE
                this.title.setTextColor(root.context.getColorFromResource(R.color.white))
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.accent)
            } else{
                this.imgCheck.visibility = View.GONE
                this.title.setTextColor(root.context.getColorFromResource(R.color.text))
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
            }
        }
    }

    fun removeChannelChipFromAdapter(channel:ChannelEntity){
        Log.d("Check", "OnCloseIcon: " + channelFilterList.toString())
        val chip = channelChipsFilterList.get(channelFilterList.indexOf(channel))
        binding.channelChips.removeView(chip)
        channelFilterList.remove(channel)
        channelChipsFilterList.remove(chip)
        if(channelFilterList.size == 0) binding.tvFilterHint.visibility = View.VISIBLE
        channelsViewModel.getAddedChannels()
    }

    fun removeChannelChip(chip: com.google.android.material.chip.Chip, view: View, channel: ChannelEntity){
        Log.d("Check", "OnCloseIcon")
        binding.channelChips.removeView(view)
        channelFilterList.remove(channel)
        channelChipsFilterList.remove(chip)

        val currentPosition = (binding.dropdownSpinner.adapter as ChannelChipsAdapter).posts.indexOf(channel)
        binding.dropdownSpinner.setSelection(currentPosition)
        (binding.dropdownSpinner.adapter as ChannelChipsAdapter).checked[currentPosition] = false
        val root = binding.dropdownSpinner.selectedView as RelativeLayout
        setCheckedOrNotBackground(root, false)

        if(channelFilterList.size == 0) binding.tvFilterHint.visibility = View.VISIBLE
        channelsViewModel.getAddedChannels()
    }

    //endregion

    //region Handle events

    private fun handleAddedChannels(channels: List<ChannelEntity>?) {
        val time = binding.calendarView.selectedDay.time
        val date = Date(time)
        Log.d("CheckSpinner", channelFilterList.toString())
        postViewModel.getScheduledPosts(channels!!.map { it.chatId }, time, getDay(date), getMonth(date), getYear(date), channelFilterList.map { it.chatId })
    }

    private fun handleAddedChannelsForWeek(channels: List<ChannelEntity>?){
        Log.d("Channels", "Handle Channels")
        postViewModel.getPostExistingOnDay(channels!!.map { it.chatId }, times)
    }

    private fun handleAlbumPost(posts: List<PostEntity>?){
        postViewModel.duplicatePost(posts!!)
    }

    private fun handleDuplicatePost(none: None?){
        channelsViewModel.getAddedChannels()
    }

    private fun handleChipChannels(channels: List<ChannelEntity>?){
        binding.dropdownSpinner.adapter = if (channels != null) ChannelChipsAdapter(requireContext(), R.layout.channel_dropdown_item,
                channels, BooleanArray(channels.size), ::onSpinnerChannelSelected, ::removeChannelChipFromAdapter) else null
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

    private fun handleDeletePost(none:None?){
        channelsViewModel.getAddedChannels()
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