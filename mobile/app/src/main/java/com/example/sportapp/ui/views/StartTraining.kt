package com.example.sportapp.ui.views

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.UtilRedirect
import com.example.sportapp.data.model.StartTrainingResponse
import com.example.sportapp.data.repository.StartTrainingRepository
import com.example.sportapp.data.services.RetrofitStartTrainingService
import com.example.sportapp.ui.home.Home
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StartTraining : AppCompatActivity() {


    //private val repository = StartTrainingRepository(RetrofitStartTrainingService.createApiService())
    private lateinit var chronometer: Chronometer
    private val utilRedirect = UtilRedirect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_training)
        setUpNavigationButtons()
        //Inicializa datos de entrenamiento.
        val dataList = listOf("Natacion", "Ciclismo", "Running") // Lista de datos
        val recyclerView = findViewById<RecyclerView>(R.id.rvTypeTraining)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyAdapter(dataList)
        chronometer = findViewById(R.id.chronometer)

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
    private class MyAdapter(private val dataList: List<String>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

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

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textViewItem: TextView = itemView.findViewById(R.id.textViewItem)
        }
    }

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
}
