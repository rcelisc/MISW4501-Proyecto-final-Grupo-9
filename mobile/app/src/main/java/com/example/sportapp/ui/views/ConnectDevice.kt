package com.example.sportapp.ui.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportapp.BluetoothManager
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.data.services.FitnessSensor
import com.example.sportapp.data.services.FitnessSensorListener
import com.example.sportapp.ui.home.Home
import com.example.sportapp.utils.BadgeUtils
import com.example.sportapp.utils.UtilRedirect
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.io.IOException
import java.util.Calendar
import java.util.concurrent.TimeUnit

interface OnItemClickListener {
    fun onItemClick(position: Int)
}

class ConnectDevice : AppCompatActivity(), BluetoothManager.BluetoothListener, FitnessSensorListener {
    private val utilRedirect = UtilRedirect()
    private lateinit var bluetoothManager: BluetoothManager
    private val sensor = FitnessSensor()
    private lateinit var powerOutputTextView: TextView
    private lateinit var maxHeartRateTextView: TextView
    private lateinit var restingHeartRateTextView: TextView
    private lateinit var stepsTextView: TextView
    private val RC_SIGN_IN = 9001
    private lateinit var fitnessOptions: FitnessOptions
    private lateinit var cuenta: GoogleSignInAccount
    var resHearRateGoogle: Float = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_device)
        setUpNavigationButtons()

        fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_BASAL_METABOLIC_RATE, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_POINTS, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
            .build()

        findViewById<MaterialButton>(R.id.btnStartDevice).setOnClickListener {
            SportApp.startDevice = true
            sensor.setListener(this@ConnectDevice) // Pasa una instancia de ConnectDevice como oyente
            sensor.start()

            val measurements = sensor.generateManualMeasurements()
            val (powerOutput, maxHeartRate, restingHeartRate) = measurements ?: Triple(0, 0, 0)

            fetchRestingHeartRate { restingHeartRate ->
                Log.d("ConnectGoogle", "Ritmo cardíaco en reposo: $restingHeartRate")
                restingHeartRateTextView.text = getString(R.string.device_restingHeartRate) + " " + restingHeartRate.toString() + " bpm"
                SportApp.restingHeartRate = restingHeartRate.toInt()
            }

            getStepsClient(this) { totalPasos ->
                Log.d("ConnectGoogle", "Total de pasos del cliente: $totalPasos")
                stepsTextView.text = getString(R.string.device_steps) + " " + totalPasos.toString() + " steps"
                SportApp.steps = totalPasos
            }

            getCaloriesClient(this) { totalCalorias ->
                Log.d("ConnectGoogle", "Total de  calorias del cliente: $totalCalorias")
                SportApp.calories = totalCalorias.toInt()
            }

            onMeasurementsChanged(powerOutput, maxHeartRate, resHearRateGoogle.toInt())
        }

        // Configurar Google Sign-In
        configureSignIn()
        startSignIn(signInResultCallback)

        // Inicializa los TextViews
        powerOutputTextView = findViewById(R.id.powerOutputTextView)
        maxHeartRateTextView = findViewById(R.id.maxHeartRateTextView)
        restingHeartRateTextView = findViewById(R.id.restingHeartRateTextView)
        stepsTextView = findViewById(R.id.stepsTextView)

        // Inicializa datos de entrenamiento
        val dataList = getString(R.string.device).split(",") // Lista de datos

        val recyclerView = findViewById<RecyclerView>(R.id.rvTypeDevice)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = Adapter(dataList, object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedItemName = dataList[position]
                val message = getString(R.string.promt_device) + " " + selectedItemName + ""
                Log.d("DEBUG", "Item seleccionado : $position")
                showToast(this@ConnectDevice, message)
            }
        }, 0) // inicializa selectedItemIndex en 0 para el primer elemento

        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        val topNavigationView = findViewById<BottomNavigationView>(R.id.top_navigation)
        BadgeUtils.updateNotificationBadge(this, topNavigationView)
    }

    // Callback para manejar el resultado del inicio de sesión
    private val signInResultCallback = object : SignInResultCallback {
        override fun onSignInSuccess() {
            Log.d("ConnectGoogle", "Definido FitnessOptions: $fitnessOptions")
            Log.d("ConnectGoogle", "Definido googleFitManager: $fitnessOptions")

            // Llamar al método fetchRestingHeartRate para obtener el ritmo cardíaco en reposo
            fetchRestingHeartRate { restingHeartRate ->
                Log.d("ConnectGoogle", "Ritmo cardíaco en reposo: $restingHeartRate")
            }
        }

        override fun onSignInError(error: String) {
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
                Log.d("ConnectGoogle", "On Activity Result >> : ${result.signInAccount}")
            } else {
                Log.d("ConnectGoogle", "Error On Activity Result >> : ${result?.status?.statusMessage.toString()}")
            }
        } else {
            Log.d("ConnectGoogle", "On Activity Result LLego por el false")
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            val account: GoogleSignInAccount? = result.signInAccount
            Log.d("ConnectGoogle", "Conexión establecida correctamente : ${result.status} >> Cuenta : ${account?.displayName} >> ${account?.requestedScopes}")
            showToast(this@ConnectDevice, "Conectado a la cuenta de google de : ${account?.displayName}")
            signInResultCallback.onSignInSuccess()
        } else {
            Log.d("ConnectGoogle", "Error en la Conexión : ${result.status.statusMessage} SignInAccount : ${result.signInAccount}")
            signInResultCallback.onSignInError(result.status.statusMessage.toString())
        }
    }

    // Configura Google Sign-In
    fun configureSignIn() {
        Log.d("ConnectGoogle", "Inicio configuracion.")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestProfile()
            .requestId()
            .requestEmail()
            .addExtension(fitnessOptions)
            .build()

        val mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this@ConnectDevice) {
                Log.d("ConnectGoogle", "Error al crear el cliente mGoogleApiClient.")
            }
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        signIn(mGoogleApiClient)
    }

    fun fetchRestingHeartRate(callback: (Float) -> Unit) {
        cuenta = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        if (cuenta != null) {
            Log.d("ConnectGoogle", "Permisos de la cuenta. >>>> : ${cuenta.grantedScopes} ")
            Log.d("ConnectGoogle", "Permisos de la fitnesoptions. >>>> : ${fitnessOptions.impliedScopes} ")

            if (GoogleSignIn.hasPermissions(cuenta, fitnessOptions)) {
                val tiempoInicial = Calendar.getInstance().timeInMillis - TimeUnit.DAYS.toMillis(90)
                val tiempoFinal = Calendar.getInstance().timeInMillis

                Log.d("ConnectGoogle", "Tiempo Inicial. >>>> : $tiempoInicial ")
                Log.d("ConnectGoogle", "Tiempo Final. >>>> : $tiempoFinal ")

                try {
                    val solicitudFrecuenciaCardiacaReposo = DataReadRequest.Builder()
                        .read(DataType.TYPE_HEART_RATE_BPM)
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
                            callback(0f)
                        }
                } catch (e: IOException) {
                    Log.e("ConnectGoogle", "Error al enviar datos por Bluetooth: ${e.message}")
                }
            } else {
                Log.e("ConnectGoogle", "El usuario no ha otorgado los permisos necesarios.")
                callback(0f)
            }
        } else {
            Log.e("ConnectGoogle", "El usuario no ha iniciado sesión en Google Fit.")
            callback(0f)
        }
    }

    fun getStepsClient(contexto: Context, callback: (Int) -> Unit) {
        val cuenta = GoogleSignIn.getAccountForExtension(contexto, fitnessOptions)
        val tiempoActual = System.currentTimeMillis()
        val tiempoInicial = tiempoActual - TimeUnit.DAYS.toMillis(30)

        if (GoogleSignIn.hasPermissions(cuenta, fitnessOptions)) {
            val solicitudPasos = DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(tiempoInicial, tiempoActual, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
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
        } else {
            Log.e("ConnectGoogle", "El usuario no ha iniciado sesión en Google Fit.")
        }
    }

    fun getCaloriesClient(contexto: Context, callback: (Float) -> Unit) {
        val cuenta = GoogleSignIn.getAccountForExtension(contexto, fitnessOptions)
        val tiempoActual = System.currentTimeMillis()
        val tiempoInicial = tiempoActual - TimeUnit.DAYS.toMillis(7) // Obtener datos de los últimos 7 días

        val solicitudCalorias = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
            .setTimeRange(tiempoInicial, tiempoActual, TimeUnit.MILLISECONDS)
            .bucketByTime(1, TimeUnit.DAYS) // Agrupar las calorías por día
            .build()

        Fitness.getHistoryClient(contexto, cuenta)
            .readData(solicitudCalorias)
            .addOnSuccessListener { respuesta ->
                var totalCalorias = 0f
                respuesta.buckets.forEach { bucket ->
                    bucket.dataSets.forEach { dataSet ->
                        dataSet.dataPoints.forEach { dataPoint ->
                            val calorias = dataPoint.getValue(Field.FIELD_CALORIES).asFloat()
                            totalCalorias += calorias
                        }
                    }
                }
                callback(totalCalorias)
            }
            .addOnFailureListener { excepcion ->
                Log.e("ObtenerCaloriasCliente", "Error al obtener las calorías: ", excepcion)
                callback(0f)
            }
    }

    override fun onConnected() {
        Log.d("ConnectDevice", "Conexión establecida correctamente")
        bluetoothManager.sendData("Hola desde el dispositivo Android!")
    }

    override fun onDisconnected() {
        Log.d("ConnectDevice", "Conexión Bluetooth desconectada")
    }

    override fun onError(message: String) {
        Log.e("ConnectDevice", "Error en la conexión Bluetooth: $message")
    }

    private fun setUpNavigationButtons() {
        val topNavigationView = findViewById<BottomNavigationView>(R.id.top_navigation)
        topNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_suggestions -> {
                    utilRedirect.redirectToActivity(this, Suggests::class.java)
                    true
                }
                R.id.nav_home -> {
                    utilRedirect.redirectToActivity(this, Home::class.java)
                    true
                }
                R.id.nav_calendar -> {
                    utilRedirect.redirectToActivity(this, CalendarEvents::class.java)
                    true
                }
                R.id.nav_notifications -> {
                    utilRedirect.redirectToActivity(this, Notifications::class.java)
                    true
                }
                else -> false
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_run -> {
                    utilRedirect.redirectToActivity(this, DashboardTraining::class.java)
                    true
                }
                R.id.nav_clock -> {
                    utilRedirect.redirectToActivity(this, DashboardTraining::class.java)
                    true
                }
                R.id.nav_start -> {
                    utilRedirect.redirectToActivity(this, StartTraining::class.java)
                    true
                }
                R.id.nav_watch -> {
                    utilRedirect.redirectToActivity(this, ConnectDevice::class.java)
                    true
                }
                else -> false
            }
        }
    }

    private inner class Adapter(
        private val dataList: List<String>,
        private val listener: OnItemClickListener,
        private var selectedItemIndex: Int
    ) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(dataList[position], position)
        }

        override fun getItemCount(): Int = dataList.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            val textViewItem: TextView = itemView.findViewById(R.id.textViewItem)

            fun bind(item: String, position: Int) {
                textViewItem.text = item
                itemView.isSelected = (selectedItemIndex == position)
                itemView.setBackgroundResource(R.color.colorOnBackground)
            }

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectedItemIndex = position
                    notifyDataSetChanged()
                    listener.onItemClick(position)
                }
            }
        }
    }

    override fun onMeasurementsChanged(powerOutput: Int, maxHeartRate: Int, restingHeartRate: Int) {
        runOnUiThread {
            val mhr = 220 - SportApp.age
            powerOutputTextView.text = getString(R.string.device_powerOutpt) + " " + powerOutput + " watts"
            SportApp.powerOutput = powerOutput
            maxHeartRateTextView.text = getString(R.string.device_maxHeartRate) + " " + mhr + " bpm"
            SportApp.maxHeartRate = mhr
        }
    }

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}
