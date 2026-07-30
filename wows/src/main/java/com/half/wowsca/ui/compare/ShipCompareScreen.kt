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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.half.wowsca.managers.CompareManager
import com.half.wowsca.model.Captain
import com.half.wowsca.model.Ship

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShipCompareScreen(
    onBack: () -> Unit,
) {
    val captains = CompareManager.getCaptains().filterNotNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ship Compare") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (captains.isEmpty()) {
            Text("No players selected", modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp))
            return@Scaffold
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            captains.forEach { captain ->
                Text(captain.name ?: "Unknown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp, 8.dp))
                val ships = captain.ships?.take(10) ?: emptyList()
                if (ships.isEmpty()) {
                    Text("No ship data", modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    ships.forEach { ship ->
                        ShipStatCard(ship)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ShipStatCard(ship: Ship) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Ship ID: ${ship.shipId}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                MiniStat("Battles", "${ship.battles}")
                MiniStat("Wins", "${ship.wins}")
                MiniStat("WR", if (ship.battles > 0) String.format("%.1f%%", (ship.wins.toFloat() / ship.battles) * 100) else "-")
                MiniStat("Frags", "${ship.frags}")
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                MiniStat("XP", formatNum(ship.totalXP))
                MiniStat("Damage", formatNum(ship.totalDamage.toLong()))
                MiniStat("Survived", "${ship.survivedBattles}")
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    Column(modifier = Modifier.padding(end = 12.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}

private fun formatNum(n: Long): String =
    if (n >= 1_000_000) String.format("%.1fM", n / 1_000_000.0)
    else if (n >= 1_000) String.format("%.1fK", n / 1_000.0)
    else "$n"
