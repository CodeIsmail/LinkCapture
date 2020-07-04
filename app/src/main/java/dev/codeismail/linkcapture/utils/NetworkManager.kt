package dev.codeismail.linkcapture.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class Callback : ConnectivityManager.NetworkCallback() {

    val result = MutableLiveData<NetworkResult>()

    override fun onLost(network: Network?) {
        Log.d("Hello","Network disconnected")
        result.postValue(NetworkResult.DISCONNECTED)
    }

    override fun onLosing(network: Network?, maxMsToLive: Int) {
        Log.d("Hello","Network disconnecting")
        result.postValue(NetworkResult.DISCONNECTING)
    }

    override fun onAvailable(network: Network?) {
        Log.d("Hello","Network connected")
        result.postValue(NetworkResult.CONNECTED)
    }
}

class Factory {

    fun wifiRequest() : NetworkRequest =
        NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
}

enum class NetworkResult {
    CONNECTED,
    DISCONNECTED,
    DISCONNECTING
}

class Manager(context: Context,
              private val factory: Factory = Factory(),
              private val callback: Callback = Callback()) {

    private val connectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    val result : LiveData<NetworkResult>
        get() = callback.result

    fun registerCallback() {
        val request = factory.wifiRequest()
        connectivityManager.registerNetworkCallback(request, callback)
    }

    fun unregisterCallback() {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}