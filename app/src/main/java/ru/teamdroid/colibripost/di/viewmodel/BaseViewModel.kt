package ru.teamdroid.colibripost.di.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.other.SingleLiveData

open class BaseViewModel: ViewModel(){

    var failureData : SingleLiveData<Failure> = SingleLiveData()

    var progressData: SingleLiveData<Boolean> = SingleLiveData()

    open fun handleFailure(failure: Failure){
        this.failureData.value = failure
    }

    protected fun updateProgress(progress: Boolean){
        this.progressData.value = progress
    }

}