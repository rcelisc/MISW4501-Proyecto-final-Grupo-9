package com.example.sportapp.ui.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportapp.R
import com.example.sportapp.ui.home.Home
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.example.sportapp.SportApp
import com.example.sportapp.UtilRedirect
import com.example.sportapp.data.repository.DataRepository
import com.example.sportapp.data.services.RetrofitEventsManagementQueries
import com.example.sportapp.data.model.CalendarEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CalendarEvents : AppCompatActivity() {

    private lateinit var tableAdapter: TableAdapter
    private val repository = DataRepository(RetrofitEventsManagementQueries.createApiService())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_events)

        setUpNavigationButtons()

        //Tabla de eventos
        val recyclerView = findViewById<RecyclerView>(R.id.rvEvents)
        recyclerView.layoutManager = LinearLayoutManager(this)

        tableAdapter = TableAdapter()
        recyclerView.adapter = tableAdapter


        repository.getCalendarEvents(SportApp.userCodeId).enqueue(object : Callback<List<CalendarEvent>> {
            override fun onResponse(call: Call<List<CalendarEvent>>, response: Response<List<CalendarEvent>>) {
                if (response.isSuccessful) {
                    val calendarResponse = response.body()
                    if (calendarResponse != null) {
                        for (event in calendarResponse) {
                            tableAdapter.addItem(event)
                        }
                    } else {
                        // Manejar el caso en que la respuesta del servidor sea nula
                        Log.d("DEBUG", "La respuesta del servidor es nula")
                    }
                } else {
                    // Manejar el caso en que la respuesta del servidor no sea exitosa
                    Log.d("DEBUG", "La llamada al servicio no fue exitosa. Código de error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<CalendarEvent>>, t: Throwable) {
                // Manejar errores de red o de llamada al servicio
                Log.d("DEBUG", "Error en la llamada al servicio: ${t.message}")
                t.printStackTrace()
            }
        })


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

    class TableAdapter : RecyclerView.Adapter<TableAdapter.ViewHolder>() {

        private val data = mutableListOf<CalendarEvent>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_event, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = data[position]
            holder.bind(item)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        fun addItem(item: CalendarEvent) {
            data.add(item)
            notifyItemInserted(data.size - 1)
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val column1TextView: TextView = itemView.findViewById(R.id.textViewColumn1)
            val column2TextView: TextView = itemView.findViewById(R.id.textViewColumn2)
            val column3TextView: TextView = itemView.findViewById(R.id.textViewColumn3)


            fun bind(item: CalendarEvent) {
                column1TextView.text = item.nombre
                column2TextView.text = item.fecha
                column3TextView.text = item.descripción
            }
        }
    }

}