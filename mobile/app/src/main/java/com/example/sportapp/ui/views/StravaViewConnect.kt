package com.example.sportapp.ui.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportapp.R
import com.example.sportapp.data.model.AccessTokenResponse
import com.example.sportapp.data.model.StravaActivity
import com.example.sportapp.data.repository.StravaRepository
import com.example.sportapp.data.services.RetrofitClient
import com.example.sportapp.ui.home.Home
import com.example.sportapp.utils.BadgeUtils
import com.example.sportapp.utils.UtilRedirect
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StravaViewConnect : AppCompatActivity() {
    private val utilRedirect = UtilRedirect()
    private lateinit var stravaRepository: StravaRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var activitiesRecyclerView: RecyclerView
    private lateinit var activitiesAdapter: StravaActivitiesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_strava)
        setUpNavigationButtons()

        sharedPreferences = getSharedPreferences("SportAppPrefs", Context.MODE_PRIVATE)
        stravaRepository = StravaRepository(RetrofitClient.createStravaService(this))

        activitiesRecyclerView = findViewById(R.id.activitiesRecyclerView)
        activitiesRecyclerView.layoutManager = LinearLayoutManager(this)
        activitiesAdapter = StravaActivitiesAdapter()
        activitiesRecyclerView.adapter = activitiesAdapter

        findViewById<MaterialButton>(R.id.btnSendStrava).setOnClickListener {
            authorizeStrava()
        }
    }

    override fun onResume() {
        super.onResume()
        val topNavigationView = findViewById<BottomNavigationView>(R.id.top_navigation)
        BadgeUtils.updateNotificationBadge(this, topNavigationView)

        // Check if Strava is connected
        checkStravaConnection()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent?.data?.let { uri ->
            Log.d("StravaViewConnect", "Received URI: $uri")
            val code = uri.getQueryParameter("code")
            if (code != null) {
                Log.d("StravaViewConnect", "Authorization code: $code")
                exchangeCodeForToken(code)
            } else {
                Log.e("StravaViewConnect", "Authorization code not found")
                Toast.makeText(this, "Authorization code not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun authorizeStrava() {
        val clientId = "125409"
        val redirectUri = "myapp://strava-callback"
        val authUrl = Uri.parse("https://www.strava.com/oauth/mobile/authorize")
            .buildUpon()
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("approval_prompt", "auto")
            .appendQueryParameter("scope", "activity:read")
            .build()

        Log.d("StravaViewConnect", "Authorization URL: $authUrl")
        val browserIntent = Intent(Intent.ACTION_VIEW, authUrl)
        startActivity(browserIntent)
    }

    private fun exchangeCodeForToken(code: String) {
        Log.d("StravaViewConnect", "Exchanging code for token")
        stravaRepository.exchangeCodeForToken(code, object : Callback<AccessTokenResponse> {
            override fun onResponse(call: Call<AccessTokenResponse>, response: Response<AccessTokenResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.access_token
                    Log.d("StravaViewConnect", "Response: ${response.body()}")
                    Log.d("StravaViewConnect", "Response Access Token: ${response.body()?.access_token}")
                    if (token != null) {
                        Log.d("StravaViewConnect", "Token received: $token")
                        saveToken(token)
                        findViewById<MaterialButton>(R.id.btnSendStrava).text = getString(R.string.get_strava_info)
                        fetchStravaActivities(token)
                    } else {
                        Log.e("StravaViewConnect", "Token is null")
                        Toast.makeText(this@StravaViewConnect, "Token is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("StravaViewConnect", "Token exchange failed: ${response.message()}")
                    Log.e("StravaViewConnect", "Response body: ${response.errorBody()?.string()}")
                    Toast.makeText(this@StravaViewConnect, "Token exchange failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AccessTokenResponse>, t: Throwable) {
                Log.e("StravaViewConnect", "Token exchange error", t)
                Toast.makeText(this@StravaViewConnect, "Token exchange error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkStravaConnection() {
        Log.d("StravaViewConnect", "Checking Strava connection")
        val token = sharedPreferences.getString("strava_access_token", null)
        if (token != null) {
            findViewById<MaterialButton>(R.id.btnSendStrava).text = getString(R.string.get_strava_info)
            fetchStoredActivities()
        } else {
            findViewById<MaterialButton>(R.id.btnSendStrava).text = getString(R.string.connect_strava)
        }
    }

    private fun fetchStravaActivities(token: String) {
        Log.d("StravaViewConnect", "Fetching Strava activities")
        stravaRepository.fetchStravaActivities(token, object : Callback<List<StravaActivity>> {
            override fun onResponse(call: Call<List<StravaActivity>>, response: Response<List<StravaActivity>>) {
                if (response.isSuccessful) {
                    Log.d("StravaViewConnect", "Successfully fetched Strava activities")
                    displayActivities(response.body())
                } else {
                    Log.e("StravaViewConnect", "Failed to fetch Strava activities: ${response.message()}")
                    Toast.makeText(this@StravaViewConnect, "Failed to fetch Strava activities", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<StravaActivity>>, t: Throwable) {
                Log.e("StravaViewConnect", "Error fetching Strava activities", t)
                Toast.makeText(this@StravaViewConnect, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchStoredActivities() {
        Log.d("StravaViewConnect", "Fetching stored activities")
        stravaRepository.getStoredActivities(object : Callback<List<StravaActivity>> {
            override fun onResponse(call: Call<List<StravaActivity>>, response: Response<List<StravaActivity>>) {
                if (response.isSuccessful) {
                    val activities = response.body()
                    displayActivities(activities)
                } else {
                    Log.e("StravaViewConnect", "Failed to retrieve stored activities: ${response.message()}")
                    Toast.makeText(this@StravaViewConnect, "Failed to retrieve stored activities", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<StravaActivity>>, t: Throwable) {
                Log.e("StravaViewConnect", "Error retrieving stored activities", t)
                Toast.makeText(this@StravaViewConnect, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayActivities(activities: List<StravaActivity>?) {
        Log.d("StravaViewConnect", "Displaying activities")
        activities?.let {
            activitiesAdapter.updateActivities(it)
        }
    }

    private fun saveToken(token: String) {
        sharedPreferences.edit().putString("strava_access_token", token).apply()
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

    // Adapter class for RecyclerView
    inner class StravaActivitiesAdapter : RecyclerView.Adapter<StravaActivitiesAdapter.ActivityViewHolder>() {

        private var activities: List<StravaActivity> = listOf()

        fun updateActivities(newActivities: List<StravaActivity>) {
            activities = newActivities
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_activity, parent, false)
            return ActivityViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
            val activity = activities[position]
            holder.nameTextView.text = activity.name
            holder.distanceTextView.text = activity.distance.toString()
            holder.timeTextView.text = activity.moving_time.toString()
            holder.typeTextView.text = activity.type
        }

        override fun getItemCount() = activities.size

        inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTextView: TextView = itemView.findViewById(R.id.activity_name)
            val distanceTextView: TextView = itemView.findViewById(R.id.activity_distance)
            val timeTextView: TextView = itemView.findViewById(R.id.activity_time)
            val typeTextView: TextView = itemView.findViewById(R.id.activity_type)
        }
    }
}
