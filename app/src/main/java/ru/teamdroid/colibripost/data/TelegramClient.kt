package ru.teamdroid.colibripost.data

import android.util.Log
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import org.drinkless.td.libcore.telegram.TdApi.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


@Singleton
class TelegramClient @Inject constructor() : Client.ResultHandler {
    var client: Client = Client.create(this, null, null)
    var authListener: AuthListener? = null
    override fun onResult(data: Object) {
        when (data.constructor) {
            UpdateAuthorizationState.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateAuthorizationState")
                authListener?.onAuthorizationStateUpdated((data as UpdateAuthorizationState).authorizationState)
            }
            UpdateUser.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateUser")
            }
            UpdateUserStatus.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateUserStatus")
            }
            UpdateBasicGroup.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateBasicGroup")
            }
            UpdateSupergroup.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateSupergroup")
            }
            UpdateSecretChat.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateSecretChat")
            }
            UpdateNewChat.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateNewChat")
            }
            UpdateChatTitle.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatTitle")
            }
            UpdateChatPhoto.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatPhoto")
            }
            UpdateChatChatList.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatChatList")
            }
            UpdateChatLastMessage.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatLastMessage")
            }
            UpdateChatOrder.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatOrder")
            }
            UpdateChatIsPinned.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatIsPinned")
            }
            UpdateChatReadInbox.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatReadInbox")
            }
            UpdateChatReadOutbox.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatReadOutbox")
            }
            UpdateChatUnreadMentionCount.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatUnreadMentionCount")
            }
            UpdateMessageMentionRead.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateMessageMentionRead")
            }
            UpdateChatReplyMarkup.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatReplyMarkup")
            }
            UpdateChatDraftMessage.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatDraftMessage")
            }
            UpdateChatNotificationSettings.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatNotificationSettings")
            }
            UpdateChatDefaultDisableNotification.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatDefaultDisableNotification")
            }
            UpdateChatIsMarkedAsUnread.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatIsMarkedAsUnread")
            }
            UpdateChatIsSponsored.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateChatIsSponsored")
            }
            UpdateUserFullInfo.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateUserFullInfo")
            }
            UpdateBasicGroupFullInfo.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateBasicGroupFullInfo")
            }
            UpdateSupergroupFullInfo.CONSTRUCTOR -> {
                Log.d("TelegramClient", "onResult: UpdateSupergroupFullInfo")
            }
            else -> {
                Log.d("TelegramClient", "onResult: else ${data::class.java.simpleName}")
            }
        }
    }


    suspend inline fun <reified ExpectedResult : TdApi.Object> send(function: TdApi.Function): ExpectedResult =
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