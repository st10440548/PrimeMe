package com.primeme.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "PrimeMe"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Good morning 👋"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Let's chase your prime."
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Prime Score"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "72"
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Today's Focus"
        )

        Spacer(modifier = Modifier.height(16.dp))

        FocusItem("Workout")
        FocusItem("Study")
        FocusItem("Read")
    }
}

@Composable
private fun FocusItem(title: String) {
    Text(
        text = "○  $title",
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
