package ru.teamdroid.colibripost.di.viewmodel

import ru.teamdroid.colibripost.domain.account.auth.CheckAuthCode
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.other.SingleLiveData
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    val checkAuthCodeUseCase: CheckAuthCode
): BaseViewModel(){

    val codeData: SingleLiveData<None> = SingleLiveData()

    fun insertCode(code: String) {
        updateRefreshing(true)
        checkAuthCodeUseCase(code) {it.either(::handleFailure) {handleCheckAuthCode(it)} }
    }

    fun handleCheckAuthCode(none: None){
        codeData.value = none
    }

    override fun onCleared() {
        super.onCleared()
        checkAuthCodeUseCase.unsubscribe()
    }
}