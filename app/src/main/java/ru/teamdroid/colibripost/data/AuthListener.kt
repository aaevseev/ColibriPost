package ru.teamdroid.colibripost.data

import org.drinkless.td.libcore.telegram.TdApi

interface AuthListener {
    fun onAuthorizationStateUpdated(state: TdApi.AuthorizationState)

}
