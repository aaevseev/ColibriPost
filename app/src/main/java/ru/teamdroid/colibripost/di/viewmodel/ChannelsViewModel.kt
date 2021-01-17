package ru.teamdroid.colibripost.di.viewmodel

import ru.teamdroid.colibripost.domain.channels.*
import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.other.SingleLiveData
import java.util.concurrent.Executors
import javax.inject.Inject

class ChannelsViewModel @Inject constructor(
    private val getAddedChannelsUseCase: GetAddedChannels,
    private val getAvailableChannelsUseCase: GetAvailableChannels,
    private val setChannelsUseCase: SetChannels,
    private val deleteChannelUseCase: DeleteChannel,
    private val getPostsChannelPhotoUseCase: GetPostsChannelsPhoto
) : BaseViewModel() {

    var addedChannelsData: SingleLiveData<List<ChannelEntity>> = SingleLiveData()
    var weekAddedChannelsData: SingleLiveData<List<ChannelEntity>> = SingleLiveData()
    var avChannelsData: SingleLiveData<List<ChannelEntity>> = SingleLiveData()
    var setChannelsData: SingleLiveData<None> = SingleLiveData()
    var deleteChannelData: SingleLiveData<None> = SingleLiveData()
    var getPostsChannelPhotoData: SingleLiveData<List<PostEntity>> = SingleLiveData()

    fun getAddedChannels() {
        updateRefreshing(true)
        getAddedChannelsUseCase(None()) { it.either(::handleFailure) { handleAddedChannels(it) } }
    }

    fun getAddedChannelsForWeek() {
        getAddedChannelsUseCase(None()) { it.either(::handleFailure) { handleWeekAddedChannels(it) } }
    }


    fun getAvChannels() {
        getAvailableChannelsUseCase(None()) {
            it.either(::handleFailure) {
                handleAvailableChannels(
                    it
                )
            }
        }
    }

    fun getPostsChannelPhoto(posts:List<PostEntity>){
        getPostsChannelPhotoUseCase(posts){it.either(::handleFailure) {
            handlePostsChannelPhoto(it)
        } }
    }

    fun setChannels(channels: List<ChannelEntity>) {
        setChannelsUseCase(channels) { it.either(::handleFailure) { handleSetChannels(it) } }
    }

    fun deleteChannel(idChannel: Long) {
        deleteChannelUseCase(idChannel) { it.either(::handleFailure) { handleDeleteChannel(it) } }
    }


    private fun handleAddedChannels(channels: List<ChannelEntity>) {
        addedChannelsData.value = channels
    }

    private fun handleWeekAddedChannels(channels: List<ChannelEntity>) {
        weekAddedChannelsData.value = channels
    }

    private fun handleAvailableChannels(channels: List<ChannelEntity>) {
        avChannelsData.value = channels
    }

    private fun handleSetChannels(none: None) {
        setChannelsData.value = none
    }

    private fun handleDeleteChannel(none: None) {
        deleteChannelData.value = none
    }

    private fun handlePostsChannelPhoto(posts:List<PostEntity>){
        getPostsChannelPhotoData.value = posts
    }

    override fun onCleared() {
        super.onCleared()
        getAddedChannelsUseCase.unsubscribe()
        getAvailableChannelsUseCase.unsubscribe()
        setChannelsUseCase.unsubscribe()
        deleteChannelUseCase.unsubscribe()
        getPostsChannelPhotoUseCase.unsubscribe()
    }
}