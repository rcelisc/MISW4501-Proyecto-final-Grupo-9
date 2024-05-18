package com.example.sportapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.sportapp.ui.views.StartTraining
import org.junit.Rule
import org.junit.Test

class RunTrainingTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(StartTraining::class.java)

    @Test
    fun testUIElementsDisplayed() {
        onView(withId(R.id.tvTrainingTypeTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.chronometer)).check(matches(isDisplayed()))
        onView(withId(R.id.btnStartTraining)).check(matches(isDisplayed()))
    }

    @Test
    fun testNavigationToHome() {
        // Verificar que la actividad de inicio (HomeActivity) se haya abierto
        onView(withId(R.id.tvWelcome)).check(matches(isDisplayed()))
    }

    @Test
    fun testChronometerStarts() {
        // Simular el clic en el botón de inicio
        onView(withId(R.id.btnStartTraining)).perform(click())

//        // Esperar un máximo de 5 segundos hasta que el cronómetro esté visible
//        try {
//            Thread.sleep(5000)
//            onView(withId(R.id.chronometer1)).check(matches(isDisplayed()))
//        } catch (e: NoMatchingViewException) {
//            // Manejar la excepción NoMatchingViewException
//            throw AssertionError("El cronómetro no se mostró después de hacer clic en el botón de inicio", e)
//        } catch (e: AssertionError) {
//            // Manejar la excepción AssertionError
//            throw AssertionError("El cronómetro no se mostró después de hacer clic en el botón de inicio", e)
//        }
    }

}
