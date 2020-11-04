package ru.teamdroid.colibripost.remote.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkHandler @Inject constructor(private val context: Context) {

    val isConnected get() = context.networkInfo?.isConnected
}

val Context.networkInfo: NetworkInfo?
    get() =
        (this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .activeNetworkInfo

fun ConnectivityManager.setNetworkCallback(
    setNetworkAvailableUi: () -> Unit,
    setNetworkLostUi: () -> Unit
): ConnectivityManager.NetworkCallback {

    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            setNetworkAvailableUi()
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            setNetworkLostUi()
        }
    }

    this.registerNetworkCallback(
        NetworkRequest.Builder().build(),
        networkCallback)

    return networkCallback
}