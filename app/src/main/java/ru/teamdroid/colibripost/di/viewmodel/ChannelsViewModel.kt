package ru.teamdroid.colibripost.di.viewmodel

import ru.teamdroid.colibripost.domain.channels.*
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.other.SingleLiveData
import javax.inject.Inject

class ChannelsViewModel @Inject constructor(
    val getAddedChannelsUseCase: GetAddedChannels,
    val getAvailableChannelsUseCase: GetAvailableChannels,
    val setChannelsUseCase: SetChannels,
    val deleteChannelUseCase: DeleteChannel
) : BaseViewModel() {

    var addedChannelsData: SingleLiveData<List<ChannelEntity>> = SingleLiveData()
    var avChannelsData: SingleLiveData<List<ChannelEntity>> = SingleLiveData()
    var setChannelsData: SingleLiveData<None> = SingleLiveData()
    var deleteChannelData: SingleLiveData<None> = SingleLiveData()

    fun getAddedChannels() {
        updateRefreshing(true)
        getAddedChannelsUseCase(None()) { it.either(::handleFailure) { handleAddedChannels(it) } }
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

    fun setChannels(channels: List<ChannelEntity>) {
        setChannelsUseCase(channels) { it.either(::handleFailure) { handleSetChannels(it) } }
    }

    fun deleteChannel(idChannel: Long) {
        deleteChannelUseCase(idChannel) { it.either(::handleFailure) { handleDeleteChannel(it) } }
    }


    private fun handleAddedChannels(channels: List<ChannelEntity>) {
        addedChannelsData.value = channels
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

    override fun onCleared() {
        super.onCleared()
        getAddedChannelsUseCase.unsubscribe()
        getAvailableChannelsUseCase.unsubscribe()
        setChannelsUseCase.unsubscribe()
        deleteChannelUseCase.unsubscribe()
    }
}