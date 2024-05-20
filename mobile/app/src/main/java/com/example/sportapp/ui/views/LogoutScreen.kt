package com.example.sportapp.ui.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sportapp.data.api.AuthService
import com.example.sportapp.data.model.LogoutRequest
import com.example.sportapp.data.model.LogoutResponse
import com.example.sportapp.databinding.ActivityLogoutBinding
import com.example.sportapp.utils.UtilRedirect
import com.example.sportapp.data.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogoutScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLogoutBinding
    private lateinit var authService: AuthService
    private val utilRedirect = UtilRedirect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authService = RetrofitClient.createAuthService(this)

        binding.btnLogout.setOnClickListener {
            Log.d("LogoutScreen", "Logout button clicked")
            val sharedPreferences = getSharedPreferences("SportAppPrefs", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", null)
            val userId = sharedPreferences.getInt("user_id", -1)

            Log.d("LogoutScreen", "Token: $token")
            Log.d("LogoutScreen", "User ID: $userId")

            if (token != null && userId != -1) {
                logoutUser(userId)
            } else {
                Toast.makeText(this, "No token found", Toast.LENGTH_SHORT).show()
                Log.e("LogoutScreen", "No token found")
            }
        }
    }

    private fun logoutUser(userId: Int) {
        val logoutRequest = LogoutRequest(user_id = userId)
        Log.d("LogoutScreen", "Logout request: $logoutRequest")

        authService.logoutUser(logoutRequest).enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                Log.d("LogoutScreen", "Response: $response")
                if (response.isSuccessful) {
                    Log.d("LogoutScreen", "Logout successful")
                    clearLocalStorage()
                    utilRedirect.redirectToActivity(this@LogoutScreen, LoginScreen::class.java)
                } else {
                    Log.e("LogoutScreen", "Logout failed: ${response.errorBody()?.string()}")
                    Toast.makeText(this@LogoutScreen, "Logout failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                Log.e("LogoutScreen", "Logout error: ${t.message}", t)
                Toast.makeText(this@LogoutScreen, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearLocalStorage() {
        Log.d("LogoutScreen", "Clearing local storage")
        val sharedPreferences = getSharedPreferences("SportAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
