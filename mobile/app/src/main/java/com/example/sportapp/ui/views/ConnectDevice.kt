package com.example.sportapp.ui.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportapp.BluetoothManager
import com.example.sportapp.GetGoogleFitManager
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.utils.UtilRedirect
import com.example.sportapp.data.services.FitnessSensor
import com.example.sportapp.data.services.FitnessSensorListener
import com.example.sportapp.ui.home.Home
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.material.button.MaterialButton
import java.io.IOException
import java.util.Calendar
import java.util.concurrent.TimeUnit
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

interface OnItemClickListener {
    fun onItemClick(position: Int)
}

class ConnectDevice : AppCompatActivity() , BluetoothManager.BluetoothListener , FitnessSensorListener {

    private lateinit var bluetoothManager: BluetoothManager
    private val sensor = FitnessSensor()
    private lateinit var powerOutputTextView: TextView
    private lateinit var maxHeartRateTextView: TextView
    private lateinit var restingHeartRateTextView: TextView
    private val utilRedirect = UtilRedirect()
    private val REQUEST_CODE_PERMISSIONS = 101
    private val RC_SIGN_IN = 9001
    private lateinit var googleFitManager: GetGoogleFitManager
    private lateinit var fitnessOptions: FitnessOptions
    private lateinit var cuenta: GoogleSignInAccount
    var resHearRateGoogle: Float = 0.0F



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_device)
        val btnStartDevice = findViewById<Button>(R.id.btnStartDevice)
        setUpNavigationButtons()


        fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_BASAL_METABOLIC_RATE, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_POINTS, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
            .build()



        btnStartDevice.setOnClickListener {
            SportApp.startDevice = true
            sensor.setListener(this@ConnectDevice) // Pasa una instancia de ConnectDevice como oyente
            sensor.start()

            val measurements = sensor.generateManualMeasurements()
            var (powerOutput, maxHeartRate, restingHeartRate) = measurements ?: Triple(0, 0, 0)



//        // Llamar al método fetchRestingHeartRate para obtener el ritmo cardíaco en reposo
            fetchRestingHeartRate1 { restingHeartRate ->
                // Procesar el ritmo cardíaco en reposo recibido en el callback
                Log.d("ConnectGoogle", "Ritmo cardíaco en reposo: $restingHeartRate")
                resHearRateGoogle = restingHeartRate

            }

            getStepsClient(this) { totalPasos ->
                Log.d("TotalPasos", "Total de pasos del cliente: $totalPasos")
                // Aquí puedes realizar cualquier acción con el total de pasos obtenido
                //resHearRateGoogle = totalPasos.toFloat()

            }
            Log.d("ConnectGoogle", "Ritmo cardíaco en reposo google: $resHearRateGoogle")
            onMeasurementsChanged(powerOutput, maxHeartRate, resHearRateGoogle.toInt())
        }

        // Configurar Google Sign-In
        configureSignIn()
        startSignIn(signInResultCallback)


        val fitnessOptions = FitnessOptions.builder().addDataType(DataType.TYPE_STEP_COUNT_DELTA).build()

        // Crear una instancia de GoogleFitManager
        val googleFitManager = GoogleFitManager(this, fitnessOptions)
//
//        // Llamar al método fetchRestingHeartRate para obtener el ritmo cardíaco en reposo
        googleFitManager.fetchRestingHeartRate { restingHeartRate ->
            // Procesar el ritmo cardíaco en reposo recibido en el callback
            Log.d("ConnectGoogle", "Ritmo cardíaco en reposo: $restingHeartRate")
        }



/*
        try {
            val macAddress = "F8:5B:6E:9A:3B:23" //Samsung Watch4
            //val macAddress = "7C:8A:E1:F7:5B:18") // Fossil GenStule 4
            //val macAddress = "24:81:C7:36:98:25" // Huawei GT-4

            // Inicializar BluetoothManager pasando el contexto de esta actividad y una instancia de BluetoothListener
            bluetoothManager = BluetoothManager(this, this)
            bluetoothManager.connectToDevice(macAddress)

            if (bluetoothManager.isConnected()) {
                Log.d("Bluetooth", "El dispositivo está conectado")
                showToast(this@ConnectDevice,  "El dispositivo está conectado MAC : " + macAddress)
            } else {
                showToast(this@ConnectDevice,  "El dispositivo NO está conectado MAC : " + macAddress)
                Log.d("Bluetooth", "El dispositivo no está conectado")
            }

            val response = bluetoothManager.sendCommandAndGetResponse(command)
            response?.let {
                Log.d("ConnectDevice", "Datos de salud recibidos del dispositivo Wear OS: $it")
            }
        }
        catch (e: IOException) {
            Log.e("ConnectDevice", "Error de comunicación al enviar o recibir datos", e)
        }
 */


        //Inicializa los textviws.
        powerOutputTextView = findViewById(R.id.powerOutputTextView)
        maxHeartRateTextView = findViewById(R.id.maxHeartRateTextView)
        restingHeartRateTextView = findViewById(R.id.restingHeartRateTextView)

        findViewById<Button>(R.id.btnStartDevice).setOnClickListener {
            SportApp.startDevice = true
            sensor.setListener(this)
            sensor.start()
            val measurements = sensor.generateManualMeasurements()
            measurements?.let { (powerOutput, maxHeartRate, restingHeartRate) ->
                onMeasurementsChanged(powerOutput, maxHeartRate, restingHeartRate)
            }
        }

        setUpRecyclerView()
        setUpNavigationButtons()
    }

    private fun setUpRecyclerView() {
        val dataList = getString(R.string.device).split(",")

        val recyclerView = findViewById<RecyclerView>(R.id.rvTypeDevice)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = Adapter(dataList, object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedItemName = dataList[position]
                Toast.makeText(this@ConnectDevice, getString(R.string.promt_device) + " $selectedItemName", Toast.LENGTH_SHORT).show()
            }
        })
    }

        // Callback para manejar el resultado del inicio de sesión
        private val signInResultCallback = object : SignInResultCallback {
            override fun onSignInSuccess() {
                // El inicio de sesión fue exitoso, obtener los datos de Google Fit
//                fitnessOptions = FitnessOptions.builder()
//                    .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//                    .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
//                    .build()

                Log.d("ConnectGoogle", "Definido FitnessOptions: " + fitnessOptions.toString())
                Log.d("ConnectGoogle", "Definido googleFitManager: " + fitnessOptions.toString())

                // Llamar al método fetchRestingHeartRate para obtener el ritmo cardíaco en reposo
                fetchRestingHeartRate1 { restingHeartRate ->
                    Log.d("ConnectGoogle", "Ritmo cardíaco en reposo: $restingHeartRate")
                }
            }

            override fun onSignInError(error: String) {
                // Manejar el error de inicio de sesión
                Log.e("ConnectGoogle", "Error al iniciar sesión: $error")
            }
        }

    interface SignInResultCallback {
        fun onSignInSuccess()
        fun onSignInError(error: String)
    }
    fun startSignIn(callback: SignInResultCallback) {
        // Configurar las opciones de inicio de sesión de Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestProfile()
            .requestId()
            .requestEmail()
            // Agrega otras opciones según sea necesario
            .build()

        // Crear un cliente de inicio de sesión con Google
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Iniciar la actividad de inicio de sesión con Google
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signIn(mGoogleApiClient: GoogleApiClient) {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
        Log.d("ConnectGoogle", "Entro SignIn")
    }

    // Implementa el método onActivityResult para manejar el resultado del inicio de sesión
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(it) }
            if (result != null) {

                handleSignInResult(result)
                Log.d("ConnectGoogle", "On Activity Result >> : " + result.signInAccount )

            }
            else{
                Log.d("ConnectGoogle", "Error On Activity Result >> : " + result?.status?.statusMessage.toString())
            }
        }
        else {
            Log.d("ConnectGoogle", "On Activity Result LLego por el false")

        }

    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            //val account = result.signInAccount
            val account: GoogleSignInAccount? = result.signInAccount
            Log.d("ConnectGoogle", "Conexión establecida correctamente : " + result.status + " >> Cuenta : " + account!!?.displayName + " >> " + account!!?.requestedScopes)
            showToast(this@ConnectDevice,  "Conectado a la cuenta de google de : " + account!!?.displayName)
            signInResultCallback.onSignInSuccess()
        } else {
            // El inicio de sesión falló, muestra un mensaje de error o toma otras medidas.
            Log.d("ConnectGoogle", "Error en la Conexión : " + result.status.statusMessage +  " SignInAccount : " + result.signInAccount)
            signInResultCallback.onSignInError(result.status.statusMessage.toString())
        }
    }

    // Configura Google Sign-In
    fun configureSignIn() {
        // Crea una instancia de GoogleSignInOptions con las opciones deseadas
        Log.d("ConnectGoogle", "Inicio configuracion.")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestProfile()
            .requestId()
            .requestEmail()
            .addExtension(fitnessOptions)
            .build()

        // Crea un cliente de GoogleApiClient
        val mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this@ConnectDevice) {
                Log.d("ConnectGoogle", "Error al crear el cliente mGoogleApiClient . ")
            }
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        // Iniciar sesión con Google
        signIn(mGoogleApiClient)
    }

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
    fun fetchRestingHeartRate(callback: (Float) -> Unit) {
        cuenta = GoogleSignIn.getAccountForExtension(this, fitnessOptions) // Cuenta
        if (cuenta != null)
        {
            val tiempoFinal = Calendar.getInstance().timeInMillis
            val tiempoInicial = tiempoFinal - TimeUnit.DAYS.toMillis(7) // Obtener datos de los últimos 7 días

            try {
                val solicitudFrecuenciaCardiacaReposo = DataReadRequest.Builder()
                    .read(DataType.TYPE_HEART_RATE_BPM) // Tipo de dato
                    .setTimeRange(tiempoInicial, tiempoFinal, TimeUnit.MILLISECONDS)
                    .build()

                Fitness.getHistoryClient(this, cuenta)
                    .readData(solicitudFrecuenciaCardiacaReposo)
                    .addOnSuccessListener { respuesta ->
                        val puntosDatos = respuesta.getDataSet(DataType.TYPE_HEART_RATE_BPM).dataPoints
                        val ultimoPunto = puntosDatos.lastOrNull()
                        val frecuenciaCardiacaReposo = ultimoPunto?.getValue(Field.FIELD_BPM)?.asFloat() ?: 0f

                        Log.e("ConnectGoogle", "Datos obtenidos enlistener >>>> : " + puntosDatos.toString()  + "")

                        callback(frecuenciaCardiacaReposo)
                    }
                    .addOnFailureListener { excepcion ->
                        Log.e("ConnectGoogle", "Error al obtener datos >>>> : " + cuenta.toString()  + " Exc: " , excepcion)
                        callback(0f) // O proporciona un mensaje más informativo o maneja el error en el código de llamada
                    }
            } catch (e: IOException) {
                // Elimina este bloque si no es relevante para el acceso a datos de Google Fit
                Log.e("ConnectGoogle", "Error al enviar datos por Bluetooth: ${e.message}")
            }
        }
        else{
            Log.e("ConnectGoogle", "El signin esta vacio o con error.")
        }
    }

    fun fetchRestingHeartRate1(callback: (Float) -> Unit) {
        cuenta = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        if (cuenta != null) {

            Log.d("ConnectGoogle", "Permisos de la cuenta. >>>> : ${cuenta.grantedScopes} ")
            Log.d("ConnectGoogle", "Permisos de la fitnesoptions. >>>> : ${fitnessOptions.impliedScopes} ")

            // Verifique si el usuario ha otorgado los permisos necesarios
            if (GoogleSignIn.hasPermissions(cuenta, fitnessOptions)) {
                val tiempoInicial = Calendar.getInstance().timeInMillis - TimeUnit.DAYS.toMillis(90)
                val tiempoFinal = Calendar.getInstance().timeInMillis

            Log.d("ConnectGoogle", "Tiempo Inicial. >>>> : ${tiempoInicial.toString()} ")
            Log.d("ConnectGoogle", "Tiempo Final. >>>> : ${tiempoFinal.toString()} ")

                try {
                    val solicitudFrecuenciaCardiacaReposo = DataReadRequest.Builder()
                        .read(DataType.TYPE_HEART_RATE_BPM) // Tipo de dato
                        .setTimeRange(tiempoInicial, tiempoFinal, TimeUnit.MILLISECONDS)
                        .build()

                    Fitness.getHistoryClient(this, cuenta)
                        .readData(solicitudFrecuenciaCardiacaReposo)
                        .addOnSuccessListener { respuesta ->
                            val puntosDatos = respuesta.getDataSet(DataType.TYPE_HEART_RATE_BPM).dataPoints
                            val ultimoPunto = puntosDatos.lastOrNull()
                            val frecuenciaCardiacaReposo = ultimoPunto?.getValue(Field.FIELD_BPM)?.asFloat() ?: 0f

                            Log.d("ConnectGoogle", "Datos obtenidos enlistener >>>> : $puntosDatos $frecuenciaCardiacaReposo $ultimoPunto")

                            callback(frecuenciaCardiacaReposo)
                        }
                        .addOnFailureListener { excepcion ->
                            val mensajeError = "Error al obtener datos: ${excepcion.message}"
                            Log.e("ConnectGoogle", mensajeError)
                            callback(0f) // O proporcione un mensaje más informativo
                        }
                } catch (e: IOException) {
                    Log.e("ConnectGoogle", "Error al enviar datos por Bluetooth: ${e.message}")
                }
            } else {
                Log.e("ConnectGoogle", "El usuario no ha otorgado los permisos necesarios.")
                // Proporcione instrucciones sobre cómo otorgar permisos
                callback(0f)
            }
        } else {
            Log.e("ConnectGoogle", "El usuario no ha iniciado sesión en Google Fit.")
            // Proporcione instrucciones sobre cómo iniciar sesión
            callback(0f)
        }
    }

    fun getStepsClient(contexto: Context, callback: (Int) -> Unit) {
        val cuenta = GoogleSignIn.getAccountForExtension(contexto, fitnessOptions)
        val tiempoActual = System.currentTimeMillis()
        val tiempoInicial = tiempoActual - TimeUnit.DAYS.toMillis(30) // Obtener datos de los últimos 7 días


        if (GoogleSignIn.hasPermissions(cuenta, fitnessOptions)) {
            val solicitudPasos = DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(tiempoInicial, tiempoActual, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS) // Agrupar los pasos por día
                .build()

            Fitness.getHistoryClient(contexto, cuenta)
                .readData(solicitudPasos)
                .addOnSuccessListener { respuesta ->
                    var totalPasos = 0
                    respuesta.buckets.forEach { bucket ->
                        bucket.dataSets.forEach { dataSet ->
                            dataSet.dataPoints.forEach { dataPoint ->
                                val pasos = dataPoint.getValue(Field.FIELD_STEPS).asInt()
                                totalPasos += pasos
                            }
                        }
                    }
                    Log.d("ConnectGoogle", "Pasos obtenidos enlistener >>>> : $totalPasos")
                    callback(totalPasos)
                }
                .addOnFailureListener { excepcion ->
                    Log.e("ConnectGoogle", "Error al obtener los pasos: ", excepcion)
                    callback(0)
                }
        }
        else {
            Log.e("ConnectGoogle", "El usuario no ha iniciado sesión en Google Fit.")
            // Proporcione instrucciones sobre cómo iniciar sesión

        }
    }

    override fun onConnected() {
        // Se llama cuando la conexión Bluetooth se establece correctamente
        Log.d("ConnectDevice", "Conexión establecida correctamente")
        // Puedes enviar datos al dispositivo Bluetooth si es necesario
        bluetoothManager.sendData("Hola desde el dispositivo Android!")
        //Log.d("ConnectDevice", "Datos del dispositivo capturados : $inputStream")
//        val inputStream = bluetoothManager.inputStream
//        val jsonData = inputStream?.let { inputStreamToJson(it) }
//        Log.d("ConnectDevice", "Datos del dispositivo capturados JSON : $jsonData")
    }

    override fun onDisconnected() {
        // Se llama cuando la conexión Bluetooth se desconecta
        Log.d("ConnectDevice", "Conexión Bluetooth desconectada")
    }
    override fun onError(message: String) {
        // Se llama cuando ocurre un error en la conexión Bluetooth
        Log.e("ConnectDevice", "Error en la conexión Bluetooth: $message")
    }

        // Callback para manejar el resultado del inicio de sesión
        private val signInResultCallback = object : SignInResultCallback {
            override fun onSignInSuccess() {
                // El inicio de sesión fue exitoso, obtener los datos de Google Fit
//                fitnessOptions = FitnessOptions.builder()
//                    .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
//                    .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
//                    .build()

                Log.d("ConnectGoogle", "Definido FitnessOptions: " + fitnessOptions.toString())
                Log.d("ConnectGoogle", "Definido googleFitManager: " + fitnessOptions.toString())

                // Llamar al método fetchRestingHeartRate para obtener el ritmo cardíaco en reposo
                fetchRestingHeartRate1 { restingHeartRate ->
                    Log.d("ConnectGoogle", "Ritmo cardíaco en reposo: $restingHeartRate")
                }
            }

            override fun onSignInError(error: String) {
                // Manejar el error de inicio de sesión
                Log.e("ConnectGoogle", "Error al iniciar sesión: $error")
            }
        }

    interface SignInResultCallback {
        fun onSignInSuccess()
        fun onSignInError(error: String)
    }
    fun startSignIn(callback: SignInResultCallback) {
        // Configurar las opciones de inicio de sesión de Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestProfile()
            .requestId()
            .requestEmail()
            // Agrega otras opciones según sea necesario
            .build()

        // Crear un cliente de inicio de sesión con Google
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Iniciar la actividad de inicio de sesión con Google
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signIn(mGoogleApiClient: GoogleApiClient) {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
        Log.d("ConnectGoogle", "Entro SignIn")
    }

    // Implementa el método onActivityResult para manejar el resultado del inicio de sesión
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(it) }
            if (result != null) {

                handleSignInResult(result)
                Log.d("ConnectGoogle", "On Activity Result >> : " + result.signInAccount )

            }
            else{
                Log.d("ConnectGoogle", "Error On Activity Result >> : " + result?.status?.statusMessage.toString())
            }
        }
        else {
            Log.d("ConnectGoogle", "On Activity Result LLego por el false")

        }

    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            //val account = result.signInAccount
            val account: GoogleSignInAccount? = result.signInAccount
            Log.d("ConnectGoogle", "Conexión establecida correctamente : " + result.status + " >> Cuenta : " + account!!?.displayName + " >> " + account!!?.requestedScopes)
            showToast(this@ConnectDevice,  "Conectado a la cuenta de google de : " + account!!?.displayName)
            signInResultCallback.onSignInSuccess()
        } else {
            // El inicio de sesión falló, muestra un mensaje de error o toma otras medidas.
            Log.d("ConnectGoogle", "Error en la Conexión : " + result.status.statusMessage +  " SignInAccount : " + result.signInAccount)
            signInResultCallback.onSignInError(result.status.statusMessage.toString())
        }
    }

    // Configura Google Sign-In
    fun configureSignIn() {
        // Crea una instancia de GoogleSignInOptions con las opciones deseadas
        Log.d("ConnectGoogle", "Inicio configuracion.")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestProfile()
            .requestId()
            .requestEmail()
            .addExtension(fitnessOptions)
            .build()

        // Crea un cliente de GoogleApiClient
        val mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this@ConnectDevice) {
                Log.d("ConnectGoogle", "Error al crear el cliente mGoogleApiClient . ")
            }
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        // Iniciar sesión con Google
        signIn(mGoogleApiClient)
    }


    fun fetchRestingHeartRate(callback: (Float) -> Unit) {
        cuenta = GoogleSignIn.getAccountForExtension(this, fitnessOptions) // Cuenta
        if (cuenta != null)
        {
            val tiempoFinal = Calendar.getInstance().timeInMillis
            val tiempoInicial = tiempoFinal - TimeUnit.DAYS.toMillis(7) // Obtener datos de los últimos 7 días

            try {
                val solicitudFrecuenciaCardiacaReposo = DataReadRequest.Builder()
                    .read(DataType.TYPE_HEART_RATE_BPM) // Tipo de dato
                    .setTimeRange(tiempoInicial, tiempoFinal, TimeUnit.MILLISECONDS)
                    .build()

                Fitness.getHistoryClient(this, cuenta)
                    .readData(solicitudFrecuenciaCardiacaReposo)
                    .addOnSuccessListener { respuesta ->
                        val puntosDatos = respuesta.getDataSet(DataType.TYPE_HEART_RATE_BPM).dataPoints
                        val ultimoPunto = puntosDatos.lastOrNull()
                        val frecuenciaCardiacaReposo = ultimoPunto?.getValue(Field.FIELD_BPM)?.asFloat() ?: 0f

                        Log.e("ConnectGoogle", "Datos obtenidos enlistener >>>> : " + puntosDatos.toString()  + "")

                        callback(frecuenciaCardiacaReposo)
                    }
                    .addOnFailureListener { excepcion ->
                        Log.e("ConnectGoogle", "Error al obtener datos >>>> : " + cuenta.toString()  + " Exc: " , excepcion)
                        callback(0f) // O proporciona un mensaje más informativo o maneja el error en el código de llamada
                    }
            } catch (e: IOException) {
                // Elimina este bloque si no es relevante para el acceso a datos de Google Fit
                Log.e("ConnectGoogle", "Error al enviar datos por Bluetooth: ${e.message}")
            }
        }
        else{
            Log.e("ConnectGoogle", "El signin esta vacio o con error.")
        }
    }

    fun fetchRestingHeartRate1(callback: (Float) -> Unit) {
        cuenta = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        if (cuenta != null) {

            Log.d("ConnectGoogle", "Permisos de la cuenta. >>>> : ${cuenta.grantedScopes} ")
            Log.d("ConnectGoogle", "Permisos de la fitnesoptions. >>>> : ${fitnessOptions.impliedScopes} ")

            // Verifique si el usuario ha otorgado los permisos necesarios
            if (GoogleSignIn.hasPermissions(cuenta, fitnessOptions)) {
                val tiempoInicial = Calendar.getInstance().timeInMillis - TimeUnit.DAYS.toMillis(90)
                val tiempoFinal = Calendar.getInstance().timeInMillis

            Log.d("ConnectGoogle", "Tiempo Inicial. >>>> : ${tiempoInicial.toString()} ")
            Log.d("ConnectGoogle", "Tiempo Final. >>>> : ${tiempoFinal.toString()} ")

                try {
                    val solicitudFrecuenciaCardiacaReposo = DataReadRequest.Builder()
                        .read(DataType.TYPE_HEART_RATE_BPM) // Tipo de dato
                        .setTimeRange(tiempoInicial, tiempoFinal, TimeUnit.MILLISECONDS)
                        .build()

                    Fitness.getHistoryClient(this, cuenta)
                        .readData(solicitudFrecuenciaCardiacaReposo)
                        .addOnSuccessListener { respuesta ->
                            val puntosDatos = respuesta.getDataSet(DataType.TYPE_HEART_RATE_BPM).dataPoints
                            val ultimoPunto = puntosDatos.lastOrNull()
                            val frecuenciaCardiacaReposo = ultimoPunto?.getValue(Field.FIELD_BPM)?.asFloat() ?: 0f

                            Log.d("ConnectGoogle", "Datos obtenidos enlistener >>>> : $puntosDatos $frecuenciaCardiacaReposo $ultimoPunto")

                            callback(frecuenciaCardiacaReposo)
                        }
                        .addOnFailureListener { excepcion ->
                            val mensajeError = "Error al obtener datos: ${excepcion.message}"
                            Log.e("ConnectGoogle", mensajeError)
                            callback(0f) // O proporcione un mensaje más informativo
                        }
                } catch (e: IOException) {
                    Log.e("ConnectGoogle", "Error al enviar datos por Bluetooth: ${e.message}")
                }
            } else {
                Log.e("ConnectGoogle", "El usuario no ha otorgado los permisos necesarios.")
                // Proporcione instrucciones sobre cómo otorgar permisos
                callback(0f)
            }
        } else {
            Log.e("ConnectGoogle", "El usuario no ha iniciado sesión en Google Fit.")
            // Proporcione instrucciones sobre cómo iniciar sesión
            callback(0f)
        }
    }

    fun getStepsClient(contexto: Context, callback: (Int) -> Unit) {
        val cuenta = GoogleSignIn.getAccountForExtension(contexto, fitnessOptions)
        val tiempoActual = System.currentTimeMillis()
        val tiempoInicial = tiempoActual - TimeUnit.DAYS.toMillis(30) // Obtener datos de los últimos 7 días


        if (GoogleSignIn.hasPermissions(cuenta, fitnessOptions)) {
            val solicitudPasos = DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(tiempoInicial, tiempoActual, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS) // Agrupar los pasos por día
                .build()

            Fitness.getHistoryClient(contexto, cuenta)
                .readData(solicitudPasos)
                .addOnSuccessListener { respuesta ->
                    var totalPasos = 0
                    respuesta.buckets.forEach { bucket ->
                        bucket.dataSets.forEach { dataSet ->
                            dataSet.dataPoints.forEach { dataPoint ->
                                val pasos = dataPoint.getValue(Field.FIELD_STEPS).asInt()
                                totalPasos += pasos
                            }
                        }
                    }
                    Log.d("ConnectGoogle", "Pasos obtenidos enlistener >>>> : $totalPasos")
                    callback(totalPasos)
                }
                .addOnFailureListener { excepcion ->
                    Log.e("ConnectGoogle", "Error al obtener los pasos: ", excepcion)
                    callback(0)
                }
        }
        else {
            Log.e("ConnectGoogle", "El usuario no ha iniciado sesión en Google Fit.")
            // Proporcione instrucciones sobre cómo iniciar sesión

        }
    }

    override fun onConnected() {
        // Se llama cuando la conexión Bluetooth se establece correctamente
        Log.d("ConnectDevice", "Conexión establecida correctamente")
        // Puedes enviar datos al dispositivo Bluetooth si es necesario
        bluetoothManager.sendData("Hola desde el dispositivo Android!")
        //Log.d("ConnectDevice", "Datos del dispositivo capturados : $inputStream")
//        val inputStream = bluetoothManager.inputStream
//        val jsonData = inputStream?.let { inputStreamToJson(it) }
//        Log.d("ConnectDevice", "Datos del dispositivo capturados JSON : $jsonData")
    }

    override fun onDisconnected() {
        // Se llama cuando la conexión Bluetooth se desconecta
        Log.d("ConnectDevice", "Conexión Bluetooth desconectada")
    }
    override fun onError(message: String) {
        // Se llama cuando ocurre un error en la conexión Bluetooth
        Log.e("ConnectDevice", "Error en la conexión Bluetooth: $message")
    }

    private fun setUpNavigationButtons() {
        findViewById<MaterialButton>(R.id.ivRunExe).setOnClickListener { utilRedirect.redirectToActivity(this, StartTraining::class.java) }
        findViewById<MaterialButton>(R.id.ivHome).setOnClickListener { utilRedirect.redirectToActivity(this, Home::class.java) }
        findViewById<MaterialButton>(R.id.ivCalendar).setOnClickListener { utilRedirect.redirectToActivity(this, CalendarEvents::class.java) }
        findViewById<MaterialButton>(R.id.ivNotifications).setOnClickListener { utilRedirect.redirectToActivity(this, Notifications::class.java) }
        findViewById<MaterialButton>(R.id.ivClockW).setOnClickListener { utilRedirect.redirectToActivity(this, DashboardTraining::class.java) }
        findViewById<MaterialButton>(R.id.ivWatch).setOnClickListener { utilRedirect.redirectToActivity(this, ConnectDevice::class.java) }
        findViewById<MaterialButton>(R.id.ivRun).setOnClickListener { utilRedirect.redirectToActivity(this, SuggestRoutes::class.java) }
        findViewById<MaterialButton>(R.id.ivSugerencias).setOnClickListener { utilRedirect.redirectToActivity(this, Suggests::class.java) }
    }

    override fun onMeasurementsChanged(powerOutput: Int, maxHeartRate: Int, restingHeartRate: Int) {
        runOnUiThread {

            val mhr = 220 - SportApp.age
            powerOutputTextView.text = "Power Output: $powerOutput watts"
            maxHeartRateTextView.text = "Max Heart Rate: $maxHeartRate bpm"
            restingHeartRateTextView.text = "Resting Heart Rate: $restingHeartRate bpm"
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private inner class Adapter(private val dataList: List<String>, private val listener: OnItemClickListener) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
            return ViewHolder(view, listener)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewItem.text = dataList[position]
        }

        override fun getItemCount() = dataList.size

        inner class ViewHolder(itemView: View, private val listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            val textViewItem: TextView = itemView.findViewById(R.id.textViewItem)

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View) {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onMeasurementsChanged(powerOutput: Int, maxHeartRate: Int, restingHeartRate: Int) {
        runOnUiThread {

            val mhr = 220 - SportApp.age
            powerOutputTextView.text = "Power Output: $powerOutput watts"
            maxHeartRateTextView.text = "Max Heart Rate: $mhr bpm"
            restingHeartRateTextView.text = "Resting Heart Rate: $resHearRateGoogle bpm"
            Log.d("DEBUG", "Entro a escribir los datos.: ")
        }
    }

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}

