package com.maestrx.studentcontrol.studentapp.presentation.loading_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrx.studentcontrol.studentapp.domain.wifi.WifiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val wifiHelper: WifiHelper,
) : ViewModel() {

    private val serverPort = 5951
    private val socket = DatagramSocket(serverPort)

    private val _state = MutableStateFlow(LoadingUiState())
    val state = _state.asStateFlow()

    private var isDataExchangeStarted = false

    fun dataExchange() {
        if (isDataExchangeStarted) return

        val serverAddress = wifiHelper.getGatewayIpAddress()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sendData =
                    "123456111123456111111111111111111111111111111112345611111111111111111111123456111111111111111111111111111111111234561111111111111111111111111111111111111123456111111111111111111111111111111111111111111111111111".toByteArray()
                val sendPacket = DatagramPacket(sendData, sendData.size, serverAddress, serverPort)
                socket.send(sendPacket)

                // Ожидание ACK от сервера
                val buffer = ByteArray(1024)
                val receivePacket = DatagramPacket(buffer, buffer.size)
                socket.receive(receivePacket)
                val receivedData = String(receivePacket.data, 0, receivePacket.length)
                Log.d("StudentApp", "Received ACK from server: $receivedData")
                isDataExchangeStarted = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            socket.close()
        }
    }
}