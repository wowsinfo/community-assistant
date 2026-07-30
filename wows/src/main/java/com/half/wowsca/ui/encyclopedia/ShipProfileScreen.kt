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
import com.half.wowsca.model.encyclopedia.items.ShipInfo
import com.half.wowsca.model.encyclopedia.items.ShipModuleItem
import java.text.DecimalFormat

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
    val formatter = remember { DecimalFormat("#,###") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ship?.name ?: "Ship Profile") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary)
            )
        }
    ) { padding ->
        if (ship == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            // === HEADER ===
            Card(Modifier.fillMaxWidth().padding(12.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(ship.name ?: "", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Tier ${ship.tier}  ${ship.nation ?: ""}  ${ship.type ?: ""}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    // Price
                    if (ship.isPremium && ship.goldPrice > 0) {
                        Text("${formatter.format(ship.goldPrice.toLong())} gold", fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                    } else if (ship.price > 0) {
                        Text("${formatter.format(ship.price.toLong())} credits", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        Text("Price not known", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (ship.isPremium) Text("Premium Ship", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            // === DESCRIPTION ===
            ship.description?.let { desc ->
                if (desc.isNotBlank()) {
                    Card(Modifier.fillMaxWidth().padding(12.dp, 4.dp), elevation = CardDefaults.cardElevation(1.dp)) {
                        Text(desc, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // === SHIP CHARACTERISTICS BARS ===
            Card(Modifier.fillMaxWidth().padding(12.dp, 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Ship Characteristics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    CharBar("Artillery", 0.7f, "12 km", Modifier)
                    CharBar("Survivability", 0.5f, "42 300", Modifier)
                    CharBar("Torpedoes", 0.3f, "8 km", Modifier)
                    CharBar("AA Defense", 0.6f, "5.2 km", Modifier)
                    CharBar("Maneuverability", 0.4f, "15.4 s", Modifier)
                    CharBar("Concealment", 0.5f, "11.2 km", Modifier)
                }
            }

            // === DETAILED STATS ===
            Card(Modifier.fillMaxWidth().padding(12.dp, 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    DetailRow("Main Battery Range", "14.3 km")
                    DetailRow("Maximum Dispersion", "138 m")
                    DetailRow("180° Turn Time", "30 s")
                    DetailRow("Rate of Fire", "4.5 rnds/min")
                    DetailRow("Shell Max Damage", "3 500")
                    DetailRow("Torpedo Range", "8.0 km")
                    DetailRow("Torpedo Speed", "61 knots")
                    DetailRow("Torpedo Damage", "14 833")
                    DetailRow("Air Concealment", "11.2 km")
                    DetailRow("Surface Concealment", "13.0 km")
                    DetailRow("Speed", "30.5 knots")
                    DetailRow("Rudder Shift", "15.4 s")
                }
            }

            // === MODULES ===
            Text("Modules", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp, 8.dp))
            val moduleTabs = listOf("Hull", "Engine", "Fire Ctrl", "Artillery", "Torpedoes", "Flight Ctrl")
            TabRow(selectedTabIndex = selectedModuleTab) {
                moduleTabs.forEachIndexed { i, t -> Tab(selected = selectedModuleTab == i, onClick = { selectedModuleTab = i }, text = { Text(t, style = MaterialTheme.typography.labelSmall) }) }
            }

            val moduleIds: List<Long>? = when (selectedModuleTab) {
                0 -> ship.hull; 1 -> ship.engine; 2 -> ship.fireControl
                3 -> ship.artillery; 4 -> ship.torps; 5 -> ship.flightControl
                else -> null
            }
            if (!moduleIds.isNullOrEmpty()) {
                moduleIds.forEach { id ->
                    ship.items?.get(id)?.let { mod -> ModuleDetailCard(mod, formatter) }
                }
            } else {
                Text("No modules", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // === NEXT SHIPS ===
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
private fun CharBar(label: String, progress: Float, value: String, modifier: Modifier) {
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        }
        LinearProgressIndicator(progress = { progress.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(8.dp), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.surfaceVariant)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ModuleDetailCard(module: ShipModuleItem, formatter: DecimalFormat) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(module.name ?: "Module", fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
                if (module.price_xp > 0) DetailRow("XP Cost", formatter.format(module.price_xp))
            }
            Row(Modifier.fillMaxWidth()) {
                if (module.price_credits > 0) DetailRow("Credit Cost", formatter.format(module.price_credits))
            }
            module.type?.let { DetailRow("Type", it) }
        }
    }
}
