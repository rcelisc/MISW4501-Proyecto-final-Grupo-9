package com.example.sportapp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sportapp.ui.home.Home
import com.example.sportapp.ui.views.CalendarEvents
import com.example.sportapp.ui.views.LoginScreen
import com.example.sportapp.ui.views.StartTraining
import com.example.sportapp.ui.views.StravaViewConnect
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeTest {

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testStravaButton() {
        ActivityScenario.launch(Home::class.java).use {
            onView(withId(R.id.imgStrava)).perform(click())
            Intents.intended(hasComponent(StravaViewConnect::class.java.name))
        }
    }

    @Test
    fun testRunExeButton() {
        ActivityScenario.launch(Home::class.java).use {
        }
    }

    @Test
    fun testExitButton() {
        ActivityScenario.launch(Home::class.java).use {
        }
    }

    @Test
    fun testCalendarButton() {
        ActivityScenario.launch(Home::class.java).use {
        }
    }
}
