package ru.teamdroid.colibripost.data.account.auth

interface AuthRemote {
    suspend fun insertCode(code: String): Boolean
}