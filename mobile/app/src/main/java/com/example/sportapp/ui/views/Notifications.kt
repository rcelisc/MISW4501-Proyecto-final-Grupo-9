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
import com.example.sportapp.data.model.FoodBeverageSuggestion
import com.example.sportapp.data.model.Service
import com.example.sportapp.data.model.ServicesResponse
import com.example.sportapp.data.model.TrainingPlansResponse
import com.example.sportapp.data.model.User
import com.example.sportapp.data.repository.EventsRepository
import com.example.sportapp.data.repository.ServicesRepository
import com.example.sportapp.data.repository.TrainingPlansRepository
import com.example.sportapp.data.services.RetrofitClient
import com.example.sportapp.ui.home.Home
import com.example.sportapp.utils.UtilRedirect
import com.example.sportapp.utils.BadgeUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Notifications : AppCompatActivity() {

    private lateinit var tableAdapter: TableAdapter
    private lateinit var tableAdapterEvents: TableAdapterEvents
    private lateinit var tableAdapterRoutes: TableAdapterRoutes
    private lateinit var tableAdapterServices: TableAdapterServices
    private lateinit var tableAdapterFoodBeverage: TableAdapterFoodBeverage
    private val repositoryEvents = EventsRepository(RetrofitClient.getEventsService(this))
    private val repository = TrainingPlansRepository(RetrofitClient.createTrainingPlansService(this))
    private val servicesRepository = ServicesRepository(RetrofitClient.getServicesPublished(this))
    private val utilRedirect = UtilRedirect()
    private var suggestedRoute: String? = null
    private var suggestedFoodBeverage: List<FoodBeverageSuggestion>? = null

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
        fetchServicesPublished()
        displayRouteSuggestion()
        displayFoodBeverageSuggestions()
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

        val recyclerViewServices = findViewById<RecyclerView>(R.id.rvServices)
        recyclerViewServices.layoutManager = LinearLayoutManager(this)
        tableAdapterServices = TableAdapterServices { position, suggestionId -> dismissService(position, suggestionId) }
        recyclerViewServices.adapter = tableAdapterServices

        val recyclerViewFoodBeverage = findViewById<RecyclerView>(R.id.rvFoodBeverage)
        recyclerViewFoodBeverage.layoutManager = LinearLayoutManager(this)
        tableAdapterFoodBeverage = TableAdapterFoodBeverage { position, suggestionId -> dismissFoodBeverage(position, suggestionId) }
        recyclerViewFoodBeverage.adapter = tableAdapterFoodBeverage
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
                        SportApp.plan_type = it.plan_type
                        Log.d("Notifications", "User profile fetched and updated: ${it.profile_type}")
                        Log.d("Notifications", "User profile fetched and updated: ${it.plan_type}")
                        fetchTrainingPlans()
                        fetchEventSuggestions()
                        fetchServicesPublished()
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

    private fun fetchServicesPublished() {
        if (SportApp.plan_type == "intermediate" || SportApp.profile == "premium") {
            servicesRepository.getServicesPublished().enqueue(object : Callback<ServicesResponse> {
                override fun onResponse(call: Call<ServicesResponse>, response: Response<ServicesResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { servicesResponse ->
                            Log.d("Notifications", "Services published found: $servicesResponse")
                            tableAdapterServices.clearItems()
                            servicesResponse.services.forEach {
                                if (!isDismissed("service_${it.id}")) {
                                    tableAdapterServices.addItem(it)
                                }
                            }
                            updateNotificationBadge()
                        } ?: Log.d("Notifications", "Server response is null for Services")
                    } else {
                        Log.d("Notifications", "Failed to fetch Services. Error code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ServicesResponse>, t: Throwable) {
                    Log.d("Notifications", "Error fetching Services: ${t.message}")
                }
            })
        }
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

    private fun displayFoodBeverageSuggestions() {
        val sharedPreferences = getSharedPreferences("SportAppPrefs", Context.MODE_PRIVATE)
        val dismissedSet = sharedPreferences.getStringSet("dismissedSuggestions", emptySet()) ?: emptySet()

        if (suggestedFoodBeverage == null) {
            val foodBeverageJson = sharedPreferences.getString("suggestedFoodBeverage", null)
            suggestedFoodBeverage = if (foodBeverageJson != null) {
                Gson().fromJson(foodBeverageJson, Array<FoodBeverageSuggestion>::class.java).toList()
            } else {
                getRandomFoodBeverageSuggestions().also {
                    sharedPreferences.edit().putString("suggestedFoodBeverage", Gson().toJson(it)).apply()
                }
            }
        }

        suggestedFoodBeverage?.let { suggestions ->
            tableAdapterFoodBeverage.clearItems()
            suggestions.forEach {
                if (!dismissedSet.contains("foodBeverage_${it.id}")) {
                    tableAdapterFoodBeverage.addItem(it)
                }
            }
            updateNotificationBadge()
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

    private fun getRandomFoodBeverageSuggestions(limit: Int = 1): List<FoodBeverageSuggestion> {
        val suggestions = listOf(
            FoodBeverageSuggestion(1, "Banana", "High in potassium and easy to digest.", "Before"),
            FoodBeverageSuggestion(2, "Energy Bar", "Provides quick energy.", "During"),
            FoodBeverageSuggestion(3, "Chocolate Milk", "Great for recovery with carbs and protein.", "After"),
            FoodBeverageSuggestion(4, "Water", "Stay hydrated throughout the activity.", "During"),
            FoodBeverageSuggestion(5, "Protein Shake", "Helps in muscle recovery.", "After")
        )
        return suggestions.shuffled().take(limit)
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

    private fun dismissService(position: Int, suggestionId: String) {
        Log.d("Notifications", "Dismissing service with ID: $suggestionId at position: $position")
        tableAdapterServices.dismissItem(position)
        addDismissedSuggestion(suggestionId)
        updateNotificationBadge()
    }

    private fun dismissFoodBeverage(position: Int, suggestionId: String) {
        Log.d("Notifications", "Dismissing food/beverage suggestion with ID: $suggestionId at position: $position")
        tableAdapterFoodBeverage.dismissItem(position)
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
                    utilRedirect.redirectToActivity(this, DashboardTrainingPlans::class.java)
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

    class TableAdapterServices(private val onItemDismissed: (Int, String) -> Unit) : RecyclerView.Adapter<TableAdapterServices.ViewHolder>() {
        private val dataServices = mutableListOf<Service>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_service_suggestion, parent, false)
            return ViewHolder(view, onItemDismissed)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(dataServices[position])
        }

        override fun getItemCount() = dataServices.size

        fun addItem(item: Service) {
            dataServices.add(item)
            notifyItemInserted(dataServices.size - 1)
        }

        fun clearItems() {
            dataServices.clear()
            notifyDataSetChanged()
        }

        fun dismissItem(position: Int) {
            dataServices.removeAt(position)
            notifyItemRemoved(position)
        }

        class ViewHolder(itemView: View, private val onItemDismissed: (Int, String) -> Unit) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.findViewById<View>(R.id.dismissIcon).setOnClickListener {
                    onItemDismissed(adapterPosition, itemView.tag as String)
                }
            }

            fun bind(item: Service) {
                itemView.findViewById<TextView>(R.id.textViewColumn1).text = item.name
                itemView.findViewById<TextView>(R.id.textViewColumn2).text = item.description
                itemView.findViewById<TextView>(R.id.textViewColumn3).text = item.rate.toString()
                itemView.tag = "service_${item.id}"

                // Set different background color for services
                val backgroundColorRes = R.color.colorSuggestionService
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, backgroundColorRes))

                // Add a dismiss icon
                val dismissIcon = itemView.findViewById<View>(R.id.dismissIcon)
                dismissIcon.visibility = View.VISIBLE
                dismissIcon.setOnClickListener {
                    onItemDismissed(adapterPosition, "service_${item.id}")
                }
            }
        }
    }

    class TableAdapterFoodBeverage(private val onItemDismissed: (Int, String) -> Unit) : RecyclerView.Adapter<TableAdapterFoodBeverage.ViewHolder>() {
        private val dataFoodBeverage = mutableListOf<FoodBeverageSuggestion>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_food_beverage, parent, false)
            return ViewHolder(view, onItemDismissed)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(dataFoodBeverage[position])
        }

        override fun getItemCount() = dataFoodBeverage.size

        fun addItem(item: FoodBeverageSuggestion) {
            dataFoodBeverage.add(item)
            notifyItemInserted(dataFoodBeverage.size - 1)
        }

        fun clearItems() {
            dataFoodBeverage.clear()
            notifyDataSetChanged()
        }

        fun dismissItem(position: Int) {
            dataFoodBeverage.removeAt(position)
            notifyItemRemoved(position)
        }

        class ViewHolder(itemView: View, private val onItemDismissed: (Int, String) -> Unit) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.findViewById<View>(R.id.dismissIcon).setOnClickListener {
                    onItemDismissed(adapterPosition, itemView.tag as String)
                }
            }

            fun bind(item: FoodBeverageSuggestion) {
                itemView.findViewById<TextView>(R.id.textViewColumn1).text = item.name
                itemView.findViewById<TextView>(R.id.textViewColumn2).text = item.description
                itemView.findViewById<TextView>(R.id.textViewColumn3).text = item.timing
                itemView.tag = "foodBeverage_${item.id}"

                // Set different background color for food and beverage suggestions
                val backgroundColorRes = R.color.colorSuggestionFoodBeverage
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, backgroundColorRes))

                // Add a dismiss icon
                val dismissIcon = itemView.findViewById<View>(R.id.dismissIcon)
                dismissIcon.visibility = View.VISIBLE
                dismissIcon.setOnClickListener {
                    onItemDismissed(adapterPosition, "foodBeverage_${item.id}")
                }
            }
        }
    }
}
