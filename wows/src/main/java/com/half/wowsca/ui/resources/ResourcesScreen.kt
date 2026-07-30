package com.half.wowsca.ui.resources

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    val ctx = LocalContext.current
    val serverState by serverInfoViewModel.uiState.collectAsState()
    val twitchState by twitchViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        serverInfoViewModel.loadServerInfo()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resources") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            // Server Info Section
            Section("Server Populations") {
                when (val s = serverState) {
                    is ServerInfoUiState.Loading -> Text("Loading server data...", modifier = Modifier.padding(16.dp))
                    is ServerInfoUiState.Success -> {
                        ServerStats("World of Tanks", s.serverResult.wotNumbers)
                        Spacer(modifier = Modifier.height(8.dp))
                        ServerStats("World of Warships", s.serverResult.wowsNumbers)
                    }
                    is ServerInfoUiState.Error -> Text("Error: ${s.message}", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error)
                    else -> Text("Loading...", modifier = Modifier.padding(16.dp))
                }
            }

            // Twitch Section
            Section("Twitch Streamers") {
                when (val s = twitchState) {
                    is TwitchUiState.Loading -> Text("Loading streamers...", modifier = Modifier.padding(16.dp))
                    is TwitchUiState.Success -> {
                        s.streamers.forEach { streamer ->
                            Card(modifier = Modifier.fillMaxWidth().padding(4.dp), elevation = CardDefaults.cardElevation(1.dp)) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(streamer.name, fontWeight = FontWeight.Medium)
                                        Text(streamer.gamePlaying ?: "Unknown", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                    else -> Text("Loading...", modifier = Modifier.padding(16.dp))
                }
            }

            // Donation Section
            Section("Support") {
                ClickableRow("Donate via PayPal", "Support development") {
                    onUrlClick("https://www.paypal.com")
                }
                ClickableRow("View Ad", "Support by watching an ad") {
                    // TODO: implement ad viewing
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 4.dp))
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        content()
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun ServerStats(game: String, servers: List<ServerInfo>) {
    Text(game, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp, 8.dp))
    servers.forEach { info ->
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
            Text(info.name ?: "Unknown", modifier = Modifier.width(120.dp), style = MaterialTheme.typography.bodyMedium)
            Text(info.server?.name?.uppercase() ?: "", modifier = Modifier.width(60.dp), style = MaterialTheme.typography.bodySmall)
            Text("${info.players} online", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ClickableRow(title: String, subtitle: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(">", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
