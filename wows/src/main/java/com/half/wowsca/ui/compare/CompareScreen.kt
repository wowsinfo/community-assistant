package com.half.wowsca.ui.compare

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.half.wowsca.managers.CompareManager
import com.half.wowsca.model.Captain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    onBack: () -> Unit,
) {
    val captains = CompareManager.getCaptains()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compare Players") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBack) {
                        androidx.compose.material3.Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (captains.isEmpty()) {
                Text(
                    text = "No players selected for comparison",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                return@Column
            }

            // Captain info cards
            captains.forEach { captain ->
                CaptainCompareCard(captain)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats comparison header
            if (captains.size >= 2) {
                Text(
                    text = "Stats Comparison",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                CompareStatRow("Battles", captains.map { it.details?.battles?.toString() ?: "-" })
                CompareStatRow("Wins", captains.map { it.details?.wins?.toString() ?: "-" })
                CompareStatRow("Win Rate", captains.map {
                    val b = it.details?.battles ?: 0
                    val w = it.details?.wins ?: 0
                    if (b > 0) String.format("%.1f%%", (w.toFloat() / b) * 100) else "-"
                })
                CompareStatRow("Avg XP", captains.map {
                    val b = it.details?.battles ?: 0
                    val xp = it.details?.totalXP ?: 0
                    if (b > 0) String.format("%.0f", xp.toFloat() / b) else "-"
                })
                CompareStatRow("Total XP", captains.map { it.details?.totalXP?.toString() ?: "-" })
                CompareStatRow("Frags", captains.map { it.details?.frags?.toString() ?: "-" })
                CompareStatRow("Survived", captains.map { it.details?.survivedBattles?.toString() ?: "-" })
            }
        }
    }
}

@Composable
private fun CaptainCompareCard(captain: Captain) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = captain.name ?: "Unknown",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Server: ${captain.server?.name?.uppercase() ?: "-"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CompareStatRow(label: String, values: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.width(120.dp)
            )
            values.forEach { value ->
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
