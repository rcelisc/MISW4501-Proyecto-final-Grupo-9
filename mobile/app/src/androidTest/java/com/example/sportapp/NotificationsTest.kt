import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sportapp.R
import com.example.sportapp.SportApp
import com.example.sportapp.data.model.TrainingPlansResponse
import com.example.sportapp.data.repository.TrainingPlansRepository
import com.example.sportapp.ui.views.Notifications
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class NotificationsTest {

    @Before
    fun setUp() {
        // Inicializa la propiedad profile en SportApp
        SportApp.userCodeId = 1
        SportApp.powerOutput = 250
        SportApp.maxHeartRate= 180
        SportApp.restingHeartRate = 60
        SportApp.profile = "Beginner"
    }

    @get:Rule
    val activityRule = ActivityScenarioRule(Notifications::class.java)

    @Test
    fun testHomeButtonClick() {

        // Hace clic en el icono de inicio
        onView(withId(R.id.ivHome)).perform(click())

//        // Verifica que la actividad Home se haya iniciado
//        onView(withId(R.id.home_activity_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testRunExeButtonClick() {
        // Hace clic en el icono de ejecutar entrenamiento
        onView(withId(R.id.ivRunExe)).perform(click())

//        // Verifica que la actividad StartTraining se haya iniciado
//        onView(withId(R.id.start_training_activity_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testCalendarButtonClick() {
        // Hace clic en el icono de calendario
        onView(withId(R.id.ivCalendar)).perform(click())

//        // Verifica que la actividad CalendarEvents se haya iniciado
//        onView(withId(R.id.calendar_events_activity_layout)).check(matches(isDisplayed()))
    }


}
