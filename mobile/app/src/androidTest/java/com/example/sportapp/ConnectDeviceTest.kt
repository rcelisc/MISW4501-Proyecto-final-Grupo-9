package com.example.sportapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.sportapp.ui.views.ConnectDevice
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test

class ConnectDeviceTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(ConnectDevice::class.java)

    @Test
    fun testStartDevice() {
        // Hace clic en el botón para iniciar el dispositivo
        onView(withId(R.id.btnStartDevice)).perform(click())

        // Espera un tiempo para que se generen las mediciones
        Thread.sleep(1000) // Ajusta el tiempo según sea necesario

        // Verifica que los TextViews contengan valores de mediciones
        onView(withId(R.id.powerOutputTextView)).check(matches(not(withText(""))))
        onView(withId(R.id.maxHeartRateTextView)).check(matches(not(withText(""))))
        onView(withId(R.id.restingHeartRateTextView)).check(matches(not(withText(""))))
    }



}
