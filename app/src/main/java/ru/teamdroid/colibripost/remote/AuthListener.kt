package ru.teamdroid.colibripost.remote

import org.drinkless.td.libcore.telegram.TdApi

interface AuthListener {
    fun onAuthorizationStateUpdated(state: TdApi.AuthorizationState)

}
