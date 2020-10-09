package ru.teamdroid.colibripost.remote.account

import org.drinkless.td.libcore.telegram.TdApi
import ru.teamdroid.colibripost.domain.account.AccountEntity
import ru.teamdroid.colibripost.remote.core.TelegramClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRequests @Inject constructor(
    private val client: TelegramClient
) {

    suspend fun getUserInfo(): AccountEntity {
        val user = client.send<TdApi.User>(TdApi.GetMe())
        user.profilePhoto?.let { it.big = downloadFiles(it.big) }
        val account = AccountEntity()
        account.fill(user)
        return account
    }

    suspend fun downloadFiles(file: TdApi.File): TdApi.File {
        return client.send(
            TdApi.DownloadFile(
                file.id,
                1,
                file.local.downloadOffset,
                file.local.downloadedPrefixSize,
                true
            )
        )
    }

}