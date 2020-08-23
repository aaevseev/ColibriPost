package ru.teamdroid.colibripost.data

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import ru.teamdroid.colibripost.R
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class AuthHolder @Inject constructor(
    private val client: TelegramClient,
    private val app: Application
) : Client.ResultHandler, AuthListener, CoroutineScope {
    private val quiting: Boolean = false
    private val _authState = MutableLiveData(AuthStates.UNKNOWN)
    val authState: LiveData<AuthStates> get() = _authState
    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO

    companion object{
        const val LOG_LEVEL_ERROR = 1
    }
    init {
        client.client.send(TdApi.SetLogVerbosityLevel(LOG_LEVEL_ERROR),client)
        client.authListener = this
        client.client.send(TdApi.GetAuthorizationState(), client)
    }

    override fun onResult(data: TdApi.Object) {
        when (data.constructor) {
            TdApi.UpdateAuthorizationState.CONSTRUCTOR -> {
                Log.d("AuthHolder", "onResult: UpdateAuthorizationState")
                onAuthorizationStateUpdated((data as TdApi.UpdateAuthorizationState).authorizationState)
            }
            else -> {
                Log.d("AuthHolder", "onResult: else ${data::class.java.simpleName}")
            }
        }
    }

    suspend fun startAuthentication() {
        Log.d("AuthHolder", "startAuthentication called")
        val tdLibParameters = TdApi.TdlibParameters().apply {
            apiId = app.resources.getInteger(R.integer.telegram_api_id)
            apiHash = app.getString(R.string.telegram_api_hash)
            useMessageDatabase = true
            useSecretChats = true
            systemLanguageCode = Locale.getDefault().language
            databaseDirectory = app.filesDir.absolutePath
            deviceModel = Build.MODEL
            systemVersion = Build.VERSION.RELEASE
            applicationVersion = "0.1"
            enableStorageOptimizer = true
        }

        client.sendFunctionLaunch(
            TdApi.SetTdlibParameters(tdLibParameters)
        )
    }

    suspend fun insertPhoneNumber(phoneNumber: String) {
        Log.d("TelegramClient", "phoneNumber: $phoneNumber")
        val settings = TdApi.PhoneNumberAuthenticationSettings(
            false,
            false,
            false
        )
        client.sendFunctionLaunch(
            TdApi.SetAuthenticationPhoneNumber(phoneNumber, settings)
        )
    }

    suspend fun insertCode(code: String) {
        Log.d("TelegramClient", "code: $code")
        client.sendFunctionLaunch(TdApi.CheckAuthenticationCode(code))
    }

    suspend fun logOut() {
        client.sendFunctionLaunch(TdApi.LogOut())
    }

    override fun onAuthorizationStateUpdated(state: TdApi.AuthorizationState) {
        Log.d("AuthHolder", "onAuthorizationStateUpdated: $state")
        when (state.constructor) {
            TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR -> {
                _authState.postValue(AuthStates.UNAUTHENTICATED)
                launch {
                    startAuthentication()
                }
            }
            TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR -> {
                launch { client.sendFunctionLaunch(TdApi.CheckDatabaseEncryptionKey()) }

                Log.d(
                    "AuthHolder",
                    "onAuthorizationStateUpdated: AuthorizationStateWaitEncryptionKey"
                )
            }
            TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR -> {
                _authState.postValue(AuthStates.WAIT_FOR_NUMBER)
            }
            TdApi.AuthorizationStateWaitCode.CONSTRUCTOR -> {
                _authState.postValue(AuthStates.WAIT_FOR_CODE)
            }

            //не разобрался в каких случаях при авторизации нужен пароль
            TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR -> {
                Log.d("AuthHolder", "AuthorizationStateWaitPassword")
                _authState.postValue(AuthStates.WAIT_FOR_PASSWORD)
            }
            TdApi.AuthorizationStateReady.CONSTRUCTOR -> {
                Log.d("AuthHolder", "AuthorizationStateReady")
                _authState.postValue(AuthStates.AUTHENTICATED)
            }
            TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR -> {
                Log.d("AuthHolder", "AuthorizationStateLoggingOut")
                _authState.postValue(AuthStates.UNAUTHENTICATED)
            }
            TdApi.AuthorizationStateClosing.CONSTRUCTOR -> {
                Log.d("AuthHolder", "AuthorizationStateClosing")
            }
            TdApi.AuthorizationStateClosed.CONSTRUCTOR -> {
                Log.d("AuthHolder", "AuthorizationStateClosed")
                if (!quiting) {
                    client.create()
                    // recreate client after previous has closed
                } else {
                    this.coroutineContext.cancel()
                }
            }
            else -> Log.d(
                "AuthHolder",
                "Unhandled authorizationState with data: $state."
            )
        }
    }


}