package com.half.wowsca.ui.viewcaptain

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.half.wowsca.model.Captain
import com.half.wowsca.model.Ship
import com.half.wowsca.model.Statistics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewCaptainScreen(captain: Captain?, onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Summary", "Ships", "Achievements", "Graphs", "Ranked")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(captain?.name ?: "Captain Profile") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary)
            )
        }
    ) { padding ->
        if (captain == null) {
            Text("Captain not found", modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), color = MaterialTheme.colorScheme.error)
            return@Scaffold
        }
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Captain header
            Card(Modifier.fillMaxWidth().padding(12.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(captain.name ?: "Unknown", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Server: ${captain.server?.name?.uppercase() ?: "-"}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    captain.clanName?.let { Text("Clan: $it", style = MaterialTheme.typography.bodyMedium) }
                }
            }
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { i, t -> Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(t) }) }
            }
            when (selectedTab) {
                0 -> SummaryTab(captain)
                1 -> ShipsTab2(captain)
                2 -> AchievementsTab2(captain)
                3 -> GraphsTab(captain)
                4 -> RankedTab2(captain)
            }
        }
    }
}

// === SUMMARY TAB ===
@Composable
private fun SummaryTab(captain: Captain) {
    val d = captain.details
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp)) {
        if (d == null) { Text("No stats"); return@Column }

        // Top 5 stat cards
        Text("Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        StatCard("Battles", formatNum(d.battles), "Total games played")
        StatCard("Win Rate", formatPercent(d.wins, d.battles), "${d.wins} wins")
        StatCard("Average XP", if (d.battles > 0) formatNum((d.totalXP / d.battles).toInt()) else "0", "Per battle")
        StatCard("Average Damage", if (d.battles > 0) formatNum((d.totalDamage.toLong() / d.battles).toInt()) else "0", "Per battle")
        StatCard("K/D Ratio", if (d.battles > 0) String.format("%.2f", d.frags.toFloat() / d.battles) else "0", "Frags per battle")

        Spacer(Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))

        // General stats
        Text("General", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        GenRow("Total XP", formatNum(d.totalXP))
        GenRow("Total Damage", formatNum(d.totalDamage.toLong()))
        GenRow("Planes Killed", formatNum(d.planesKilled))
        GenRow("Capture Points", formatNum(d.capturePoints))
        GenRow("Dropped Capture", formatNum(d.droppedCapturePoints))
        GenRow("Profile Level", "${d.tierLevel}")

        Spacer(Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))

        // PvP/PvE sections
        PvESection("PvE Stats", captain.pveDetails)
        PvESection("PvP Solo", captain.pvpSoloDetails)
        PvESection("PvP Div 2", captain.pvpDiv2Details)
        PvESection("PvP Div 3", captain.pvpDiv3Details)
        PvESection("Team Battles", captain.teamBattleDetails)

        // Ships summary
        Spacer(Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))
        Text("Ships (${captain.ships?.size ?: 0})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        captain.ships?.forEach { ship ->
            ShipMiniCard(ship)
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun StatCard(title: String, value: String, subtitle: String) {
    Card(Modifier.fillMaxWidth().padding(vertical = 3.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun GenRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(140.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PvESection(title: String, stats: Statistics?) {
    if (stats == null) return
    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
    GenRow("Battles", formatNum(stats.battles))
    GenRow("Wins", formatNum(stats.wins))
    GenRow("Win Rate", formatPercent(stats.wins, stats.battles))
}

@Composable
private fun ShipMiniCard(ship: Ship) {
    Card(Modifier.fillMaxWidth().padding(vertical = 2.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Ship ${ship.shipId}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                Text("B:${ship.battles} W:${ship.wins} F:${ship.frags}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(formatNum(ship.battles), style = MaterialTheme.typography.bodySmall)
        }
    }
}

// === SHIPS TAB ===
@Composable
private fun ShipsTab2(captain: Captain) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp)) {
        val ships = captain.ships
        Text("Ships (${ships?.size ?: 0})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (ships.isNullOrEmpty()) { Text("No ship data"); return@Column }
        ships.forEach { ship ->
            Card(Modifier.fillMaxWidth().padding(vertical = 2.dp), elevation = CardDefaults.cardElevation(1.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Ship ID: ${ship.shipId}", fontWeight = FontWeight.Medium)
                    Row(Modifier.fillMaxWidth()) { MiniStat("Battles", "${ship.battles}"); MiniStat("Wins", "${ship.wins}"); MiniStat("WR", formatPercent(ship.wins, ship.battles)); MiniStat("Frags", "${ship.frags}") }
                    Row(Modifier.fillMaxWidth()) { MiniStat("XP", formatNum(ship.totalXP)); MiniStat("Damage", formatNum(ship.totalDamage.toLong())); MiniStat("Survived", "${ship.survivedBattles}") }
                }
            }
        }
    }
}

// === ACHIEVEMENTS TAB ===
@Composable
private fun AchievementsTab2(captain: Captain) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Achievements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        val a = captain.achievements
        if (a.isNullOrEmpty()) Text("No achievements data available")
        else Text("${a.size} achievements earned")
    }
}

// === GRAPHS TAB ===
@Composable
private fun GraphsTab(captain: Captain) {
    val ships = captain.ships?.filter { it.battles > 0 } ?: emptyList()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp)) {
        Text("Performance by Tier", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        if (ships.isEmpty()) {
            Text("No ship data available for graphs", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Text("Average Experience (top 10 ships)", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            ships.sortedByDescending { it.totalXP.toFloat() / maxOf(1, it.battles) }.take(10).forEach { s ->
                val avg = if (s.battles > 0) s.totalXP.toFloat() / s.battles else 0f
                GraphBar("Ship ${s.shipId}", formatNum(avg.toLong()))
            }
            Spacer(Modifier.height(16.dp))

            Text("Average Damage (top 10 ships)", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            ships.sortedByDescending { s -> if (s.battles > 0) s.totalDamage.toFloat() / s.battles else 0f }.take(10).forEach { s ->
                val avg = if (s.battles > 0) s.totalDamage.toFloat() / s.battles else 0f
                GraphBar("Ship ${s.shipId}", formatNum(avg.toLong()))
            }
            Spacer(Modifier.height(16.dp))

            Text("Win Rate (top 10 ships)", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            ships.sortedByDescending { s -> if (s.battles > 0) s.wins.toFloat() / s.battles else 0f }.take(10).forEach { s ->
                val wr = if (s.battles > 0) String.format("%.1f%%", (s.wins.toFloat() / s.battles) * 100) else "0%"
                GraphBar("Ship ${s.shipId}", wr)
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun GraphBar(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

// === RANKED TAB ===
@Composable
private fun RankedTab2(captain: Captain) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("Ranked Seasons", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        val seasons = captain.rankedSeasons
        if (seasons.isNullOrEmpty()) { Text("No ranked data"); return@Column }
        seasons.forEach { s ->
            Card(Modifier.fillMaxWidth().padding(vertical = 2.dp), elevation = CardDefaults.cardElevation(1.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Season ${s.seasonInt ?: "?"}", fontWeight = FontWeight.Medium)
                    Row { MiniStat("Rank", "${s.rank}"); MiniStat("Max Rank", "${s.maxRank}"); MiniStat("Stars", "${s.stars}"); MiniStat("Stage", "${s.stage}") }
                    s.solo?.let { Row { MiniStat("Battles", "${it.battles}"); MiniStat("Wins", "${it.wins}"); MiniStat("WR", formatPercent(it.wins, it.battles)) } }
                }
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    Column(Modifier.padding(end = 12.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}

private fun formatPercent(part: Int, total: Int): String = if (total > 0) String.format("%.1f%%", (part.toFloat() / total) * 100) else "-"
private fun formatNum(n: Long): String = if (n >= 1_000_000) String.format("%.1fM", n / 1_000_000.0) else if (n >= 1_000) String.format("%.1fK", n / 1_000.0) else "$n"
private fun formatNum(n: Int): String = formatNum(n.toLong())
