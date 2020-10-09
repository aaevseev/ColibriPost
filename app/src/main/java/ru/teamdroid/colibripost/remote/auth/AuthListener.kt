package ru.teamdroid.colibripost.remote.auth

import org.drinkless.td.libcore.telegram.TdApi

interface AuthListener {
    fun onAuthorizationStateUpdated(state: TdApi.AuthorizationState)

}