package ru.teamdroid.colibripost.remote.core

import android.util.Log
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import ru.teamdroid.colibripost.remote.auth.AuthListener
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class TelegramClient @Inject constructor() : Client.ResultHandler {
    var client: Client = Client.create(this, null, null)
    var authListener: AuthListener? = null
    override fun onResult(data: TdApi.Object) {
        when (data.constructor) {
            TdApi.UpdateAuthorizationState.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateAuthorizationState")
                authListener?.onAuthorizationStateUpdated((data as TdApi.UpdateAuthorizationState).authorizationState)
            }
            TdApi.UpdateUser.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateUser")
            }
            TdApi.UpdateUserStatus.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateUserStatus")
            }
            TdApi.UpdateBasicGroup.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateBasicGroup")
            }
            TdApi.UpdateSupergroup.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateSupergroup")
            }
            TdApi.UpdateSecretChat.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateSecretChat")
            }
            TdApi.UpdateNewChat.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateNewChat")
            }
            TdApi.UpdateChatTitle.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatTitle")
            }
            TdApi.UpdateChatPhoto.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatPhoto")
            }
            TdApi.UpdateChatChatList.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatChatList")
            }
            TdApi.UpdateChatLastMessage.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatLastMessage")
            }
            TdApi.UpdateChatOrder.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatOrder")
            }
            TdApi.UpdateChatIsPinned.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatIsPinned")
            }
            TdApi.UpdateChatReadInbox.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatReadInbox")
            }
            TdApi.UpdateChatReadOutbox.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatReadOutbox")
            }
            TdApi.UpdateChatUnreadMentionCount.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatUnreadMentionCount")
            }
            TdApi.UpdateMessageMentionRead.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateMessageMentionRead")
            }
            TdApi.UpdateChatReplyMarkup.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatReplyMarkup")
            }
            TdApi.UpdateChatDraftMessage.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatDraftMessage")
            }
            TdApi.UpdateChatNotificationSettings.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatNotificationSettings")
            }
            TdApi.UpdateChatDefaultDisableNotification.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatDefaultDisableNotification")
            }
            TdApi.UpdateChatIsMarkedAsUnread.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatIsMarkedAsUnread")
            }
            TdApi.UpdateChatIsSponsored.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatIsSponsored")
            }
            TdApi.UpdateUserFullInfo.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateUserFullInfo")
            }
            TdApi.UpdateBasicGroupFullInfo.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateBasicGroupFullInfo")
            }
            TdApi.UpdateSupergroupFullInfo.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateSupergroupFullInfo")
            }
            else -> {
                Log.d("TelegramClient", "onResult: else ${data::class.java.simpleName}")
            }
        }
    }


    suspend inline fun <reified ExpectedResult : TdApi.Object> send(function: TdApi.Function):
            ExpectedResult =
        suspendCoroutine { continuation ->
            val resultHandler: (TdApi.Object) -> Unit = { result ->
                when (result) {
                    is ExpectedResult -> continuation.resume(result).also {
                        Log.d("TelegramClient", "send: $result")
                    }
                    is TdApi.Error -> continuation.resumeWithException(
                        TelegramException.Error(result.message)
                    ).also { Log.e("TelegramClient", "send: error ${result.message}") }
                    else -> continuation.resumeWithException(
                        TelegramException.UnexpectedResult(result)
                    ).also { Log.e("TelegramClient", "send: unexpected error $result") }

                }
            }
            client.send(function, resultHandler) { throwable ->
                continuation.resumeWithException(
                    TelegramException.Error(throwable?.message ?: "unknown")
                )
            }
        }

    suspend fun sendFunctionLaunch(function: TdApi.Function) {
        send<TdApi.Ok>(function)
    }

    fun create() {
        client = Client.create(this, null, null)
    }


}