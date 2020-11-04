package ru.teamdroid.colibripost.di.viewmodel


import ru.teamdroid.colibripost.domain.account.AccountEntity
import ru.teamdroid.colibripost.domain.account.GetAccount
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.other.SingleLiveData
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    val getAccountUseCase: GetAccount
) : BaseViewModel() {

    var accountData: SingleLiveData<AccountEntity> = SingleLiveData()

    fun getAccount() {
        updateRefreshing(true)
        getAccountUseCase(None()) { it.either(::handleFailure) { handleAccount(it) } }
    }

    private fun handleAccount(accountEntity: AccountEntity) {
        accountData.value = accountEntity
    }

    override fun onCleared() {
        super.onCleared()
        getAccountUseCase.unsubscribe()
    }
}