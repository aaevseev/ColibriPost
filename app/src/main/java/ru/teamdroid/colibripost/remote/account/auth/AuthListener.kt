package ru.teamdroid.colibripost.remote.account.auth

import org.drinkless.td.libcore.telegram.TdApi

interface AuthListener {
    fun onAuthorizationStateUpdated(state: TdApi.AuthorizationState)

}