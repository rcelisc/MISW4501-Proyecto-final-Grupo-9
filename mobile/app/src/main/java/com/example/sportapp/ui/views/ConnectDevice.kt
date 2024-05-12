package com.example.sportapp.ui.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportapp.BluetoothManager
import com.example.sportapp.GoogleFitManager
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.UtilRedirect
import com.example.sportapp.data.services.FitnessSensor
import com.example.sportapp.data.services.FitnessSensorListener
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
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
    private val REQUEST_CODE_PERMISSIONS = 101
    private val RC_SIGN_IN = 9001
    private lateinit var googleFitManager: GoogleFitManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_device)
        val btnStartDevice = findViewById<Button>(R.id.btnStartDevice)
        setUpNavigationButtons()


        btnStartDevice.setOnClickListener{
            SportApp.startDevice = true
            sensor.setListener(this@ConnectDevice) // Pasa una instancia de ConnectDevice como oyente
            sensor.start()

            val measurements = sensor.generateManualMeasurements()
            val (powerOutput, maxHeartRate, restingHeartRate) = measurements ?: Triple(0, 0, 0)
            onMeasurementsChanged(powerOutput, maxHeartRate, restingHeartRate)

        }

        // Configurar Google Sign-In
        configureSignIn()


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

        //Inicializa datos de entrenamiento.
        val dataList = getString(R.string.device).split(",") // Lista de datos

        val recyclerView = findViewById<RecyclerView>(R.id.rvTypeDevice)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = Adapter(dataList, object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val selectedItemName = dataList[position]
                val message =  getString(R.string.promt_device) + " " + selectedItemName + ""
                Log.d("DEBUG", "Item seleccionado : " + position.toString())
                showToast(this@ConnectDevice,  message)

            }
        }, 0) // inicializa selectedItemIndex en 0 para el primer elemento

        recyclerView.adapter = adapter
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

            }
        }
    }

    // Implementa el método para manejar el resultado del inicio de sesión
    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            //val account = result.signInAccount
            val account: GoogleSignInAccount? = result.signInAccount
            Log.d("ConnectGoogle", "Conexión establecida correctamente : " + result.status + " " + account)
            showToast(this@ConnectDevice,  "Conectado a la cuenta de google.")

        } else {
            // El inicio de sesión falló, muestra un mensaje de error o toma otras medidas.
            Log.d("ConnectGoogle", "Error en la Conexión : " + result.status.statusMessage +  " SignInAccount : " + result.signInAccount)
        }
    }

    // Configura Google Sign-In
    fun configureSignIn() {
        // Crea una instancia de GoogleSignInOptions con las opciones deseadas
        Log.d("ConnectGoogle", "Inicion configuracion.")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Crea un cliente de GoogleApiClient
        val mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this@ConnectDevice) {
                Log.d("ConnectGoogle", "Error al crear el cliente mGoogleApiClient . ")
            }
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()


        Log.d("ConnectGoogle", "GSO " + gso.account.toString())

        // Iniciar sesión con Google
        signIn(mGoogleApiClient)
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

    fun inputStreamToJson(inputStream: InputStream): JSONObject {
        val jsonStringBuilder = StringBuilder()

        // Lee los datos del InputStream línea por línea y los agrega al StringBuilder
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            jsonStringBuilder.append(line)
        }

        // Crea un objeto JSONObject a partir de la cadena JSON
        return JSONObject(jsonStringBuilder.toString())
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
        val btnRunExe = findViewById<ImageView>(R.id.ivRunExe)
        val btnExit = findViewById<ImageView>(R.id.ivHome)
        val btnCalendar = findViewById<ImageView>(R.id.ivCalendar)
        val btnNotifications = findViewById<ImageView>(R.id.ivNotifications)
        val btnDashboard = findViewById<ImageView>(R.id.ivClockW)
        val btnDevice = findViewById<ImageView>(R.id.ivWatch)
        val btnSuggestRoutes = findViewById<ImageView>(R.id.ivRun)
        val btnSuggest = findViewById<ImageView>(R.id.ivSugerencias)

        btnDevice.setOnClickListener{ UtilRedirect().redirectToDeviceActivity(this)}
        btnRunExe.setOnClickListener{ UtilRedirect().redirectToStartTrainingActivity(this)}
        btnExit.setOnClickListener{ UtilRedirect().redirectToHomeActivity(this)}
        btnCalendar.setOnClickListener{ UtilRedirect().redirectToCalendarEventsActivity(this)}
        btnNotifications.setOnClickListener{ UtilRedirect().redirectToNotificationsActivity(this)}
        btnDashboard.setOnClickListener{ UtilRedirect().redirectToDashboardTrainingActivity(this)}
        btnSuggestRoutes.setOnClickListener{ UtilRedirect().redirectToSuggestRoutesActivity(this)}
        btnSuggest.setOnClickListener{ UtilRedirect().redirectToSuggestsActivity(this)}
    }

    private inner class Adapter(private val dataList: List<String>, private val listener: OnItemClickListener, private var selectedItemIndex: Int) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        //private var listener: OnItemClickListener? = null

        //private var selectedItemIndex: Int = RecyclerView.NO_POSITION

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = dataList[position]
            holder.textViewItem.text = item
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
            val textViewItem: TextView = itemView.findViewById(R.id.textViewItem)

            fun bind(item: String, position: Int) {
                textViewItem.text = item
                itemView.isSelected = (selectedItemIndex == position)

                  //if (selectedItemIndex == position) {
                  // Cambia el color de fondo del elemento seleccionado


                itemView.setBackgroundResource(R.color.backcolorapp)
//                } else {
//                    // Restablece el color de fondo del elemento no seleccionado
//                    itemView.setBackgroundResource(R.color.backtittle)
//                }
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
            powerOutputTextView.text = "Power Output: $powerOutput watts"
            maxHeartRateTextView.text = "Max Heart Rate: $maxHeartRate bpm"
            restingHeartRateTextView.text = "Resting Heart Rate: $restingHeartRate bpm"
            Log.d("DEBUG", "Entro a escribir los datos.: ")
        }
    }

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}

