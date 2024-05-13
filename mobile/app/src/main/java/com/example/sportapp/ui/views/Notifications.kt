package com.example.sportapp.ui.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.UtilRedirect
import com.example.sportapp.data.model.EventsSuggestionsResponse
import com.example.sportapp.data.model.TrainingPlansResponse
import com.example.sportapp.data.repository.EventsSuggestionsRepository
import com.example.sportapp.data.repository.TrainingPlansRepository
import com.example.sportapp.data.services.RetrofitEventSuggestionsService
import com.example.sportapp.data.services.RetrofitTrainingPlansService
import com.example.sportapp.ui.home.Home
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Notifications : AppCompatActivity() {

    private lateinit var tableAdapter: TableAdapter
    private lateinit var tableAdapterEvents: TableAdapterEvents
    private val repositoryEvents = EventsSuggestionsRepository(RetrofitEventSuggestionsService.createApiService())
    private val repository = TrainingPlansRepository(RetrofitTrainingPlansService.createApiService())
    private val utilRedirect = UtilRedirect()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suggestions)
        setUpNavigationButtons()
        setUpRecyclerViews()
        fetchTrainingPlans()
        fetchEventSuggestions()
    }

    private fun setUpRecyclerViews() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvSugestions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tableAdapter = TableAdapter()
        recyclerView.adapter = tableAdapter

        val recyclerViewEvents = findViewById<RecyclerView>(R.id.rvEvents)
        recyclerViewEvents.layoutManager = LinearLayoutManager(this)
        tableAdapterEvents = TableAdapterEvents()
        recyclerViewEvents.adapter = tableAdapterEvents
    }

    private fun fetchTrainingPlans() {
        repository.getTrainingPlans(SportApp.profile).enqueue(object : Callback<List<TrainingPlansResponse>> {
            override fun onResponse(call: Call<List<TrainingPlansResponse>>, response: Response<List<TrainingPlansResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let { plans ->
                        plans.forEach(tableAdapter::addItem)
                    } ?: Log.d("DEBUG", "Server response is null for Training Plans")
                } else {
                    Log.d("DEBUG", "Failed to fetch Training Plans. Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<TrainingPlansResponse>>, t: Throwable) {
                Log.d("DEBUG", "Error fetching Training Plans: ${t.message}")
            }
        })
    }

    private fun fetchEventSuggestions() {
        repositoryEvents.getEventsSuggestions().enqueue(object : Callback<List<EventsSuggestionsResponse>> {
            override fun onResponse(call: Call<List<EventsSuggestionsResponse>>, response: Response<List<EventsSuggestionsResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let { events ->
                        events.forEach(tableAdapterEvents::addItem)
                    } ?: Log.d("DEBUG", "Server response is null for Events")
                } else {
                    Log.d("DEBUG", "Failed to fetch Events. Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<EventsSuggestionsResponse>>, t: Throwable) {
                Log.d("DEBUG", "Error fetching Events: ${t.message}")
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

    class TableAdapter : RecyclerView.Adapter<TableAdapter.ViewHolder>() {
        private val data = mutableListOf<TrainingPlansResponse>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_suggestion, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(data[position])
        }

        override fun getItemCount() = data.size

        fun addItem(item: TrainingPlansResponse) {
            data.add(item)
            notifyItemInserted(data.size - 1)
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: TrainingPlansResponse) {
                itemView.findViewById<TextView>(R.id.textViewColumn1).text = item.description
                itemView.findViewById<TextView>(R.id.textViewColumn2).text = item.duration
                itemView.findViewById<TextView>(R.id.textViewColumn3).text = item.exercises
                itemView.findViewById<TextView>(R.id.textViewColumn4).text = item.objectives
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
            holder.bind(dataEvent[position])
        }

        override fun getItemCount() = dataEvent.size

        fun addItem(item: EventsSuggestionsResponse) {
            dataEvent.add(item)
            notifyItemInserted(dataEvent.size - 1)
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: EventsSuggestionsResponse) {
                itemView.findViewById<TextView>(R.id.textViewColumn1).text = item.description
                itemView.findViewById<TextView>(R.id.textViewColumn2).text = item.name
                itemView.findViewById<TextView>(R.id.textViewColumn3).text = item.fee.toString()
                itemView.findViewById<TextView>(R.id.textViewColumn4).text = item.eventDate?.toString()
            }
        }
    }
}
