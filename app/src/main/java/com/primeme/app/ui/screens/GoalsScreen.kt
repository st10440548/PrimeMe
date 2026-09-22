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
        try {
            goals = withContext(Dispatchers.IO) {
                PrimeMeApiClient.api.getGoals()
            }

            errorMessage = null
        } catch (exception: Exception) {
            errorMessage = "Unable to load goals: ${exception.message}"
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
                    text = "Your Goals",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Keep moving toward your prime."
                )
            }

            Button(
                onClick = {
                    showAddGoalDialog = true
                }
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            Text("Loading goals...")
        }

        if (errorMessage != null) {
            Text(errorMessage!!)
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(goals) { goal ->
                GoalCard(goal = goal)
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
    goal: ApiGoal
) {
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

            Text("${goal.progress}% complete")

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {}
            ) {
                Text("Mark Complete")
            }
        }
    }
}

@Composable
private fun AddGoalDialog(
    onDismiss: () -> Unit,
    onAddGoal: (String) -> Unit
) {
    var title by remember {
        mutableStateOf("")
    }

    var showError by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Goal")
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
                        Text("Goal name")
                    },
                    singleLine = true
                )

                if (showError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Please enter a goal name.")
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
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
