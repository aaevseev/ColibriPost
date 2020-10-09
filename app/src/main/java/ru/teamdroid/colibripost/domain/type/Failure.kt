package ru.teamdroid.colibripost.domain.type

sealed class Failure {

    object NetworkConnectionError : Failure()
    object NetworkPlaceHolderConnectionError : Failure()

    object ServerError : Failure()

    object ChannelsListIsEmptyError : Failure()
    object ChannelsNotCreatedError : Failure()

}