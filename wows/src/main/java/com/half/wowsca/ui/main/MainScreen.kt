package com.half.wowsca.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.half.wowsca.managers.CaptainManager
import com.half.wowsca.model.Captain
import com.half.wowsca.ui.InformationActivity
import com.half.wowsca.ui.ResourcesActivity
import com.half.wowsca.ui.SearchActivity
import com.half.wowsca.ui.SettingActivity
import com.half.wowsca.ui.encyclopedia.EncyclopediaTabbedActivity
import com.half.wowsca.ui.viewcaptain.ViewCaptainActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(context: android.content.Context) {
    val ctx = context
    var selectedTab by remember { mutableIntStateOf(0) }
    val captains = remember { CaptainManager.getCaptains(ctx)?.values?.filterNotNull() ?: emptyList() }

    fun startActivity(cls: Class<*>) {
        ctx.startActivity(Intent(ctx, cls).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community Assistant") },
                actions = {
                    IconButton(onClick = { startActivity(InformationActivity::class.java) }) {
                        Icon(Icons.Default.Info, "About")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, selected = selectedTab == 0, onClick = { selectedTab = 0 })
                NavigationBarItem(icon = { Icon(Icons.Default.Search, null) }, label = { Text("Search") }, selected = selectedTab == 1, onClick = { startActivity(SearchActivity::class.java) })
                NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, label = { Text("Resources") }, selected = selectedTab == 2, onClick = { startActivity(ResourcesActivity::class.java) })
                NavigationBarItem(icon = { Icon(Icons.Default.Star, null) }, label = { Text("Encyclopedia") }, selected = selectedTab == 3, onClick = { startActivity(EncyclopediaTabbedActivity::class.java) })
                NavigationBarItem(icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") }, selected = selectedTab == 4, onClick = { startActivity(SettingActivity::class.java) })
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> HomeTab(captains, ctx)
        }
    }
}

@Composable
private fun HomeTab(captains: List<Captain>, ctx: android.content.Context) {
    fun start(cls: Class<*>) { ctx.startActivity(Intent(ctx, cls).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
        item {
            Text("Saved Captains (${captains.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp, 8.dp))
        }
        if (captains.isEmpty()) {
            item { Text("No saved captains.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
            item { Text("Search for players to get started.", modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(captains, key = { it.id }) { captain ->
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable {
                    start(ViewCaptainActivity::class.java)
                }, elevation = CardDefaults.cardElevation(2.dp)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(captain.name ?: "Unknown", fontWeight = FontWeight.Medium)
                            Text("Server: ${captain.server?.name?.uppercase() ?: "-"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("ID: ${captain.id}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        item {
            Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp))
        }
        item { QuickAction("Search Players", Icons.Default.Search) { start(SearchActivity::class.java) } }
        item { QuickAction("Encyclopedia", Icons.Default.Person) { start(EncyclopediaTabbedActivity::class.java) } }
        item { QuickAction("Resources", Icons.Default.Info) { start(ResourcesActivity::class.java) } }
        item { QuickAction("Settings", Icons.Default.Settings) { start(SettingActivity::class.java) } }
        item { QuickAction("Rate App", Icons.Default.Star) {
            try { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${ctx.packageName}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
            catch (_: Exception) { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${ctx.packageName}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        } }
        item { Spacer(modifier = Modifier.padding(bottom = 80.dp)) }
    }
}

@Composable
private fun QuickAction(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp).clickable(onClick = onClick), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.padding(start = 12.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
