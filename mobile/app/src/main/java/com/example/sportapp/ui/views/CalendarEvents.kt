package com.example.sportapp.ui.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.utils.UtilRedirect
import com.example.sportapp.data.repository.DataRepository
import com.example.sportapp.data.services.RetrofitClient
import com.example.sportapp.data.model.CalendarEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.util.Log
import com.example.sportapp.ui.home.Home
import com.example.sportapp.ui.views.*
import com.google.android.material.button.MaterialButton

class CalendarEvents : AppCompatActivity() {
    private lateinit var tableAdapter: TableAdapter
    private val repository = DataRepository(RetrofitClient.createEventsService(this))
    private val utilRedirect = UtilRedirect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_events)

        setUpNavigationButtons()

        val recyclerView = findViewById<RecyclerView>(R.id.rvEvents)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tableAdapter = TableAdapter()
        recyclerView.adapter = tableAdapter

        repository.getCalendarEvents(SportApp.userCodeId).enqueue(object : Callback<List<CalendarEvent>> {
            override fun onResponse(call: Call<List<CalendarEvent>>, response: Response<List<CalendarEvent>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        tableAdapter.addItems(it)
                    } ?: Log.d("DEBUG", "Server response is null")
                } else {
                    Log.d("DEBUG", "Service call not successful. Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<CalendarEvent>>, t: Throwable) {
                Log.d("DEBUG", "Error calling the service: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    private fun setUpNavigationButtons() {
        findViewById<MaterialButton>(R.id.ivRunExe).setOnClickListener {
            utilRedirect.redirectToActivity(this, StartTraining::class.java)
        }
        findViewById<MaterialButton>(R.id.ivHome).setOnClickListener {
            utilRedirect.redirectToActivity(this, Home::class.java)
        }
        findViewById<MaterialButton>(R.id.ivCalendar).setOnClickListener {
            utilRedirect.redirectToActivity(this, CalendarEvents::class.java)
        }
        findViewById<MaterialButton>(R.id.ivNotifications).setOnClickListener {
            utilRedirect.redirectToActivity(this, Notifications::class.java)
        }
        findViewById<MaterialButton>(R.id.ivClockW).setOnClickListener {
            utilRedirect.redirectToActivity(this, DashboardTraining::class.java)
        }
        findViewById<MaterialButton>(R.id.ivWatch).setOnClickListener {
            utilRedirect.redirectToActivity(this, ConnectDevice::class.java)
        }
        findViewById<MaterialButton>(R.id.ivRun).setOnClickListener {
            utilRedirect.redirectToActivity(this, SuggestRoutes::class.java)
        }
        findViewById<MaterialButton>(R.id.ivSugerencias).setOnClickListener {
            utilRedirect.redirectToActivity(this, Suggests::class.java)
        }
    }

    class TableAdapter : RecyclerView.Adapter<TableAdapter.ViewHolder>() {
        private val data = mutableListOf<CalendarEvent>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_event, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(data[position])
        }

        override fun getItemCount() = data.size

        fun addItems(items: List<CalendarEvent>) {
            val startInsertPosition = data.size
            data.addAll(items)
            notifyItemRangeInserted(startInsertPosition, items.size)
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val column1TextView: TextView = itemView.findViewById(R.id.textViewColumn1)
            private val column2TextView: TextView = itemView.findViewById(R.id.textViewColumn2)
            private val column3TextView: TextView = itemView.findViewById(R.id.textViewColumn3)

            fun bind(item: CalendarEvent) {
                column1TextView.text = item.nombre
                column2TextView.text = item.fecha
                column3TextView.text = item.descripción
            }
        }
    }}