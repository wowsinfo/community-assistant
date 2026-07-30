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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.half.wowsca.model.Captain

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
        if (captain == null) {
            Text(
                text = "Captain not found",
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                color = MaterialTheme.colorScheme.error
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Captain header
            CaptainHeader(captain)

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
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
private fun CaptainHeader(captain: Captain) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = captain.name ?: "Unknown",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Server: ${captain.server?.name?.uppercase() ?: "-"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            captain.clanName?.let {
                Text(
                    text = "Clan: $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SummaryTab(captain: Captain) {
    val details = captain.details
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (details == null) {
            Text("No stats available", color = MaterialTheme.colorScheme.onSurfaceVariant)
            return@Column
        }

        Text(text = "Overall Stats", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        StatRow2("Battles", "${details.battles}")
        StatRow2("Wins", "${details.wins}")
        StatRow2("Win Rate", formatPercent(details.wins, details.battles))
        StatRow2("Frags", "${details.frags}")
        StatRow2("Total XP", "${details.totalXP}")
        StatRow2("Survived", "${details.survivedBattles}")
        StatRow2("Max XP", "${details.maxXP}")
        StatRow2("Max Frags", "${details.maxFragsInBattle}")
        StatRow2("Total Damage", "${details.totalDamage.toLong()}")

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Team Battles", style = MaterialTheme.typography.titleMedium)
        captain.teamBattleDetails?.let { team ->
            StatRow2("Battles", "${team.battles}")
            StatRow2("Wins", "${team.wins}")
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "PvE Stats", style = MaterialTheme.typography.titleMedium)
        captain.pveDetails?.let { pve ->
            StatRow2("Battles", "${pve.battles}")
            StatRow2("Wins", "${pve.wins}")
        }
    }
}

@Composable
private fun ShipsTab(captain: Captain) {
    val ships = captain.ships
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Recent Ships (${ships?.size ?: 0})", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (ships.isNullOrEmpty()) {
            Text("No ship data", color = MaterialTheme.colorScheme.onSurfaceVariant)
            return@Column
        }

        ships.take(20).forEach { ship ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Ship ID: ${ship.shipId}", style = MaterialTheme.typography.bodyMedium)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        MiniStat("B", "${ship.battles}")
                        MiniStat("W", "${ship.wins}")
                        MiniStat("F", "${ship.frags}")
                        MiniStat("XP", "${ship.totalXP}")
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementsTab(captain: Captain) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Achievements", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        val achievements = captain.achievements
        if (achievements.isNullOrEmpty()) {
            Text("No achievements data", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Text("${achievements.size} achievements earned")
        }
    }
}

@Composable
private fun RankedTab(captain: Captain) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Ranked Seasons", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        val seasons = captain.rankedSeasons
        if (seasons.isNullOrEmpty()) {
            Text("No ranked data", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            seasons.forEach { season ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Season ${season.seasonInt ?: 0}", style = MaterialTheme.typography.bodyMedium)
                        Row {
                            MiniStat("Rank", "${season.rank}")
                            MiniStat("Max", "${season.maxRank}")
                            MiniStat("B", "${season.solo?.battles ?: 0}")
                            MiniStat("W", "${season.solo?.wins ?: 0}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow2(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(120.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    Column(modifier = Modifier.padding(end = 16.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall)
    }
}

private fun formatPercent(part: Int, total: Int): String {
    return if (total > 0) String.format("%.1f%%", (part.toFloat() / total) * 100) else "-"
}
