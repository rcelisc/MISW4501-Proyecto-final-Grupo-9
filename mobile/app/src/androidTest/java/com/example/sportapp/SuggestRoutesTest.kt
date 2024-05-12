package com.example.sportapp

import com.example.sportapp.ui.views.SuggestRoutes

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sportapp.ui.home.Home
import com.example.sportapp.ui.views.StravaViewConnect
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SuggestRoutesTest {


    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

   // @Test
//    fun testHomeButton() {
//        ActivityScenario.launch(StravaViewConnect::class.java).use {
//            onView(withId(R.id.ivHome)).perform(click())
//            Intents.intended(IntentMatchers.hasComponent(Home::class.java.name))
//        }
//    }
//
    @Test
    fun testSuggestButton() {
        ActivityScenario.launch(SuggestRoutes::class.java).use {
            onView(withId(R.id.ivHome)).perform(click())
            Intents.intended(IntentMatchers.hasComponent(Home::class.java.name))
        }
    }


}
