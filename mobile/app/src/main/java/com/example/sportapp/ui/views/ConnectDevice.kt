package com.example.sportapp.ui.views

import android.os.Bundle
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
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.UtilRedirect
import com.example.sportapp.data.services.FitnessSensor
import com.example.sportapp.data.services.FitnessSensorListener
import com.example.sportapp.ui.home.Home

class ConnectDevice : AppCompatActivity(), FitnessSensorListener {
    private val sensor = FitnessSensor()
    private lateinit var powerOutputTextView: TextView
    private lateinit var maxHeartRateTextView: TextView
    private lateinit var restingHeartRateTextView: TextView
    private val utilRedirect = UtilRedirect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_device)

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

    private fun setUpNavigationButtons() {
        findViewById<ImageView>(R.id.ivRunExe).setOnClickListener { utilRedirect.redirectToActivity(this, StartTraining::class.java) }
        findViewById<ImageView>(R.id.ivHome).setOnClickListener { utilRedirect.redirectToActivity(this, Home::class.java) }
        findViewById<ImageView>(R.id.ivCalendar).setOnClickListener { utilRedirect.redirectToActivity(this, CalendarEvents::class.java) }
        findViewById<ImageView>(R.id.ivNotifications).setOnClickListener { utilRedirect.redirectToActivity(this, Notifications::class.java) }
        findViewById<ImageView>(R.id.ivClockW).setOnClickListener { utilRedirect.redirectToActivity(this, DashboardTraining::class.java) }
        findViewById<ImageView>(R.id.ivWatch).setOnClickListener { utilRedirect.redirectToActivity(this, ConnectDevice::class.java) }
        findViewById<ImageView>(R.id.ivRun).setOnClickListener { utilRedirect.redirectToActivity(this, SuggestRoutes::class.java) }
        findViewById<ImageView>(R.id.ivSugerencias).setOnClickListener { utilRedirect.redirectToActivity(this, Suggests::class.java) }
    }

    override fun onMeasurementsChanged(powerOutput: Int, maxHeartRate: Int, restingHeartRate: Int) {
        runOnUiThread {
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
}
