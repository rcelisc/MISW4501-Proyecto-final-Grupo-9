package com.example.sportapp

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeoutException

class BluetoothManager(private val context: Context, private val listener: BluetoothListener) {

    private val TAG = "ConnectDeviceBTM"
    private val UUID_SERIAL = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // UUID para SPP (Serial Port Profile)
    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSION = 1001
    }
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    var inputStream: InputStream? = null
    var inputStream123: InputStream? = null

    init {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.e(TAG, "El dispositivo no admite Bluetooth")
            listener.onError("El dispositivo no admite Bluetooth")
        } else if (!bluetoothAdapter!!.isEnabled) {
            Log.e(TAG, "Bluetooth no está habilitado")
            listener.onError("Bluetooth no está habilitado")
        }
    }


    fun connectToDevice(deviceAddress: String) {
        val device: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(deviceAddress)

        // Verificar permisos BLUETOOTH y BLUETOOTH_ADMIN
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {

            // Si los permisos no están concedidos, solicitarlos
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN), REQUEST_BLUETOOTH_PERMISSION)
            return
        }

        try {
            bluetoothSocket = device?.createRfcommSocketToServiceRecord(UUID_SERIAL)
            bluetoothSocket?.connect()
            Log.i(TAG, "Conexión Bluetooth establecida")
            outputStream = bluetoothSocket?.outputStream
            inputStream = bluetoothSocket?.inputStream

            //Log.i(TAG, "Conexión Bluetooth establecida JSON Data: " + inputStream123.toString())

            listener.onConnected()

            // Llama a la función para leer datos del inputStream
            //readDataFromStream()
        } catch (e: IOException) {
            Log.e(TAG, "Error al establecer conexión Bluetooth: ${e.message}")
            listener.onError("Error al establecer conexión Bluetooth: ${e.message}")
        }
    }


    private fun readDataFromStream() {
        try {
            val buffer = ByteArray(1024)
            val bytesRead = inputStream123?.read(buffer) // Leer los datos del inputStream
            if (bytesRead != null && bytesRead > 0) {
                val receivedData = String(buffer, 0, bytesRead)
                Log.i(TAG, "Datos recibidos del dispositivo: $receivedData")
                // Procesa los datos recibidos según sea necesario
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error al leer datos del inputStream: ${e.message}")
            // Maneja el error de lectura del inputStream
        }
    }

    fun sendData(message: String) {
        try {
            outputStream?.write(message.toByteArray())
            Log.i(TAG, "Mensaje enviado: $message")
        } catch (e: IOException) {
            Log.e(TAG, "Error al enviar datos por Bluetooth: ${e.message}")
            listener.onError("Error al enviar datos por Bluetooth: ${e.message}")
        }
    }

//    fun sendCommandAndGetResponse(command: String): String? {
//        try {
//            outputStream?.write(command.toByteArray())
//            outputStream?.flush()
//
//            Log.i(TAG, "Mensaje Enviado : " + command)
//            // Esperar la respuesta durante un tiempo limitado
//            val timeoutMillis = 10000 // 5 segundos
//            val startTime = System.currentTimeMillis()
//
//            while (System.currentTimeMillis() - startTime < timeoutMillis) {
//                if (inputStream?.available() ?: 0 > 0) {
//                    val buffer = ByteArray(1024)
//                    val bytesRead = inputStream?.read(buffer)
//                    if (bytesRead != null && bytesRead > 0) {
//                        return String(buffer, 0, bytesRead)
//                    }
//                } else {
//                    // Esperar un breve período antes de verificar nuevamente
//                    Thread.sleep(100)
//                }
//            }
//
//            // Si no se recibió ninguna respuesta dentro del tiempo de espera
//            Log.e(TAG, "Tiempo de espera agotado para recibir la respuesta del comando")
//        } catch (e: IOException) {
//            Log.e(TAG, "Error de comunicación al enviar comando", e)
//        } catch (e: InterruptedException) {
//            Log.e(TAG, "Error de temporización al esperar la respuesta", e)
//        }
//
//        return null
//    }

    fun sendCommandAndGetResponse(command: String): CompletableFuture<String?> {
        val futureResponse = CompletableFuture<String?>()

        try {
            outputStream?.write(command.toByteArray())
            outputStream?.flush()

            Log.i(TAG, "Mensaje Enviado: $command")

            // Inicia un hilo para esperar la respuesta en segundo plano
            Thread {
                val timeoutMillis = 10000 // 10 segundos
                val startTime = System.currentTimeMillis()

                while (System.currentTimeMillis() - startTime < timeoutMillis) {
                    if (inputStream?.available() ?: 0 > 0) {
                        val buffer = ByteArray(1024)
                        val bytesRead = inputStream?.read(buffer)
                        if (bytesRead != null && bytesRead > 0) {
                            val response = String(buffer, 0, bytesRead)
                            futureResponse.complete(response)
                            return@Thread
                        }
                    } else {
                        // Esperar un breve período antes de verificar nuevamente
                        Thread.sleep(100)
                    }
                }

                // Si no se recibió ninguna respuesta dentro del tiempo de espera
                futureResponse.completeExceptionally(TimeoutException("Tiempo de espera agotado para recibir la respuesta del comando"))
            }.start()
        } catch (e: IOException) {
            futureResponse.completeExceptionally(e)
            Log.e(TAG, "Error de comunicación al enviar comando", e)
        }

        return futureResponse
    }

    fun disconnect() {
        try {
            bluetoothSocket?.close()
            listener.onDisconnected()
        } catch (e: IOException) {
            Log.e(TAG, "Error al cerrar conexión Bluetooth: ${e.message}")
            listener.onError("Error al cerrar conexión Bluetooth: ${e.message}")
        }
    }

    fun isConnected(): Boolean {
        return bluetoothSocket != null && bluetoothSocket!!.isConnected
    }

    interface BluetoothListener {
        fun onConnected()
        fun onDisconnected()
        fun onError(message: String)
    }
}
