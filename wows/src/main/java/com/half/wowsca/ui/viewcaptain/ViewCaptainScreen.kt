package com.half.wowsca.ui.viewcaptain

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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.half.wowsca.model.Captain
import com.half.wowsca.model.Ship
import com.half.wowsca.model.Statistics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewCaptainScreen(
    captain: Captain?,
    onBack: () -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Summary", "Ships", "Achievements", "Ranked")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(captain?.name ?: "Captain Profile") },
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
        if (captain == null) {
            Text("Captain not found", modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), color = MaterialTheme.colorScheme.error)
            return@Scaffold
        }

        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Captain header card
            Card(modifier = Modifier.fillMaxWidth().padding(12.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(captain.name ?: "Unknown", style = MaterialTheme.typography.headlineSmall)
                    Text("Server: ${captain.server?.name?.uppercase() ?: "-"}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    captain.clanName?.let { Text("Clan: $it", style = MaterialTheme.typography.bodyMedium) }
                }
            }

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { i, title ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(title) })
                }
            }

            when (selectedTab) {
                0 -> SummaryTab(captain)
                1 -> ShipsTab(captain)
                2 -> AchievementsTab(captain)
                3 -> RankedTab(captain)
            }
        }
    }
}

@Composable
private fun SummaryTab(captain: Captain) {
    val d = captain.details
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        if (d == null) { Text("No stats available"); return@Column }

        Text("Overall Stats", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        StatRow("Battles", formatNum(d.battles))
        StatRow("Wins", formatNum(d.wins))
        StatRow("Win Rate", formatPercent(d.wins, d.battles))
        StatRow("Frags", formatNum(d.frags))
        StatRow("Total XP", formatNum(d.totalXP))
        StatRow("Survived Battles", formatNum(d.survivedBattles))
        StatRow("Max XP", formatNum(d.maxXP))
        StatRow("Max Frags", formatNum(d.maxFragsInBattle))
        StatRow("Total Damage", formatNum(d.totalDamage.toLong()))
        StatRow("Planes Killed", formatNum(d.planesKilled))
        StatRow("Capture Points", formatNum(d.capturePoints))
        StatRow("Dropped Capture", formatNum(d.droppedCapturePoints))

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))

        // PvP stats
        pveSection("PvE Stats", captain.pveDetails)
        pveSection("PvP Solo", captain.pvpSoloDetails)
        pveSection("PvP Div2", captain.pvpDiv2Details)
        pveSection("PvP Div3", captain.pvpDiv3Details)
        pveSection("Team Battles", captain.teamBattleDetails)
    }
}

@Composable
private fun pveSection(title: String, stats: Statistics?) {
    if (stats == null) return
    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    StatRow("Battles", formatNum(stats.battles))
    StatRow("Wins", formatNum(stats.wins))
    StatRow("Win Rate", formatPercent(stats.wins, stats.battles))
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun ShipsTab(captain: Captain) {
    val ships = captain.ships
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Ships (${ships?.size ?: 0})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        if (ships.isNullOrEmpty()) { Text("No ship data"); return@Column }
        ships.forEach { ship ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), elevation = CardDefaults.cardElevation(1.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Ship ID: ${ship.shipId}", style = MaterialTheme.typography.bodyMedium)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        MiniStat("Battles", "${ship.battles}")
                        MiniStat("Wins", "${ship.wins}")
                        MiniStat("WR", formatPercent(ship.wins, ship.battles))
                        MiniStat("Frags", "${ship.frags}")
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        MiniStat("XP", formatNum(ship.totalXP))
                        MiniStat("Damage", formatNum(ship.totalDamage.toLong()))
                        MiniStat("Planes", "${ship.planesKilled}")
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementsTab(captain: Captain) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Achievements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        val a = captain.achievements
        if (a.isNullOrEmpty()) Text("No achievements data")
        else Text("${a.size} achievements earned")
    }
}

@Composable
private fun RankedTab(captain: Captain) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Ranked Seasons", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        val seasons = captain.rankedSeasons
        if (seasons.isNullOrEmpty()) { Text("No ranked data"); return@Column }
        seasons.forEach { s ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), elevation = CardDefaults.cardElevation(1.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Season ${s.seasonInt ?: "?"}", style = MaterialTheme.typography.bodyMedium)
                    Row {
                        MiniStat("Rank", "${s.rank}")
                        MiniStat("Max Rank", "${s.maxRank}")
                        MiniStat("Stars", "${s.stars}")
                        MiniStat("Stage", "${s.stage}")
                    }
                    s.solo?.let { Row {
                        MiniStat("Battles", "${it.battles}")
                        MiniStat("Wins", "${it.wins}")
                        MiniStat("WR", formatPercent(it.wins, it.battles))
                    } }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(140.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    Column(modifier = Modifier.padding(end = 12.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}

private fun formatPercent(part: Int, total: Int): String =
    if (total > 0) String.format("%.1f%%", (part.toFloat() / total) * 100) else "-"

private fun formatNum(n: Long): String =
    if (n >= 1_000_000) String.format("%.1fM", n / 1_000_000.0)
    else if (n >= 1_000) String.format("%.1fK", n / 1_000.0)
    else "$n"

private fun formatNum(n: Int): String = formatNum(n.toLong())
