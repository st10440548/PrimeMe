package com.primeme.app.data.local

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.primeme.app.data.remote.ApiGoal

/** Stores the last successfully synchronized goal list for read-only offline access. */
class GoalsCache(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "primeme_goals_cache", Context.MODE_PRIVATE
    )
    private val gson = Gson()

    private fun userKey(): String? = FirebaseAuth.getInstance().currentUser?.uid?.let { "goals_json_$it" }

    fun save(goals: List<ApiGoal>) {
        val key = userKey() ?: return
        preferences.edit().putString(key, gson.toJson(goals)).apply()
    }

    fun load(): List<ApiGoal>? {
        val key = userKey() ?: return null
        val json = preferences.getString(key, null) ?: return null
        return try {
            val type = object : TypeToken<List<ApiGoal>>() {}.type
            gson.fromJson<List<ApiGoal>>(json, type)
        } catch (_: Exception) {
            null
        }
    }
}
