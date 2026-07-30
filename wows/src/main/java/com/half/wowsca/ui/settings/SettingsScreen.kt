package com.half.wowsca.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.half.wowsca.ui.InformationActivity
import com.half.wowsca.ui.encyclopedia.EncyclopediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    encyclopediaViewModel: EncyclopediaViewModel,
    onRefreshData: () -> Unit,
    onAboutClick: () -> Unit,
) {
    val ctx = LocalContext.current
    val isLoading by encyclopediaViewModel.isLoading.collectAsState()
    var colorblindMode by remember { mutableStateOf(false) }
    var showAverages by remember { mutableStateOf(false) }
    var noArp by remember { mutableStateOf(false) }
    var loginUser by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
            // Display Section
            Section("Display") {
                SettingToggle("Colorblind Mode", "Adjust colors for colorblindness", colorblindMode, { colorblindMode = it })
                SettingToggle("Show Averages", "Display average stats in lists", showAverages, { showAverages = it })
                SettingToggle("Remove ARP Ships", "Hide ARP/Special ships from lists", noArp, { noArp = it })
            }

            // Account Section
            Section("Account") {
                SettingToggle("Login Primary User", "Auto-login to primary Wargaming account", loginUser, { loginUser = it })
            }

            // Data Section
            Section("Data") {
                SettingClickable("Refresh Stored Data", "Update encyclopedia and game data", Icons.Default.Refresh) {
                    onRefreshData()
                }
                SettingClickable("Memory Settings", "View cached data usage", Icons.Default.Info) {
                    ctx.startActivity(Intent(ctx, InformationActivity::class.java))
                }
                SettingClickable("Clear Player Data", "Remove saved captain information", Icons.Default.Delete) {
                    // TODO: implement
                }
            }

            // Links Section
            Section("Links") {
                SettingClickable("Write a Review", "Rate the app on Google Play", Icons.Default.Star) {
                    try { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${ctx.packageName}"))) }
                    catch (_: Exception) { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${ctx.packageName}"))) }
                }
                SettingClickable("Contact Developer", "slai4@live.com", Icons.Default.MailOutline) {
                    val i = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:slai4@live.com"))
                    i.putExtra(Intent.EXTRA_SUBJECT, "Community Assistant Feedback")
                    ctx.startActivity(i)
                }
                SettingClickable("About", "Version info and credits", Icons.Default.Info) {
                    ctx.startActivity(Intent(ctx, InformationActivity::class.java))
                }
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Refreshing data...", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
private fun SettingToggle(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingClickable(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end = 12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(">", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
