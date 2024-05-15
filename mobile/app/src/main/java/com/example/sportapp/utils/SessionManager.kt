package com.example.sportapp.utils

import android.content.Context
import android.widget.Toast
import com.example.sportapp.data.api.AuthService
import com.example.sportapp.data.model.LogoutRequest
import com.example.sportapp.data.model.LogoutResponse
import com.example.sportapp.data.services.RetrofitClient
import com.example.sportapp.ui.views.LoginScreen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object SessionManager {

    private val utilRedirect = UtilRedirect()

    fun getUserIdFromToken(context: Context): Int? {
        val sharedPreferences = context.getSharedPreferences("SportAppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)
        return if (userId != -1) userId else null
    }
    fun logoutUser(context: Context) {
        val sharedPreferences = context.getSharedPreferences("SportAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (token != null && userId != -1) {
            val authService = RetrofitClient.createAuthService(context)
            val logoutRequest = LogoutRequest(user_id = userId)

            authService.logoutUser(logoutRequest).enqueue(object : Callback<LogoutResponse> {
                override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                    if (response.isSuccessful) {
                        clearLocalStorage(context)
                        utilRedirect.redirectToActivity(context, LoginScreen::class.java)
                    } else {
                        Toast.makeText(context, "Logout failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "No token found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearLocalStorage(context: Context) {
        val sharedPreferences = context.getSharedPreferences("SportAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
