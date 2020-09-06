package ru.teamdroid.colibripost.remote

enum class AuthStates {
    UNAUTHENTICATED,
    WAIT_FOR_NUMBER,
    WAIT_FOR_CODE,
    WAIT_FOR_PASSWORD,
    AUTHENTICATED,
    UNKNOWN
}