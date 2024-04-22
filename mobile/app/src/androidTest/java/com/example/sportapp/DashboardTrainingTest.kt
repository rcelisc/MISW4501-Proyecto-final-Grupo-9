import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.action.ViewActions
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sportapp.R
import com.example.sportapp.ui.views.*

@RunWith(AndroidJUnit4::class)
class DashboardTrainingTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(DashboardTraining::class.java)

    @Test
    fun testRunExeButtonClick() {
        // Hacer clic en el icono de ejecutar entrenamiento
        Espresso.onView(ViewMatchers.withId(R.id.ivRunExe))
            .perform(ViewActions.click())

        // Verificar que la actividad StartTraining se haya iniciado
        // Espresso.onView(ViewMatchers.withId(R.id.start_training_activity_layout))
        // .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testExitButtonClick() {
        // Hacer clic en el icono de salida
        Espresso.onView(ViewMatchers.withId(R.id.ivHome))
            .perform(ViewActions.click())

//        // Verificar que la actividad LoginScreen se haya iniciado
//        Espresso.onView(ViewMatchers.withId(R.id.login_activity_layout))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testCalendarButtonClick() {
        // Hacer clic en el icono de calendario
        Espresso.onView(ViewMatchers.withId(R.id.ivCalendar))
            .perform(ViewActions.click())

        // Verificar que la actividad CalendarEvents se haya iniciado
//        Espresso.onView(ViewMatchers.withId(R.id.calendar_events_activity_layout))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}
