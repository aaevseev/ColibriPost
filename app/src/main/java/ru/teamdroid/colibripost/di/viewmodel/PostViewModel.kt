package ru.teamdroid.colibripost.di.viewmodel

import ru.teamdroid.colibripost.domain.post.GetChatScheduledMessages
import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.other.SingleLiveData
import javax.inject.Inject

class PostViewModel @Inject constructor(
        private val getChatScheduledMessagesUseCase:GetChatScheduledMessages
) : BaseViewModel(){

    var postsData: SingleLiveData<List<PostEntity>> = SingleLiveData()

    fun getScheduledPosts(chatIds:List<Long>, calendarDay:Long){
        updateRefreshing(true)
        getChatScheduledMessagesUseCase(GetChatScheduledMessages.Params(chatIds, calendarDay)){ it ->
            it.either(::handleFailure) {handlePosts(it)}
        }
    }

    fun handlePosts(posts: List<PostEntity>){
        postsData.value = posts
    }

    override fun onCleared() {
        super.onCleared()
        getChatScheduledMessagesUseCase.unsubscribe()
    }
}