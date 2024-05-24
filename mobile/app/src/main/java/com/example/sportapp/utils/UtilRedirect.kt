package com.example.sportapp.utils

import android.content.Context
import android.content.Intent

class UtilRedirect {
    fun redirectToActivity(context: Context, activityClass: Class<*>) {
        val intent = Intent(context, activityClass)
        context.startActivity(intent)
    }
}
