package ru.teamdroid.colibripost.di.viewmodel

import kotlinx.coroutines.delay
import ru.teamdroid.colibripost.domain.post.*
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.other.SingleLiveData
import javax.inject.Inject

class PostViewModel @Inject constructor(
        private val getChatScheduledMessagesUseCase:GetChatScheduledMessages,
        private val checkPostsOnWeekUseCase: CheckPostsOnWeek,
        private val deleteSchedulePostUseCase:DeleteSchedulePost,
        private val getScheduledAlbumPostUseCase: GetScheduledAlbumPost,
        private val duplicateSchedulePostUseCase: DuplicatePost
) : BaseViewModel(){

    var postsData: SingleLiveData<List<PostEntity>> = SingleLiveData()
    var postAlbumData: SingleLiveData<List<PostEntity>> = SingleLiveData()
    var postDuplicateData: SingleLiveData<None> = SingleLiveData()
    var weekExistData: SingleLiveData<List<Boolean>> = SingleLiveData()
    var deleteSchedulePostData:SingleLiveData<None> = SingleLiveData()

    fun getScheduledPosts(chatIds:List<Long>, calendarDay:Long, day:Int, month:Int, year:Int, channelsIds:List<Long>){
        updateRefreshing(true)
        getChatScheduledMessagesUseCase(GetChatScheduledMessages.Params(chatIds, calendarDay, day, month, year, channelsIds)){ it ->
            it.either(::handleFailure) {handlePosts(it)}
        }
    }

    fun getPostExistingOnDay(chatIds: List<Long>, times:List<Long>){
        checkPostsOnWeekUseCase(CheckPostsOnWeek.Params(chatIds, times)) {
            it.either(::handleFailure) {handlePostsExisting(it)}
        }
    }

    fun deletePost(post:PostEntity){
        deleteSchedulePostUseCase(DeleteSchedulePost.Params(post)) {
            it.either(::handleFailure) {handleDeletePost(it)}
        }
    }

    fun duplicatePost(posts: List<PostEntity>){
        duplicateSchedulePostUseCase(DuplicatePost.Params(posts)){
            it.either(::handleFailure) {handleDuplicatePost(it)}
        }
    }

    fun getScheduledAlbumPost(post: PostEntity){
        getScheduledAlbumPostUseCase(GetScheduledAlbumPost.Params(post)){
            it.either(::handleFailure){handleAlbumPost(it)}
        }
    }

    fun handlePosts(posts: List<PostEntity>){
        postsData.value = posts
    }

    fun handleAlbumPost(posts: List<PostEntity>){
        postAlbumData.value = posts
    }

    fun handlePostsExisting(existingPostsOnWeek:List<Boolean>){
        weekExistData.value = existingPostsOnWeek
    }

    fun handleDeletePost(none:None){
        deleteSchedulePostData.value = none
    }

    fun handleDuplicatePost(none: None){
        postDuplicateData.value = none
    }


    override fun onCleared() {
        super.onCleared()
        getChatScheduledMessagesUseCase.unsubscribe()
        checkPostsOnWeekUseCase.unsubscribe()
        deleteSchedulePostUseCase.unsubscribe()
        getScheduledAlbumPostUseCase.unsubscribe()
        duplicateSchedulePostUseCase.unsubscribe()
    }
}