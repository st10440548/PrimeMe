package com.primeme.app.domain

/** Pure goal-progress calculations, independent of Android UI and network state. */
object GoalProgress {
    fun completedCount(progressValues: List<Int>): Int =
        progressValues.count { it >= 100 }

    fun primeScore(progressValues: List<Int>): Int =
        if (progressValues.isEmpty()) 0
        else completedCount(progressValues) * 100 / progressValues.size
}
