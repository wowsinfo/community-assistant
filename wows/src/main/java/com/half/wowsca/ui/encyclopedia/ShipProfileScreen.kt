package com.half.wowsca.ui.encyclopedia

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.half.wowsca.CAApp
import com.half.wowsca.managers.InfoManager
import com.half.wowsca.model.encyclopedia.items.ShipInfo
import com.half.wowsca.model.encyclopedia.items.ShipModuleItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShipProfileScreen(
    context: android.content.Context,
    shipId: Long,
    onBack: () -> Unit,
) {
    val ships = CAApp.infoManager?.getShipInfo(context)?.items
    val ship = ships?.get(shipId)
    var selectedModuleTab by remember { mutableIntStateOf(0) }
    var selectedModuleId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ship?.name ?: "Ship Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (ship == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            // Header
            Card(Modifier.fillMaxWidth().padding(12.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(ship.name ?: "", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Tier ${ship.tier} ${ship.nation ?: ""} ${ship.type ?: ""}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (ship.price > 0) Text("Price: ${ship.price} credits", style = MaterialTheme.typography.bodyMedium)
                    if (ship.goldPrice > 0) Text("Gold: ${ship.goldPrice}", style = MaterialTheme.typography.bodyMedium)
                    if (ship.isPremium) Text("Premium Ship", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            // Description
            ship.description?.let { desc ->
                if (desc.isNotBlank()) {
                    Card(Modifier.fillMaxWidth().padding(12.dp, 4.dp), elevation = CardDefaults.cardElevation(1.dp)) {
                        Text(desc, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Ship Stats Bars (like original progress bars)
            Card(Modifier.fillMaxWidth().padding(12.dp, 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Ship Characteristics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    StatBar("Artillery", 0.7f)
                    StatBar("Survivability", 0.5f)
                    StatBar("Torpedoes", 0.3f)
                    StatBar("AA Defense", 0.6f)
                    StatBar("Maneuverability", 0.4f)
                    StatBar("Concealment", 0.5f)
                }
            }

            // Detailed Stats
            Card(Modifier.fillMaxWidth().padding(12.dp, 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    StatRow("Tier", "${ship.tier}")
                    StatRow("Type", ship.type ?: "-")
                    StatRow("Nation", ship.nation ?: "-")
                }
            }

            // Module tabs
            Text("Modules", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp, 8.dp))

            val moduleTabs = listOf("Hull", "Engine", "Fire Ctrl", "Artillery", "Torpedoes", "Flight Ctrl")
            TabRow(selectedTabIndex = selectedModuleTab) {
                moduleTabs.forEachIndexed { i, title ->
                    Tab(selected = selectedModuleTab == i, onClick = { selectedModuleTab = i }, text = { Text(title, style = MaterialTheme.typography.labelSmall) })
                }
            }

            // Show module items based on selected tab
            val moduleIds = when (selectedModuleTab) {
                0 -> ship.hull
                1 -> ship.engine
                2 -> ship.fireControl
                3 -> ship.artillery
                4 -> ship.torps
                5 -> ship.flightControl
                else -> emptyList()
            }

            if (!moduleIds.isNullOrEmpty()) {
                moduleIds.forEach { id ->
                    val module = ship.items?.get(id)
                    if (module != null) {
                        ModuleCard(module)
                    }
                }
            } else {
                Text("No modules", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Next ships
            if (!ship.nextShipIds.isNullOrEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text("Next Ships", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp, 8.dp))
                ship.nextShipIds.forEach { nextId ->
                    val nextShip = ships?.get(nextId)
                    Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp), elevation = CardDefaults.cardElevation(1.dp)) {
                        Text(nextShip?.name ?: "Ship #$nextId", modifier = Modifier.padding(12.dp))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatBar(label: String, progress: Float) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(120.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.weight(1f).height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(120.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ModuleCard(module: ShipModuleItem) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(module.name ?: "Module", fontWeight = FontWeight.Medium)
                Text("ID: ${module.id}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            module.price_xp?.let { if (it > 0) Text("${it} XP", style = MaterialTheme.typography.bodySmall) }
        }
    }
}
