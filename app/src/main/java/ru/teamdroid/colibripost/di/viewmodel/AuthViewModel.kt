package ru.teamdroid.colibripost.di.viewmodel

import ru.teamdroid.colibripost.domain.account.auth.CheckAuthCode
import ru.teamdroid.colibripost.domain.account.auth.InsertPhoneNumber
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.other.SingleLiveData
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    val checkAuthCodeUseCase: CheckAuthCode,
    val insertPhoneNumberUseCase:InsertPhoneNumber
): BaseViewModel(){

    val codeData: SingleLiveData<None> = SingleLiveData()
    val insertPhoneData: SingleLiveData<None> = SingleLiveData()

    fun insertCode(code: String) {
        updateRefreshing(true)
        checkAuthCodeUseCase(code) {it.either(::handleFailure) {handleCheckAuthCode(it)} }
    }

    fun insertPhoneNumber(phone: String){
        updateRefreshing(true)
        insertPhoneNumberUseCase(phone){it.either(::handleFailure) {handleInsertPhoneNumber(it)} }
    }

    private fun handleCheckAuthCode(none: None){
        codeData.value = none
    }

    private fun handleInsertPhoneNumber(none: None){
        insertPhoneData.value = none
    }

    override fun onCleared() {
        super.onCleared()
        checkAuthCodeUseCase.unsubscribe()
        insertPhoneNumberUseCase.unsubscribe()
    }
}