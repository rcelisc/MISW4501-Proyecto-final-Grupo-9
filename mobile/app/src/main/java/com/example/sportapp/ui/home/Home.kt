package com.example.sportapp.ui.home
import android.util.Log

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sportapp.ui.views.*
import com.example.sportapp.R
import com.example.sportapp.utils.SessionManager
import com.example.sportapp.utils.UtilRedirect
import com.example.sportapp.data.services.RetrofitClient
import com.example.sportapp.data.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Home : AppCompatActivity() {

    private val utilRedirect = UtilRedirect()
    private lateinit var tvWelcome: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvAge: TextView
    private lateinit var tvWeightHeight: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUpNavigationButtons()
        initNumeration()
        fetchUserData()
    }

    private fun setUpNavigationButtons() {
        val btnStrava = findViewById<ImageView>(R.id.imgStrava)
        val btnExit1 = findViewById<TextView>(R.id.tvCloseSession)

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

        btnStrava.setOnClickListener { utilRedirect.redirectToActivity(this, StravaViewConnect::class.java) }
        btnExit1.setOnClickListener { SessionManager.logoutUser(this) }
    }

    private fun initNumeration() {
        val tvVersion = findViewById<TextView>(R.id.tvVersionName)
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        tvVersion.text = "v_" + packageInfo.versionName
    }

    private fun fetchUserData() {
        tvWelcome = findViewById(R.id.tvWelcome)
        tvLocation = findViewById(R.id.tvLocation)
        tvAge = findViewById(R.id.tvAge)
        tvWeightHeight = findViewById(R.id.tvWeightHeight)

        val userId = SessionManager.getUserIdFromToken(this)
        if (userId != null) {
            Log.d("HomeActivity", "Fetching user data for user ID: $userId")
            val userService = RetrofitClient.createUserService(this)
            val call = userService.getUserById(userId)
            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        user?.let {
                            Log.d("HomeActivity", "User data fetched successfully: $it")
                            tvWelcome.text = "Welcome, ${it.name ?: "User"} ${it.surname ?: "!"}"
                            tvLocation.text = "${it.city_of_birth ?: "City"}, ${it.country_of_birth ?: "Country"}"
                            tvAge.text = "${it.age ?: "Age"} years"
                            tvWeightHeight.text = "${it.weight ?: "Weight"} kg, ${it.height ?: "Height"} cm"
                        } ?: run {
                            Log.d("HomeActivity", "User data is null")
                        }
                    } else {
                        Log.d("HomeActivity", "Failed to fetch user data: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d("HomeActivity", "Error fetching user data: ${t.message}")
                }
            })
        } else {
            Log.d("HomeActivity", "User ID is null")
        }
    }
}
