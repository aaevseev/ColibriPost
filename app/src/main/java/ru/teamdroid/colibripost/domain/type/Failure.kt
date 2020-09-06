package ru.teamdroid.colibripost.domain.type

sealed class Failure {

    object NetworkConnectionError: Failure()
    object ServerError: Failure()

    object ChannelsListIsEmptyError:Failure()

}