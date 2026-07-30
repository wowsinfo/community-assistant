package com.half.wowsca.ui.compare

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
import androidx.compose.material3.HorizontalDivider
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
import com.half.wowsca.model.Ship

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShipCompareScreen(
    onBack: () -> Unit,
) {
    val captains = CompareManager.getCaptains()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ship Comparison") },
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
            Text(
                text = "Ship Stats Comparison",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(12.dp))

            captains.forEach { captain ->
                CaptainShipsSection(captain)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
private fun CaptainShipsSection(captain: Captain) {
    Text(
        text = (captain.name ?: "Unknown") + " - Ships",
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(4.dp))

    val ships = captain.ships
    if (ships.isNullOrEmpty()) {
        Text(
            text = "No ship data",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    ships.take(5).forEach { ship ->
        ShipStatCard(ship)
        Spacer(modifier = Modifier.height(4.dp))
    }

    if (ships.size > 5) {
        Text(
            text = "...and ${ships.size - 5} more ships",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ShipStatCard(ship: Ship) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Ship ID: ${ship.shipId}", style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem("Battles", "${ship.battles}", Modifier.weight(1f))
                StatItem("Wins", "${ship.wins}", Modifier.weight(1f))
                StatItem("WR", if (ship.battles > 0) String.format("%.1f%%", (ship.wins.toFloat() / ship.battles) * 100) else "-", Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem("Frags", "${ship.frags}", Modifier.weight(1f))
                StatItem("XP", "${ship.totalXP}", Modifier.weight(1f))
                StatItem("DMG", "${ship.totalDamage}", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
