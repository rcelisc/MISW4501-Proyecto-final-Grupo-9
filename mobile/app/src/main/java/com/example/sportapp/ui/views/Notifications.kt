package com.example.sportapp.ui.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.data.model.EventSuggestion
import com.example.sportapp.data.model.TrainingPlansResponse
import com.example.sportapp.data.model.User
import com.example.sportapp.data.repository.EventsRepository
import com.example.sportapp.data.repository.TrainingPlansRepository
import com.example.sportapp.data.services.RetrofitClient
import com.example.sportapp.ui.home.Home
import com.example.sportapp.utils.UtilRedirect
import com.example.sportapp.utils.BadgeUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Notifications : AppCompatActivity() {

    private lateinit var tableAdapter: TableAdapter
    private lateinit var tableAdapterEvents: TableAdapterEvents
    private lateinit var tableAdapterRoutes: TableAdapterRoutes
    private val repositoryEvents = EventsRepository(RetrofitClient.getEventsService(this))
    private val repository = TrainingPlansRepository(RetrofitClient.createTrainingPlansService(this))
    private val utilRedirect = UtilRedirect()
    private var suggestedRoute: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        setUpNavigationButtons()
        setUpRecyclerViews()
        fetchUserProfile()
    }

    override fun onResume() {
        super.onResume()
        val topNavigationView = findViewById<BottomNavigationView>(R.id.top_navigation)
        BadgeUtils.updateNotificationBadge(this, topNavigationView)
        fetchTrainingPlans()
        fetchEventSuggestions()
        displayRouteSuggestion()
    }

    private fun setUpRecyclerViews() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvSuggestions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tableAdapter = TableAdapter { position, suggestionId -> dismissSuggestion(position, suggestionId) }
        recyclerView.adapter = tableAdapter

        val recyclerViewEvents = findViewById<RecyclerView>(R.id.rvEvents)
        recyclerViewEvents.layoutManager = LinearLayoutManager(this)
        tableAdapterEvents = TableAdapterEvents { position, suggestionId -> dismissEvent(position, suggestionId) }
        recyclerViewEvents.adapter = tableAdapterEvents

        val recyclerViewRoutes = findViewById<RecyclerView>(R.id.rvRoutes)
        recyclerViewRoutes.layoutManager = LinearLayoutManager(this)
        tableAdapterRoutes = TableAdapterRoutes { position, suggestionId -> dismissRoute(position, suggestionId) }
        recyclerViewRoutes.adapter = tableAdapterRoutes
    }

    private fun fetchUserProfile() {
        val userId = SportApp.userCodeId
        val userService = RetrofitClient.createUserService(this)
        userService.getUserById(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        SportApp.profile = it.profile_type
                        Log.d("Notifications", "User profile fetched and updated: ${it.profile_type}")
                        fetchTrainingPlans()
                        fetchEventSuggestions()
                    } ?: run {
                        Log.d("Notifications", "User data is null")
                    }
                } else {
                    Log.d("Notifications", "Failed to fetch user data: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("Notifications", "Error fetching user data: ${t.message}")
            }
        })
    }

    private fun fetchTrainingPlans() {
        repository.getTrainingPlans(SportApp.profile).enqueue(object : Callback<List<TrainingPlansResponse>> {
            override fun onResponse(call: Call<List<TrainingPlansResponse>>, response: Response<List<TrainingPlansResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let { plans ->
                        Log.d("Notifications", "Training Plans found: $plans")
                        tableAdapter.clearItems()
                        plans.forEach {
                            if (!isDismissed("plan_${it.id}")) {
                                tableAdapter.addItem(it)
                            }
                        }
                        updateNotificationBadge()
                    } ?: Log.d("Notifications", "Server response is null for Training Plans")
                } else {
                    Log.d("Notifications", "Failed to fetch Training Plans. Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<TrainingPlansResponse>>, t: Throwable) {
                Log.d("Notifications", "Error fetching Training Plans: ${t.message}")
            }
        })
    }

    private fun fetchEventSuggestions() {
        repositoryEvents.getCalendarEvents(SportApp.userCodeId).enqueue(object : Callback<List<EventSuggestion>> {
            override fun onResponse(call: Call<List<EventSuggestion>>, response: Response<List<EventSuggestion>>) {
                if (response.isSuccessful) {
                    response.body()?.let { events ->
                        Log.d("Notifications", "Events found: $events")
                        tableAdapterEvents.clearItems()
                        events.forEach {
                            if (!isDismissed("event_${it.id}")) {
                                tableAdapterEvents.addItem(it)
                            }
                        }
                        updateNotificationBadge()
                    } ?: Log.d("Notifications", "Server response is null for Events")
                } else {
                    Log.d("Notifications", "Failed to fetch Events. Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<EventSuggestion>>, t: Throwable) {
                Log.d("Notifications", "Error fetching Events: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    private fun displayRouteSuggestion() {
        val sharedPreferences = getSharedPreferences("SportAppPrefs", Context.MODE_PRIVATE)
        suggestedRoute = sharedPreferences.getString("suggestedRoute", null)

        if (suggestedRoute == null) {
            suggestedRoute = getRandomRoute()
            sharedPreferences.edit().putString("suggestedRoute", suggestedRoute).apply()
        }

        suggestedRoute?.let { route ->
            if (!isDismissed("route_0")) {
                tableAdapterRoutes.clearItems()
                tableAdapterRoutes.addItem(route)
                updateNotificationBadge()
            }
        }
    }

    private fun getRandomRoute(): String {
        val routes = listOf(
            "Scenic Route along the River",
            "Urban Trail through Downtown",
            "Mountain Path with Elevation",
            "Forest Loop near the Park",
            "Beachfront Path by the Sea"
        )
        return routes.random()
    }

    private fun dismissSuggestion(position: Int, suggestionId: String) {
        Log.d("Notifications", "Dismissing suggestion with ID: $suggestionId at position: $position")
        tableAdapter.dismissItem(position)
        addDismissedSuggestion(suggestionId)
        updateNotificationBadge()
    }

    private fun dismissEvent(position: Int, suggestionId: String) {
        Log.d("Notifications", "Dismissing event with ID: $suggestionId at position: $position")
        tableAdapterEvents.dismissItem(position)
        addDismissedSuggestion(suggestionId)
        updateNotificationBadge()
    }

    private fun dismissRoute(position: Int, suggestionId: String) {
        Log.d("Notifications", "Dismissing route with ID: $suggestionId at position: $position")
        tableAdapterRoutes.dismissItem(position)
        addDismissedSuggestion(suggestionId)
        updateNotificationBadge()
    }

    private fun addDismissedSuggestion(suggestionId: String) {
        val sharedPreferences = getSharedPreferences("SportAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val dismissedSet = sharedPreferences.getStringSet("dismissedSuggestions", mutableSetOf()) ?: mutableSetOf()
        dismissedSet.add(suggestionId)
        Log.d("Notifications", "Adding dismissed suggestion ID: $suggestionId. New dismissed set: $dismissedSet")
        editor.putStringSet("dismissedSuggestions", dismissedSet)
        editor.apply()
    }

    private fun isDismissed(suggestionId: String): Boolean {
        val sharedPreferences = getSharedPreferences("SportAppPrefs", Context.MODE_PRIVATE)
        val dismissedSet = sharedPreferences.getStringSet("dismissedSuggestions", emptySet()) ?: emptySet()
        val isDismissed = dismissedSet.contains(suggestionId)
        Log.d("Notifications", "Checking if suggestion ID: $suggestionId is dismissed: $isDismissed")
        return isDismissed
    }

    private fun updateNotificationBadge() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.top_navigation)
        BadgeUtils.updateNotificationBadge(this, bottomNavigationView)
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
                    utilRedirect.redirectToActivity(this, Home::class.java)
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

    class TableAdapter(private val onItemDismissed: (Int, String) -> Unit) : RecyclerView.Adapter<TableAdapter.ViewHolder>() {
        private val data = mutableListOf<TrainingPlansResponse>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_suggestion, parent, false)
            return ViewHolder(view, onItemDismissed)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(data[position])
        }

        override fun getItemCount() = data.size

        fun addItem(item: TrainingPlansResponse) {
            data.add(item)
            notifyItemInserted(data.size - 1)
        }

        fun clearItems() {
            data.clear()
            notifyDataSetChanged()
        }

        fun dismissItem(position: Int) {
            data.removeAt(position)
            notifyItemRemoved(position)
        }

        class ViewHolder(itemView: View, private val onItemDismissed: (Int, String) -> Unit) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.findViewById<View>(R.id.dismissIcon).setOnClickListener {
                    onItemDismissed(adapterPosition, itemView.tag as String)
                }
            }

            fun bind(item: TrainingPlansResponse) {
                itemView.findViewById<TextView>(R.id.textViewColumn1).text = item.description
                itemView.findViewById<TextView>(R.id.textViewColumn2).text = item.duration
                itemView.findViewById<TextView>(R.id.textViewColumn3).text = item.exercises
                itemView.findViewById<TextView>(R.id.textViewColumn4).text = item.objectives
                itemView.tag = "plan_${item.id}"

                // Set different background color based on suggestion type
                val backgroundColorRes = R.color.colorSuggestionTraining
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, backgroundColorRes))

                // Add a dismiss icon
                val dismissIcon = itemView.findViewById<View>(R.id.dismissIcon)
                dismissIcon.visibility = View.VISIBLE
                dismissIcon.setOnClickListener {
                    onItemDismissed(adapterPosition, "plan_${item.id}")
                }
            }
        }
    }

    class TableAdapterEvents(private val onItemDismissed: (Int, String) -> Unit) : RecyclerView.Adapter<TableAdapterEvents.ViewHolder>() {
        private val dataEvent = mutableListOf<EventSuggestion>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_event_sugg, parent, false)
            return ViewHolder(view, onItemDismissed)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(dataEvent[position])
        }

        override fun getItemCount() = dataEvent.size

        fun addItem(item: EventSuggestion) {
            dataEvent.add(item)
            notifyItemInserted(dataEvent.size - 1)
        }

        fun clearItems() {
            dataEvent.clear()
            notifyDataSetChanged()
        }

        fun dismissItem(position: Int) {
            dataEvent.removeAt(position)
            notifyItemRemoved(position)
        }

        class ViewHolder(itemView: View, private val onItemDismissed: (Int, String) -> Unit) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.findViewById<View>(R.id.dismissIcon).setOnClickListener {
                    onItemDismissed(adapterPosition, itemView.tag as String)
                }
            }

            fun bind(item: EventSuggestion) {
                itemView.findViewById<TextView>(R.id.textViewColumn1).text = item.nombre
                itemView.findViewById<TextView>(R.id.textViewColumn2).text = item.fecha
                itemView.findViewById<TextView>(R.id.textViewColumn3).text = item.descripción
                itemView.tag = "event_${item.id}"

                // Set different background color based on event type
                val backgroundColorRes = R.color.colorSuggestionEvent
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, backgroundColorRes))

                // Add a dismiss icon
                val dismissIcon = itemView.findViewById<View>(R.id.dismissIcon)
                dismissIcon.visibility = View.VISIBLE
                dismissIcon.setOnClickListener {
                    onItemDismissed(adapterPosition, "event_${item.id}")
                }
            }
        }
    }

    class TableAdapterRoutes(private val onItemDismissed: (Int, String) -> Unit) : RecyclerView.Adapter<TableAdapterRoutes.ViewHolder>() {
        private val dataRoutes = mutableListOf<String>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_route, parent, false)
            return ViewHolder(view, onItemDismissed)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(dataRoutes[position])
        }

        override fun getItemCount() = dataRoutes.size

        fun addItem(item: String) {
            dataRoutes.add(item)
            notifyItemInserted(dataRoutes.size - 1)
        }

        fun clearItems() {
            dataRoutes.clear()
            notifyDataSetChanged()
        }

        fun dismissItem(position: Int) {
            dataRoutes.removeAt(position)
            notifyItemRemoved(position)
        }

        class ViewHolder(itemView: View, private val onItemDismissed: (Int, String) -> Unit) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.findViewById<View>(R.id.dismissIcon).setOnClickListener {
                    onItemDismissed(adapterPosition, itemView.tag as String)
                }
            }

            fun bind(item: String) {
                itemView.findViewById<TextView>(R.id.textViewColumn1).text = item
                itemView.tag = "route_${adapterPosition}"

                // Set different background color for routes
                val backgroundColorRes = R.color.colorSuggestionRoute
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, backgroundColorRes))

                // Add a dismiss icon
                val dismissIcon = itemView.findViewById<View>(R.id.dismissIcon)
                dismissIcon.visibility = View.VISIBLE
                dismissIcon.setOnClickListener {
                    onItemDismissed(adapterPosition, "route_${adapterPosition}")
                }
            }
        }
    }

}
