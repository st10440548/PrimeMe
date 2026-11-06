package com.primeme.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.primeme.app.data.local.GoalsCache
import com.primeme.app.data.remote.ApiGoal
import com.primeme.app.data.remote.PrimeMeApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.primeme.app.ui.localization.LocalIsZulu
import com.primeme.app.ui.localization.translate
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    val isZulu = LocalIsZulu.current
    fun t(en: String, zu: String) = translate(isZulu, en, zu)
    val context = LocalContext.current
    val cache = remember(context) { GoalsCache(context) }
    var goals by remember { mutableStateOf<List<ApiGoal>>(emptyList()) }
    var showProgress by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var offline by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val saved = withContext(Dispatchers.IO) { cache.load() }
        if (saved != null) goals = saved
        try {
            val remote = withContext(Dispatchers.IO) { PrimeMeApiClient.api.getGoals() }
            goals = remote
            withContext(Dispatchers.IO) { cache.save(remote) }
            offline = false
        } catch (_: Exception) {
            offline = true
        } finally {
            isLoading = false
        }
    }
    val completed = goals.count { it.progress >= 100 }
    val score = if (goals.isEmpty()) 0 else (completed * 100 / goals.size)
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "PrimeMe",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = t("Good morning 👋", "Sawubona 👋"),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = t("Let's chase your prime.", "Asisebenzele impumelelo yakho."),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimeScoreCard(score)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = t("Active Goals", "Izinhloso Eziqhubekayo"),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        val activeGoals = goals.filter { it.progress < 100 }
        if (isLoading && goals.isEmpty()) {
            Text(t("Loading your goals...", "Kulayishwa izinhloso zakho..."))
        } else if (activeGoals.isEmpty()) {
            Text(
                if (goals.isEmpty()) t(
                    "No active goals yet. Add a goal in the Goals tab to see it here.",
                    "Azikho izinhloso ezisebenzayo. Engeza inhloso ekhasini Lezinhloso."
                ) else t(
                    "All your goals are complete. Great work!",
                    "Zonke izinhloso zakho seziqediwe. Wenze kahle!"
                )
            )
        } else {
            activeGoals.forEach { goal ->
                FocusItem(
                    title = goal.title,
                    subtitle = "${goal.progress}% " + t("complete", "kuqediwe")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { showProgress = !showProgress },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = t("View Goal Progress", "Buka Inqubekelaphambili Yezinhloso"))
        }
        if (showProgress) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(t("Overall Goal Progress", "Inqubekelaphambili Yezinhloso Zonke"), fontWeight = FontWeight.Bold)
            Text(t("Completed", "Eziqediwe") + ": $completed / ${goals.size}")
            Text(t("In progress", "Eziqhubekayo") + ": ${goals.size - completed}")
            Text(t("Prime Score", "Amaphuzu e-Prime") + ": $score / 100")
            if (isLoading) Text(t("Loading goals...", "Kulayishwa izinhloso..."))
            if (offline) Text(t("Showing saved goals (offline)", "Kuboniswa izinhloso ezilondoloziwe"))
        }
    }
}

@Composable
private fun PrimeScoreCard(score: Int) {
    val isZulu = LocalIsZulu.current
    fun t(en: String, zu: String) = translate(isZulu, en, zu)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = t("Prime Score", "Amaphuzu e-Prime"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = t("Keep building consistency.", "Qhubeka nokuzimisela."),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = score.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FocusItem(
    title: String,
    subtitle: String
) {
    val isZulu = LocalIsZulu.current
    fun t(en: String, zu: String) = translate(isZulu, en, zu)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.size(14.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
