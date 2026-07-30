package com.half.wowsca.ui.encyclopedia

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.half.wowsca.CAApp
import com.half.wowsca.managers.InfoManager
import com.half.wowsca.model.encyclopedia.items.CaptainSkill
import com.half.wowsca.model.encyclopedia.items.EquipmentInfo
import com.half.wowsca.model.encyclopedia.items.ExteriorItem
import com.half.wowsca.model.encyclopedia.items.ShipInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncyclopediaScreen(
    context: android.content.Context,
    onShipClick: (ShipInfo) -> Unit,
    onCompareClick: () -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf("Ships", "Upgrades", "Flags", "Skills")

    val ships = remember {
        CAApp.infoManager?.getShipInfo(context)?.items?.values?.filterNotNull() ?: emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Encyclopedia") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { i, title ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(title) })
                }
            }

            when (selectedTab) {
                0 -> ShipsTab(ships, searchQuery, { searchQuery = it }, onShipClick)
                1 -> UpgradesTab(context)
                2 -> FlagsTab(context)
                3 -> SkillsTab(context)
            }
        }
    }
}

@Composable
private fun ShipsTab(
    ships: List<ShipInfo>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onShipClick: (ShipInfo) -> Unit,
) {
    Column(modifier = Modifier.padding(12.dp)) {
        OutlinedTextField(
            value = searchQuery, onValueChange = onSearchChange,
            placeholder = { Text("Search ships...") }, leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {}),
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        val filtered = if (searchQuery.isBlank()) ships
        else ships.filter { it.name?.contains(searchQuery, ignoreCase = true) == true }

        if (filtered.isEmpty()) {
            Text("No ships found", modifier = Modifier.padding(vertical = 24.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(filtered, key = { it.shipId }) { ship ->
                    Card(modifier = Modifier.fillMaxWidth().clickable { onShipClick(ship) }, elevation = CardDefaults.cardElevation(2.dp)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ship.name ?: "Unknown", fontWeight = FontWeight.Medium)
                                Text("Tier ${ship.tier} ${ship.nation ?: ""} ${ship.type ?: ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("T${ship.tier}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UpgradesTab(context: android.content.Context) {
    val upgrades: List<com.half.wowsca.model.encyclopedia.items.EquipmentInfo> = CAApp.infoManager?.getUpgrades(context)?.items?.values?.filterNotNull() ?: emptyList()
    if (upgrades.isEmpty()) { Text("No upgrades data", modifier = Modifier.padding(16.dp)); return }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(12.dp)) {
        items(upgrades) { upgrade ->
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(upgrade.name ?: "Unknown", fontWeight = FontWeight.Medium)
                    upgrade.description?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
        }
    }
}

@Composable
private fun FlagsTab(context: android.content.Context) {
    val flags: List<com.half.wowsca.model.encyclopedia.items.ExteriorItem> = CAApp.infoManager?.getExteriorItems(context)?.items?.values?.filterNotNull() ?: emptyList()
    if (flags.isEmpty()) { Text("No flags data", modifier = Modifier.padding(16.dp)); return }

    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items(flags) { flag ->
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(flag.name ?: "Unknown", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodySmall)
                    Text(flag.description ?: "", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun SkillsTab(context: android.content.Context) {
    val skills: List<com.half.wowsca.model.encyclopedia.items.CaptainSkill> = CAApp.infoManager?.getCaptainSkills(context)?.items?.values?.filterNotNull() ?: emptyList()
    if (skills.isEmpty()) { Text("No skills data", modifier = Modifier.padding(16.dp)); return }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(12.dp)) {
        items(skills) { skill ->
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(skill.name ?: "Unknown", fontWeight = FontWeight.Medium)
                    skill.getAbilities()?.forEach { ability -> ability?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
                }
            }
        }
    }
}
