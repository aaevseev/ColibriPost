package ru.teamdroid.colibripost.data

import org.drinkless.td.libcore.telegram.TdApi

sealed class TelegramException(message: String) : Throwable(message) {
    class Error(message: String) : TelegramException(message)
    class UnexpectedResult(result: TdApi.Object) : TelegramException("unexpected result: $result")
}