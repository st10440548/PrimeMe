package com.primeme.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class GoalProgressTest {
    @Test fun emptyGoalsHaveZeroScore() {
        assertEquals(0, GoalProgress.primeScore(emptyList()))
    }

    @Test fun incompleteGoalsHaveZeroScore() {
        assertEquals(0, GoalProgress.primeScore(listOf(0, 25, 99)))
    }

    @Test fun mixedGoalsProduceCompletionPercentage() {
        assertEquals(50, GoalProgress.primeScore(listOf(100, 0, 100, 50)))
    }

    @Test fun completedCountIncludesProgressAtOrAboveOneHundred() {
        assertEquals(2, GoalProgress.completedCount(listOf(100, 120, 99)))
    }

    @Test fun allCompletedGoalsHaveFullScore() {
        assertEquals(100, GoalProgress.primeScore(listOf(100, 100)))
    }
}
