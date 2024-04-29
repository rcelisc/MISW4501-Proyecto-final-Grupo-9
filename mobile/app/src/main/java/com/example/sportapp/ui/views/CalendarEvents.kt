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

        val ivHome = findViewById<ImageView>(R.id.ivHome)
        val ivRunExe = findViewById<ImageView>(R.id.ivRunExe)
        val ivNotif = findViewById<ImageView>(R.id.ivNotifications)
        val btnDevice = findViewById<ImageView>(R.id.ivWatch)




        //Redirige a la Actividad Device
        btnDevice.setOnClickListener{
            val device = Intent(this, ConnectDevice::class.java)
            startActivity(device)
        }

        ivHome.setOnClickListener{
            val home = Intent(this, Home::class.java)
            startActivity(home)
        }

        ivRunExe.setOnClickListener{
            val home = Intent(this, StartTraining::class.java)
            startActivity(home)
        }

        ivNotif.setOnClickListener{
            val notif = Intent(this, Notifications::class.java)
            startActivity(notif)
        }


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