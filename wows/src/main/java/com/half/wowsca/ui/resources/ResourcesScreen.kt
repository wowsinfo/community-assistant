package com.half.wowsca.ui.resources

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.half.wowsca.model.ServerInfo
import com.half.wowsca.model.enums.Server

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(
    serverInfoViewModel: ServerInfoViewModel,
    twitchViewModel: TwitchViewModel,
    onUrlClick: (String) -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Servers", "Twitch")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resources") },
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
        ) {
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
                0 -> ServerInfoTab(serverInfoViewModel)
                1 -> TwitchTab(twitchViewModel, onUrlClick)
            }
        }
    }
}

@Composable
private fun ServerInfoTab(serverInfoViewModel: ServerInfoViewModel) {
    val state by serverInfoViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Server Populations",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(12.dp))

        when (val s = state) {
            is ServerInfoUiState.Idle -> {
                serverInfoViewModel.loadServerInfo()
            }
            is ServerInfoUiState.Loading -> {
                Text("Loading...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            is ServerInfoUiState.Success -> {
                s.serverResult.wotNumbers.forEach { info ->
                    ServerInfoCard(info, "WoT")
                }
                s.serverResult.wowsNumbers.forEach { info ->
                    ServerInfoCard(info, "WoWS")
                }
            }
            is ServerInfoUiState.Error -> {
                Text("Error: ${s.message}", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun ServerInfoCard(info: ServerInfo, game: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${info.name ?: "Unknown"} (${game})",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = info.server?.name?.uppercase() ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${info.players} online",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun TwitchTab(
    twitchViewModel: TwitchViewModel,
    onUrlClick: (String) -> Unit,
) {
    val state by twitchViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Twitch Streamers",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(12.dp))

        when (val s = state) {
            is TwitchUiState.Idle -> {
                Text("Loading streamers...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            is TwitchUiState.Loading -> {
                Text("Loading...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            is TwitchUiState.Success -> {
                s.streamers.forEach { streamer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = streamer.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = streamer.gamePlaying ?: "Unknown game",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            is TwitchUiState.Error -> {
                Text("Error: ${s.message}", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
