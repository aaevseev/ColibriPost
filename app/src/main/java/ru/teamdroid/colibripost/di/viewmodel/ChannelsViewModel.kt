package ru.teamdroid.colibripost.di.viewmodel

import androidx.lifecycle.MutableLiveData
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.domain.channels.GetChannels
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.other.SingleLiveData
import javax.inject.Inject

class ChannelsViewModel @Inject constructor(
    val getChannelsUseCase: GetChannels
) : BaseViewModel(){

    var channelsData: SingleLiveData<List<ChannelEntity>> = SingleLiveData()

    fun getChannels() {
        getChannelsUseCase(None()){it.either(::handleFailure) {handleChannels(it)} }
    }

    fun handleChannels(channels: List<ChannelEntity>){
        channelsData.value = channels
    }

    override fun onCleared() {
        super.onCleared()
        getChannelsUseCase.unsubscribe()
    }
}