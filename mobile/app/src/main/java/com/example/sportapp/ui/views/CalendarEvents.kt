package com.example.sportapp.ui.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportapp.R
import com.example.sportapp.data.model.Event
import com.example.sportapp.data.model.CalendarEventsAndServicesResponse
import com.example.sportapp.data.model.Service
import com.example.sportapp.data.repository.DataRepository
import com.example.sportapp.data.services.RetrofitClient
import com.example.sportapp.utils.UtilRedirect
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CalendarEvents : AppCompatActivity() {
    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var servicesAdapter: ServicesAdapter
    private val repository = DataRepository(RetrofitClient.getEventsService(this))
    private val utilRedirect = UtilRedirect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_events)

        setUpNavigationButtons()

        val eventsRecyclerView = findViewById<RecyclerView>(R.id.rvEvents)
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsAdapter = EventsAdapter()
        eventsRecyclerView.adapter = eventsAdapter

        val servicesRecyclerView = findViewById<RecyclerView>(R.id.rvServices)
        servicesRecyclerView.layoutManager = LinearLayoutManager(this)
        servicesAdapter = ServicesAdapter()
        servicesRecyclerView.adapter = servicesAdapter

        fetchCalendarEventsAndServices()
    }

    private fun fetchCalendarEventsAndServices() {
        Log.d("DEBUG", "Fetching calendar events and services")
        repository.getCalendarEventsAndServices().enqueue(object : Callback<CalendarEventsAndServicesResponse> {
            override fun onResponse(call: Call<CalendarEventsAndServicesResponse>, response: Response<CalendarEventsAndServicesResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("DEBUG", "Fetched events: ${it.events}")
                        Log.d("DEBUG", "Fetched services: ${it.services}")
                        eventsAdapter.addItems(it.events)
                        servicesAdapter.addItems(it.services)
                    } ?: Log.d("DEBUG", "Server response is null")
                } else {
                    Log.d("DEBUG", "Service call not successful. Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CalendarEventsAndServicesResponse>, t: Throwable) {
                Log.d("DEBUG", "Error calling the service: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    private fun setUpNavigationButtons() {
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

        val topNavigationView = findViewById<BottomNavigationView>(R.id.top_navigation)
        topNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_suggestions -> {
                    utilRedirect.redirectToActivity(this, Suggests::class.java)
                    true
                }

                R.id.nav_home -> {
                    utilRedirect.redirectToActivity(this, LoginScreen::class.java)
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
    }

    class EventsAdapter : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
        private val data = mutableListOf<Event>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_layout_event, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val event = data[position]
            holder.bind(event)
            Log.d("DEBUG", "Binding event item: $event")
        }

        override fun getItemCount() = data.size

        fun addItems(items: List<Event>) {
            val startInsertPosition = data.size
            data.addAll(items)
            notifyItemRangeInserted(startInsertPosition, items.size)
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val eventNameTextView: TextView = itemView.findViewById(R.id.textViewEventName)
            private val eventDateTextView: TextView = itemView.findViewById(R.id.textViewEventDate)
            private val eventDescriptionTextView: TextView =
                itemView.findViewById(R.id.textViewEventDescription)
            private val eventFeeTextView: TextView = itemView.findViewById(R.id.textViewEventFee)

            fun bind(event: Event) {
                eventNameTextView.text = event.name
                eventDateTextView.text = event.event_date
                eventDescriptionTextView.text = event.description
                eventFeeTextView.text = event.fee.toString()
            }
        }
    }

    class ServicesAdapter : RecyclerView.Adapter<ServicesAdapter.ViewHolder>() {
        private val data = mutableListOf<Service>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_layout_service, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val service = data[position]
            holder.bind(service)
            Log.d("DEBUG", "Binding service item: $service")
        }

        override fun getItemCount() = data.size

        fun addItems(items: List<Service>) {
            val startInsertPosition = data.size
            data.addAll(items)
            notifyItemRangeInserted(startInsertPosition, items.size)
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val serviceNameTextView: TextView =
                itemView.findViewById(R.id.textViewServiceName)
            private val serviceDescriptionTextView: TextView =
                itemView.findViewById(R.id.textViewServiceDescription)
            private val serviceRateTextView: TextView =
                itemView.findViewById(R.id.textViewServiceRate)
            private val serviceStatusTextView: TextView =
                itemView.findViewById(R.id.textViewServiceStatus)

            fun bind(service: Service) {
                serviceNameTextView.text = service.name
                serviceDescriptionTextView.text = service.description
                serviceRateTextView.text = service.rate.toString()
                serviceStatusTextView.text = service.status
            }
        }
    }

}
