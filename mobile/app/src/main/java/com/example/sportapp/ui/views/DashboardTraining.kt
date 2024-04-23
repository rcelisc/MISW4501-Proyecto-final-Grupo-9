package com.example.sportapp.ui.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportapp.LoginScreen
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.data.model.CalendarEvent
import com.example.sportapp.data.model.TrainingsSesionsResponse
import com.example.sportapp.data.repository.DataRepository
import com.example.sportapp.data.repository.TrainingSessionsRepository
import com.example.sportapp.data.services.RetrofitEventsManagementQueries
import com.example.sportapp.data.services.RetrofitTrainingSesionsService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardTraining : AppCompatActivity() {

    private lateinit var tableAdapter: TableAdapter
    private val repository = TrainingSessionsRepository(RetrofitTrainingSesionsService.createApiService())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_training)
        val btnRunExe = findViewById<ImageView>(R.id.ivRunExe)
        val btnExit = findViewById<ImageView>(R.id.ivHome)
        //  val btnExit1 = findViewById<TextView>(R.id.tvwCerrarSesion)
        val btnCalendar = findViewById<ImageView>(R.id.ivCalendar)
        val btnNotifications = findViewById<ImageView>(R.id.ivNotifications)

        //Redirige a la Actividad Iniciar Entrenamiento.
        btnRunExe.setOnClickListener{
            val startTraining = Intent(this, StartTraining::class.java)
            startActivity(startTraining)
        }

        //Cerrar Sesion.
        btnExit.setOnClickListener{
            val exitApp = Intent(this, LoginScreen::class.java)
            startActivity(exitApp)
        }

//        btnExit1.setOnClickListener{
//            val exitApp = Intent(this, LoginScreen::class.java)
//            startActivity(exitApp)
//        }

        //Redirige a la Actividad Calendario de Eventos.
        btnCalendar.setOnClickListener{
            val calendar = Intent(this, CalendarEvents::class.java)
            startActivity(calendar)
        }

        btnNotifications.setOnClickListener{
            val notif = Intent(this, Notifications::class.java)
            startActivity(notif)
        }

        /**/
        //Tabla de eventos
        val recyclerView = findViewById<RecyclerView>(R.id.rvTrainings)
        recyclerView.layoutManager = LinearLayoutManager(this)

        tableAdapter = TableAdapter()
        recyclerView.adapter = tableAdapter

        repository.getTrainingSessions(SportApp.userCodeId).enqueue(object :
            Callback<List<TrainingsSesionsResponse>> {
            override fun onResponse(call: Call<List<TrainingsSesionsResponse>>, response: Response<List<TrainingsSesionsResponse>>) {
                if (response.isSuccessful) {
                    val trainingsResponse = response.body()
                    if (trainingsResponse != null) {
                        Log.d("DEBUG", "Entrenamientos Encontrados...")
                        for (training in trainingsResponse) {
                            tableAdapter.addItem(training)
                        }
                    } else {
                       Log.d("DEBUG", "La respuesta del servidor es nula")
                    }
                } else {
                    Log.d("DEBUG", "La llamada al servicio no fue exitosa. Código de error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<TrainingsSesionsResponse>>, t: Throwable) {
                // Manejar errores de red o de llamada al servicio
                Log.d("DEBUG", "Error en la llamada al servicio: ${t.message}")
                t.printStackTrace()
            }
        })

    }


    class TableAdapter : RecyclerView.Adapter<TableAdapter.ViewHolder>() {

        private val data = mutableListOf<TrainingsSesionsResponse>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_trainings, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = data[position]
            holder.bind(item)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        fun addItem(item: TrainingsSesionsResponse) {
            data.add(item)
            notifyItemInserted(data.size - 1)
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val column1TextView: TextView = itemView.findViewById(R.id.textViewColumn1)
            val column2TextView: TextView = itemView.findViewById(R.id.textViewColumn2)
            val column3TextView: TextView = itemView.findViewById(R.id.textViewColumn3)

            fun bind(item: TrainingsSesionsResponse) {
                column1TextView.text = item.training_type
                column2TextView.text = item.duration
                column3TextView.text = item.notes
            }
        }
    }

}