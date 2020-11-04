package ru.teamdroid.colibripost.remote.account.auth

import ru.teamdroid.colibripost.data.account.auth.AuthRemote
import javax.inject.Inject

class AuthRemoteImpl @Inject constructor(
    private val authHolder: AuthHolder
): AuthRemote {
    override suspend fun insertCode(code: String): Boolean {
        return authHolder.insertCode(code)
    }

    override suspend fun insertPhoneNumber(phone: String): String {
        return authHolder.insertPhoneNumber(phone)
    }

}