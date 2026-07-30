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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    onBack: () -> Unit,
) {
    val captains = CompareManager.getCaptains().filterNotNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compare") },
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
            // Captain headers
            captains.forEach { captain ->
                Card(modifier = Modifier.fillMaxWidth().padding(12.dp, 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(captain.name ?: "Unknown", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Server: ${captain.server?.name?.uppercase() ?: "-"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Stats comparison
            Text("Stats Comparison", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
            CompareStatRow("Battles", captains.map { formatNum(it.details?.battles ?: 0) })
            CompareStatRow("Wins", captains.map { formatNum(it.details?.wins ?: 0) })
            CompareStatRow("Win Rate", captains.map {
                val b = it.details?.battles ?: 0; val w = it.details?.wins ?: 0
                if (b > 0) String.format("%.1f%%", (w.toFloat() / b) * 100) else "-"
            })
            CompareStatRow("Frags", captains.map { formatNum(it.details?.frags ?: 0) })
            CompareStatRow("Total XP", captains.map { formatNum(it.details?.totalXP ?: 0) })
            CompareStatRow("Avg XP", captains.map {
                val b = it.details?.battles ?: 0; val xp = it.details?.totalXP ?: 0
                if (b > 0) String.format("%.0f", xp.toFloat() / b) else "-"
            })
            CompareStatRow("Survived", captains.map { formatNum(it.details?.survivedBattles ?: 0) })
            CompareStatRow("Max XP", captains.map { formatNum(it.details?.maxXP ?: 0) })
            CompareStatRow("Damage", captains.map { formatNum(it.details?.totalDamage?.toLong() ?: 0) })
            CompareStatRow("Planes", captains.map { formatNum(it.details?.planesKilled ?: 0) })

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CompareStatRow(label: String, values: List<String>) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(100.dp), fontWeight = FontWeight.Medium)
            values.forEach { value ->
                Text(value, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

private fun formatNum(n: Long): String =
    if (n >= 1_000_000) String.format("%.1fM", n / 1_000_000.0)
    else if (n >= 1_000) String.format("%.1fK", n / 1_000.0)
    else "$n"

private fun formatNum(n: Int): String = formatNum(n.toLong())
