package com.primeme.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.primeme.app.data.local.GoalsCache
import com.primeme.app.ui.localization.LocalIsZulu
import com.primeme.app.ui.localization.translate
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.primeme.app.data.remote.ApiGoal
import com.primeme.app.data.remote.CreateGoalRequest
import com.primeme.app.data.remote.PrimeMeApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GoalsScreen(
    modifier: Modifier = Modifier
) {
    val isZulu = LocalIsZulu.current
    fun t(en: String, zu: String) = translate(isZulu, en, zu)
    val context = LocalContext.current
    val cache = remember(context) { GoalsCache(context) }
    var offline by remember { mutableStateOf(false) }
    var goals by remember {
        mutableStateOf<List<ApiGoal>>(emptyList())
    }

    var showAddGoalDialog by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val saved = withContext(Dispatchers.IO) { cache.load() }
        if (saved != null) goals = saved
        try {
            val remote = withContext(Dispatchers.IO) { PrimeMeApiClient.api.getGoals() }
            goals = remote
            withContext(Dispatchers.IO) { cache.save(remote) }
            offline = false
            errorMessage = null
        } catch (exception: Exception) {
            offline = true
            errorMessage = if (saved == null) t("No saved goals available. Connect to the internet to sync.", "Azikho izinhloso ezilondoloziwe. Xhuma ku-inthanethi.") else null
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = t("Your Goals", "Izinhloso Zakho"),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = t("Keep moving toward your prime.", "Qhubeka nokufeza izinhloso zakho.")
                )
            }

            Button(
                onClick = {
                    if (!offline) showAddGoalDialog = true
                },
                enabled = !offline
            ) {
                Text(t("Add", "Engeza"))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (offline) {
            Text(t("Offline mode: showing saved goals. Reconnect and reopen Goals to sync.", "Imodi engaxhunyiwe: kuboniswa izinhloso ezilondoloziwe. Xhuma futhi uvule Izinhloso kabusha."))
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (isLoading) {
            Text(t("Loading goals...", "Kulayishwa izinhloso..."))
        }

        if (errorMessage != null) {
            Text(errorMessage!!)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(goals) { goal ->
                GoalCard(
                    goal = goal,
                    offline = offline,
                    onCompleteGoal = { goalId ->
                        scope.launch {
                            try {
                                val updatedGoal = withContext(Dispatchers.IO) {
                                    PrimeMeApiClient.api.completeGoal(goalId)
                                }
                                goals = goals.map {
                                    if (it.id == goalId) updatedGoal else it
                                }
                                withContext(Dispatchers.IO) { cache.save(goals) }
                                errorMessage = null
                            } catch (exception: Exception) {
                                errorMessage =
                                    "Unable to complete goal: ${exception.message}"
                            }
                        }
                    }
                )
            }
        }
    }

    if (showAddGoalDialog) {
        AddGoalDialog(
            onDismiss = {
                showAddGoalDialog = false
            },
            onAddGoal = { title ->
                scope.launch {
                    try {
                        val newGoal = withContext(Dispatchers.IO) {
                            PrimeMeApiClient.api.createGoal(
                                CreateGoalRequest(title)
                            )
                        }

                        goals = goals + newGoal
                        withContext(Dispatchers.IO) { cache.save(goals) }
                        errorMessage = null
                        showAddGoalDialog = false
                    } catch (exception: Exception) {
                        errorMessage =
                            "Unable to save goal: ${exception.message}"
                    }
                }
            }
        )
    }
}

@Composable
private fun GoalCard(
    goal: ApiGoal,
    offline: Boolean,
    onCompleteGoal: (Int) -> Unit
) {
    val isZulu = LocalIsZulu.current
    fun t(en: String, zu: String) = translate(isZulu, en, zu)
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = goal.title,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { goal.progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("${goal.progress}% " + t("complete", "kuqediwe"))

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onCompleteGoal(goal.id) },
                enabled = goal.progress < 100 && !offline
            ) {
                Text(
                    if (goal.progress >= 100) t("Completed", "Kuqediwe")
                    else t("Mark Complete", "Maka Njengokuqediwe")
                )
            }
        }
    }
}

@Composable
private fun AddGoalDialog(
    onDismiss: () -> Unit,
    onAddGoal: (String) -> Unit
) {
    val isZulu = LocalIsZulu.current
    fun t(en: String, zu: String) = translate(isZulu, en, zu)
    var title by remember {
        mutableStateOf("")
    }

    var showError by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(t("Add Goal", "Engeza Inhloso"))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        showError = false
                    },
                    label = {
                        Text(t("Goal name", "Igama lenhloso"))
                    },
                    singleLine = true
                )

                if (showError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(t("Please enter a goal name.", "Sicela ufake igama lenhloso."))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isBlank()) {
                        showError = true
                    } else {
                        onAddGoal(title.trim())
                    }
                }
            ) {
                Text(t("Add", "Engeza"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(t("Cancel", "Khansela"))
            }
        }
    )
}
