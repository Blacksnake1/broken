package com.nekosoft.brokenglass.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Handler
import android.os.Looper

class NetworkStateMonitor(
    private val context: Context,
    private val onNetworkStateChanged: (Boolean) -> Unit
) {
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    fun startMonitoring() {
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Handler(Looper.getMainLooper()).postDelayed({
                    onNetworkStateChanged(true)
                }, 1000)
            }

            override fun onLost(network: Network) {
                Handler(Looper.getMainLooper()).postDelayed({
                    onNetworkStateChanged(false)
                }, 1000)
            }
        }

        connectivityManager?.registerDefaultNetworkCallback(networkCallback!!)
    }

    fun stopMonitoring() {
        connectivityManager?.unregisterNetworkCallback(networkCallback!!)
    }
}