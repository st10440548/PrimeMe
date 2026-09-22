package com.primeme.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    var displayName by remember {
        mutableStateOf("")
    }

    var dailyFocus by remember {
        mutableStateOf("3")
    }

    var remindersEnabled by remember {
        mutableStateOf(true)
    }

    var darkModeEnabled by remember {
        mutableStateOf(false)
    }

    var showSavedMessage by remember {
        mutableStateOf(false)
    }

    var nameError by remember {
        mutableStateOf(false)
    }

    var focusError by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Settings",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Personalise your PrimeMe experience."
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Profile",
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = {
                displayName = it
                nameError = false
                showSavedMessage = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Display name")
            },
            singleLine = true,
            isError = nameError
        )

        if (nameError) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Please enter your display name."
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Daily Goals",
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = dailyFocus,
            onValueChange = {
                dailyFocus = it.filter { character ->
                    character.isDigit()
                }

                focusError = false
                showSavedMessage = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Daily focus activities")
            },
            supportingText = {
                Text("Recommended: 1–10 activities")
            },
            singleLine = true,
            isError = focusError
        )

        if (focusError) {
            Text(
                text = "Enter a number between 1 and 10."
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Preferences",
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingSwitch(
            title = "Daily reminders",
            checked = remindersEnabled,
            onCheckedChange = {
                remindersEnabled = it
                showSavedMessage = false
            }
        )

        SettingSwitch(
            title = "Dark mode",
            checked = darkModeEnabled,
            onCheckedChange = {
                darkModeEnabled = it
                showSavedMessage = false
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val validName = displayName.isNotBlank()
                val focusValue = dailyFocus.toIntOrNull()
                val validFocus = focusValue != null &&
                        focusValue in 1..10

                nameError = !validName
                focusError = !validFocus

                if (validName && validFocus) {
                    showSavedMessage = true
                } else {
                    showSavedMessage = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Preferences")
        }

        if (showSavedMessage) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Preferences saved successfully."
            )
        }
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title)

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
