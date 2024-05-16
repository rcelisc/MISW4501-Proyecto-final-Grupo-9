package com.example.sportapp.ui.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.JWT
import com.example.sportapp.SportApp
import com.example.sportapp.data.api.AuthService
import com.example.sportapp.data.model.LoginRequest
import com.example.sportapp.data.model.LoginResponse
import com.example.sportapp.data.model.User
import com.example.sportapp.databinding.ActivityLoginBinding
import com.example.sportapp.ui.home.Home
import com.example.sportapp.utils.UtilRedirect
import com.example.sportapp.data.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authService: AuthService
    private val utilRedirect = UtilRedirect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authService = RetrofitClient.createAuthService(this)

        binding.btnLogin.setOnClickListener {
            val idNumber = binding.idNumberInput.text.toString()
            val password = binding.passwordInput.text.toString()
            loginUser(idNumber, password)
        }
    }

    private fun loginUser(idNumber: String, password: String) {
        val loginRequest = LoginRequest(id_number = idNumber, password = password)
        Log.d("LoginScreen", "Login request: $loginRequest")
        authService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("LoginScreen", "Response: $response")
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Log.d("LoginScreen", "Login response: $loginResponse")
                    if (loginResponse != null) {
                        val decodedToken = decodeToken(loginResponse.token)
                        if (decodedToken != null && decodedToken.role == "athlete") {
                            saveToken(loginResponse.token)
                            fetchUserProfile(decodedToken.userId)
                            utilRedirect.redirectToActivity(this@LoginScreen, Home::class.java)
                        } else {
                            Log.e("LoginScreen", "Login failed: Only athletes can log in")
                            Toast.makeText(this@LoginScreen, "Login failed: Only athletes can log in", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("LoginScreen", "Login failed: response body is null")
                        Toast.makeText(this@LoginScreen, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    Log.e("LoginScreen", "Login failed: $errorResponse")
                    if (errorResponse?.contains("Another session is already active") == true) {
                        Toast.makeText(this@LoginScreen, "Login failed: Another session is already active. Please log out from other devices.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@LoginScreen, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginScreen", "Login error: ${t.message}", t)
                Toast.makeText(this@LoginScreen, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("SportAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)

        // Decode token and save user details
        val decodedToken = decodeToken(token)
        if (decodedToken != null) {
            editor.putInt("user_id", decodedToken.userId)
            editor.putString("user_role", decodedToken.role)
            editor.apply()

            // Initialize user session
            initializeUserSession(decodedToken.userId, decodedToken.role)
        } else {
            Toast.makeText(this, "Failed to decode token", Toast.LENGTH_SHORT).show()
        }
    }

    private fun decodeToken(token: String): DecodedToken? {
        return try {
            val jwt = JWT(token)
            val userId = jwt.getClaim("user_id").asInt()
            val role = jwt.getClaim("role").asString()
            if (userId != null && role != null) {
                DecodedToken(userId, role)
            } else {
                null
            }
        } catch (exception: Exception) {
            null
        }
    }

    private fun initializeUserSession(userId: Int, role: String) {
        SportApp.userCodeId = userId
        SportApp.userRole = role
        // Set other necessary session details if available
    }
    private fun fetchUserProfile(userId: Int) {
        val userService = RetrofitClient.createUserService(this)
        userService.getUserById(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        SportApp.profile = it.profile_type
                        Log.d("LoginScreen", "User profile fetched and updated: ${it.profile_type}")
                        utilRedirect.redirectToActivity(this@LoginScreen, Home::class.java)
                    } ?: run {
                        Log.d("LoginScreen", "User data is null")
                    }
                } else {
                    Log.d("LoginScreen", "Failed to fetch user data: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("LoginScreen", "Error fetching user data: ${t.message}")
            }
        })
    }

    data class DecodedToken(val userId: Int, val role: String)
}

