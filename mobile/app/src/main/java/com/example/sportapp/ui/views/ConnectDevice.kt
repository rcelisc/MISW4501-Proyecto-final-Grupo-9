package com.example.sportapp.ui.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportapp.LoginScreen
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.data.services.FitnessSensor
import com.example.sportapp.data.services.FitnessSensorListener

interface OnItemClickListener {
    fun onItemClick(position: Int)
}

class ConnectDevice : AppCompatActivity() , FitnessSensorListener {

    //private var selectedItemIndex: Int = RecyclerView.NO_POSITION
    private val sensor = FitnessSensor()
    private lateinit var powerOutputTextView: TextView
    private lateinit var maxHeartRateTextView: TextView
    private lateinit var restingHeartRateTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_device)
        val btnRunExe = findViewById<ImageView>(R.id.ivRunExe)
        val btnExit = findViewById<ImageView>(R.id.ivHome)
        val btnCalendar = findViewById<ImageView>(R.id.ivCalendar)
        val btnNotifications = findViewById<ImageView>(R.id.ivNotifications)
        val btnDashboard = findViewById<ImageView>(R.id.ivClockW)
        val btnDevice = findViewById<ImageView>(R.id.ivWatch)
        val btnStartDevice = findViewById<Button>(R.id.btnStartDevice)

        //Redirige a la Actividad Device
        btnDevice.setOnClickListener{
            val device = Intent(this, ConnectDevice::class.java)
            startActivity(device)
        }

        //Redirige a la Actividad Iniciar Entrenamiento.
        btnRunExe.setOnClickListener{
            val startTraining = Intent(this, StartTraining::class.java)
            startActivity(startTraining)
        }

        //Cerrar Sesion.
        btnExit.setOnClickListener{
            val exitApp = Intent(this, LoginScreen::class.java)
            startActivity(exitApp)
        }

        //Redirige a la Actividad Calendario de Eventos.
        btnCalendar.setOnClickListener{
            val calendar = Intent(this, CalendarEvents::class.java)
            startActivity(calendar)
        }

        btnNotifications.setOnClickListener{
            val notif = Intent(this, Notifications::class.java)
            startActivity(notif)
        }


        btnDashboard.setOnClickListener{
            val dash = Intent(this, DashboardTraining::class.java)
            startActivity(dash)
        }

        btnStartDevice.setOnClickListener{
            //val startDevice = Intent(this, DashboardTraining::class.java)
            //startActivity(startDevice)
            SportApp.startDevice = true


            sensor.setListener(this@ConnectDevice) // Pasa una instancia de ConnectDevice como oyente
            sensor.start()

            val measurements = sensor.generateManualMeasurements()
            val (powerOutput, maxHeartRate, restingHeartRate) = measurements ?: Triple(0, 0, 0)
            onMeasurementsChanged(powerOutput, maxHeartRate, restingHeartRate)

        }

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

//        fun setOnItemClickListener(listener: OnItemClickListener) {
//            this.listener = listener
//        }

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