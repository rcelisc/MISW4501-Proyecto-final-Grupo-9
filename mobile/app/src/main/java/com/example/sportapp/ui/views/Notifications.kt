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
import com.example.sportapp.data.model.CalendarEvent
import com.example.sportapp.data.model.EventsSuggestionsResponse
import com.example.sportapp.data.model.TrainingPlansResponse
import com.example.sportapp.data.repository.EventsSuggestionsRepository
import com.example.sportapp.data.repository.TrainingPlansRepository
import com.example.sportapp.data.services.RetrofitEventSuggestionsService
import com.example.sportapp.data.services.RetrofitTrainingPlansService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class Notifications : AppCompatActivity() {

    private lateinit var tableAdapter: TableAdapter
    private lateinit var tableAdapterEvents: TableAdapterEvents
    private val repositoryEvents = EventsSuggestionsRepository(RetrofitEventSuggestionsService.createApiService())
    var repository = TrainingPlansRepository(RetrofitTrainingPlansService.createApiService())



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestions)

        val ivHome = findViewById<ImageView>(R.id.ivHome)
        val ivRunExe = findViewById<ImageView>(R.id.ivRunExe)
        val ivCalendar = findViewById<ImageView>(R.id.ivCalendar)

        ivHome.setOnClickListener{
            val home = Intent(this, Home::class.java)
            startActivity(home)
        }

        ivRunExe.setOnClickListener{
            val home = Intent(this, StartTraining::class.java)
            startActivity(home)
        }

        ivCalendar.setOnClickListener{
            val calendar = Intent(this, CalendarEvents::class.java)
            startActivity(calendar)
        }

        //Tabla de Suggestions
        val recyclerView = findViewById<RecyclerView>(R.id.rvSugestions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tableAdapter = TableAdapter()
        recyclerView.adapter = tableAdapter

        //Tabla de eventos
        val recyclerViewEvents = findViewById<RecyclerView>(R.id.rvEvents)
        recyclerViewEvents.layoutManager = LinearLayoutManager(this)
        tableAdapterEvents = TableAdapterEvents()
        recyclerViewEvents.adapter = tableAdapterEvents

        //SportApp.profile
        repository.getTrainingPlans("Beginner").enqueue(object : Callback<List<TrainingPlansResponse>> {
            override fun onResponse(call: Call<List<TrainingPlansResponse>>, response: Response<List<TrainingPlansResponse>>) {
                if (response.isSuccessful) {
                    val triningResponse = response.body()
                    if (triningResponse != null) {
                        Log.d("DEBUG", "Se encontraron Planes")
                        for (plan in triningResponse) {
                            tableAdapter.addItem(plan)
                        }
                    } else {
                        // Manejar el caso en que la respuesta del servidor sea nula
                        Log.d("DEBUG", "La respuesta del servidor es nula")
                    }
                } else {
                    // Manejar el caso en que la respuesta del servidor no sea exitosa
                    Log.d("DEBUG", "La llamada al servicio no fue exitosa. Código de error: ${response.code()}")
                }

                repositoryEvents.getEventsSuggestions().enqueue(object : Callback<List<EventsSuggestionsResponse>> {
                    override fun onResponse(call: Call<List<EventsSuggestionsResponse>>, response: Response<List<EventsSuggestionsResponse>>) {
                        if (response.isSuccessful) {
                            val eventResponse = response.body()
                            if (eventResponse != null) {
                                Log.d("DEBUG", "Se encontraron Eventos")
                                for (event in eventResponse) {
                                    tableAdapterEvents.addItem(event)
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

                    override fun onFailure(call: Call<List<EventsSuggestionsResponse>>, t: Throwable) {
                        // Manejar errores de red o de llamada al servicio
                        Log.d("DEBUG", "Error en la llamada al servicio: ${t.message}")
                        t.printStackTrace()
                    }
                })
            }

            override fun onFailure(call: Call<List<TrainingPlansResponse>>, t: Throwable) {
                // Manejar errores de red o de llamada al servicio
                Log.d("DEBUG", "Error en la llamada al servicio: ${t.message}")
                t.printStackTrace()
            }
        })





    }

    class TableAdapter : RecyclerView.Adapter<TableAdapter.ViewHolder>() {

        private val data = mutableListOf<TrainingPlansResponse>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layot_seggestion, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = data[position]
            holder.bind(item)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        fun addItem(item: TrainingPlansResponse) {
            data.add(item)
            notifyItemInserted(data.size - 1)
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val column1TextView: TextView = itemView.findViewById(R.id.textViewColumn1)
            val column2TextView: TextView = itemView.findViewById(R.id.textViewColumn2)
            val column3TextView: TextView = itemView.findViewById(R.id.textViewColumn3)
            val column4TextView: TextView = itemView.findViewById(R.id.textViewColumn4)


            fun bind(item: TrainingPlansResponse) {
                column1TextView.text = item.description
                column2TextView.text = item.duration
                column3TextView.text = item.exercises
                column4TextView.text = item.objectives
            }
        }
    }

    class TableAdapterEvents : RecyclerView.Adapter<TableAdapterEvents.ViewHolder>() {

        private val dataEvent = mutableListOf<EventsSuggestionsResponse>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_event_sugg, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = dataEvent[position]
            holder.bind(item)
        }

        override fun getItemCount(): Int {
            return dataEvent.size
        }

        fun addItem(item: EventsSuggestionsResponse) {
            dataEvent.add(item)
            notifyItemInserted(dataEvent.size - 1)
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val column1TextView: TextView = itemView.findViewById(R.id.textViewColumn1)
            val column2TextView: TextView = itemView.findViewById(R.id.textViewColumn2)
            val column3TextView: TextView = itemView.findViewById(R.id.textViewColumn3)
            val column4TextView: TextView = itemView.findViewById(R.id.textViewColumn4)


            fun bind(item: EventsSuggestionsResponse) {
                column1TextView.text = item.description
                column2TextView.text = item.name
                column3TextView.text = item.fee.toString()
                column4TextView.text = item.eventDate?.toString()
                Log.d("DEBUG", "Error en la llamada al servicio: ${item.eventDate?.toString()}")

            }
        }
    }
}