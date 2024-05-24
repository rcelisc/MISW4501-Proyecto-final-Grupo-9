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
import com.example.sportapp.SportApp
import com.example.sportapp.data.model.TrainingsSessionsResponse
import com.example.sportapp.data.repository.TrainingPlansRepository
import com.example.sportapp.data.repository.TrainingSessionsRepository
import com.example.sportapp.data.services.RetrofitClient
import com.example.sportapp.ui.home.Home
import com.example.sportapp.utils.BadgeUtils
import com.example.sportapp.utils.UtilRedirect
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardTraining : AppCompatActivity() {
    private lateinit var tableAdapter: TableAdapter
    private val repository = TrainingSessionsRepository(RetrofitClient.createTrainingPlansService(this))
    private val utilRedirect = UtilRedirect()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_training)
        setUpNavigationButtons()

        val recyclerView = findViewById<RecyclerView>(R.id.rvTrainings)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tableAdapter = TableAdapter()
        recyclerView.adapter = tableAdapter

        repository.getTrainingSessions(SportApp.userCodeId).enqueue(object :
            Callback<List<TrainingsSessionsResponse>> {
            override fun onResponse(call: Call<List<TrainingsSessionsResponse>>, response: Response<List<TrainingsSessionsResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let { trainings ->
                        Log.d("DEBUG", "Trainings found...")
                        tableAdapter.addItems(trainings)
                    } ?: Log.d("DEBUG", "Server response is null")
                } else {
                    Log.d("DEBUG", "Service call not successful. Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<TrainingsSessionsResponse>>, t: Throwable) {
                Log.d("DEBUG", "Error calling the service: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    override fun onResume() {
        super.onResume()
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

    class TableAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val data = mutableListOf<TrainingsSessionsResponse>()

        companion object {
            const val VIEW_TYPE_HEADER = 0
            const val VIEW_TYPE_ITEM = 1
        }

        override fun getItemViewType(position: Int): Int {
            return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == VIEW_TYPE_HEADER) {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_training_header, parent, false)
                HeaderViewHolder(view)
            } else {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_trainings, parent, false)
                ItemViewHolder(view)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ItemViewHolder) {
                holder.bind(data[position - 1]) // -1 because position 0 is the header
            }
        }

        override fun getItemCount() = data.size + 1 // +1 for the header

        fun addItems(items: List<TrainingsSessionsResponse>) {
            val startInsertPosition = data.size + 1 // +1 for the header
            data.addAll(items)
            notifyItemRangeInserted(startInsertPosition, items.size)
        }

        class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val column1TextView: TextView = itemView.findViewById(R.id.textViewColumn1)
            private val column2TextView: TextView = itemView.findViewById(R.id.textViewColumn2)
            private val column3TextView: TextView = itemView.findViewById(R.id.textViewColumn3)
            private val column4TextView: TextView = itemView.findViewById(R.id.textViewColumn4)

            fun bind(item: TrainingsSessionsResponse) {
                column1TextView.text = item.training_type
                column2TextView.text = item.duration
                column3TextView.text = item.end_time
                column4TextView.text = item.notes
            }
        }
    }
}
