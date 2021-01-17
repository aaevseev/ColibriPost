package ru.teamdroid.colibripost.di.viewmodel

import kotlinx.coroutines.delay
import ru.teamdroid.colibripost.domain.post.CheckPostsOnWeek
import ru.teamdroid.colibripost.domain.post.GetChatScheduledMessages
import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.other.SingleLiveData
import javax.inject.Inject

class PostViewModel @Inject constructor(
        private val getChatScheduledMessagesUseCase:GetChatScheduledMessages,
        private val checkPostsOnWeekUseCase: CheckPostsOnWeek
) : BaseViewModel(){

    var postsData: SingleLiveData<List<PostEntity>> = SingleLiveData()
    var weekExistData: SingleLiveData<List<Boolean>> = SingleLiveData()

    fun getScheduledPosts(chatIds:List<Long>, calendarDay:Long, day:Int, month:Int, year:Int){
        updateRefreshing(true)
        getChatScheduledMessagesUseCase(GetChatScheduledMessages.Params(chatIds, calendarDay, day, month, year)){ it ->
            it.either(::handleFailure) {handlePosts(it)}
        }
    }

    fun getPostExistingOnDay(chatIds: List<Long>, times:List<Long>){
        checkPostsOnWeekUseCase(CheckPostsOnWeek.Params(chatIds, times)) {
            it.either(::handleFailure) {handlePostsExisting(it)}
        }
    }

    fun handlePosts(posts: List<PostEntity>){
        postsData.value = posts
    }

    fun handlePostsExisting(existingPostsOnWeek:List<Boolean>){
        weekExistData.value = existingPostsOnWeek
    }

    override fun onCleared() {
        super.onCleared()
        getChatScheduledMessagesUseCase.unsubscribe()
        checkPostsOnWeekUseCase.unsubscribe()
    }
}