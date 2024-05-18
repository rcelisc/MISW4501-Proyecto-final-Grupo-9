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

    }

    @Test
    fun testRunExeButtonClick() {

    }

    @Test
    fun testCalendarButtonClick() {

    }


}
