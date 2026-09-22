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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Goal(
    val title: String,
    val progress: Float
)

@Composable
fun GoalsScreen(
    modifier: Modifier = Modifier
) {
    var goals by remember {
        mutableStateOf(
            listOf(
                Goal("Run 10 km", 0.6f),
                Goal("Read 2 books", 0.4f),
                Goal("Learn piano", 0.25f)
            )
        )
    }

    var showAddGoalDialog by remember {
        mutableStateOf(false)
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(goals) { goal ->
                GoalCard(
                    goal = goal,
                    onComplete = {
                        goals = goals.map {
                            if (it == goal) {
                                it.copy(progress = 1f)
                            } else {
                                it
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
                goals = goals + Goal(
                    title = title,
                    progress = 0f
                )

                showAddGoalDialog = false
            }
        )
    }
}

@Composable
private fun GoalCard(
    goal: Goal,
    onComplete: () -> Unit
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
                progress = { goal.progress },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(goal.progress * 100).toInt()}% complete"
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onComplete,
                enabled = goal.progress < 1f
            ) {
                Text(
                    text = if (goal.progress < 1f) {
                        "Mark Complete"
                    } else {
                        "Completed"
                    }
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

                    Text(
                        text = "Please enter a goal name."
                    )
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
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}
