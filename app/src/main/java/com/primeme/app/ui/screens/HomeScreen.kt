package com.primeme.app.ui.screens

import androidx.compose.foundation.background
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
    Column(
        modifier = modifier
            .fillMaxSize()
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

        PrimeScoreCard()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = t("Today's Focus", "Okubalulekile Namuhla"),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        FocusItem(
            title = t("Workout", "Ukujima"),
            subtitle = t("Train for 45 minutes", "Zivocavoce imizuzu engu-45")
        )

        FocusItem(
            title = t("Study", "Ukufunda"),
            subtitle = t("Complete your planned study session", "Qedela isikhathi sakho sokufunda")
        )

        FocusItem(
            title = t("Read", "Funda"),
            subtitle = t("Read for at least 20 minutes", "Funda okungenani imizuzu engu-20")
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                // Progress functionality will be added later.
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = t("View Today's Progress", "Buka Inqubekelaphambili Yanamuhla"))
        }
    }
}

@Composable
private fun PrimeScoreCard() {
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
                    text = "72",
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
