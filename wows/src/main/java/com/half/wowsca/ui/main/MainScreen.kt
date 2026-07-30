package com.half.wowsca.ui.main

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    context: android.content.Context,
) {
    val ctx = context
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }
    val captains = remember { CaptainManager.getCaptains(ctx)?.values?.filterNotNull() ?: emptyList() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Community Assistant", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
                DrawerItem("Search", Icons.Default.Search) { ctx.startActivity(Intent(ctx, SearchActivity::class.java)) }
                DrawerItem("Resources", Icons.Default.Person) {
                    val i = Intent(ctx, ResourcesActivity::class.java)
                    ctx.startActivity(i)
                }
                DrawerItem("Settings", Icons.Default.Settings) { ctx.startActivity(Intent(ctx, SettingActivity::class.java)) }
                DrawerItem("About", Icons.Default.Info) { ctx.startActivity(Intent(ctx, InformationActivity::class.java)) }
                DrawerItem("Rate App", Icons.Default.Star) {
                    try { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${ctx.packageName}"))) }
                    catch (_: Exception) { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${ctx.packageName}"))) }
                }
                DrawerItem("Send Feedback", Icons.Default.Info) {
                    val i = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:slai4@live.com"))
                    i.putExtra(Intent.EXTRA_SUBJECT, "Community Assistant Feedback")
                    ctx.startActivity(i)
                }
                if (captains.isNotEmpty()) {
                    captains.forEach { captain ->
                        NavigationDrawerItem(
                            label = { Text(captain.name ?: "Unknown") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                ctx.startActivity(Intent(ctx, ViewCaptainActivity::class.java).apply {
                                    putExtra("captainId", captain.id)
                                    putExtra("server", captain.server?.name)
                                })
                            },
                            icon = { Icon(Icons.Default.Person, null) }
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Community Assistant") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, selected = selectedTab == 0, onClick = { selectedTab = 0 })
                    NavigationBarItem(icon = { Icon(Icons.Default.Search, null) }, label = { Text("Search") }, selected = selectedTab == 1, onClick = { ctx.startActivity(Intent(ctx, SearchActivity::class.java)) })
                    NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, label = { Text("Encyclopedia") }, selected = selectedTab == 2, onClick = { ctx.startActivity(Intent(ctx, EncyclopediaTabbedActivity::class.java)) })
                    NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, label = { Text("Compare") }, selected = selectedTab == 3, onClick = {
                        if (com.half.wowsca.managers.CompareManager.size() > 1) {
                            ctx.startActivity(Intent(ctx, com.half.wowsca.ui.compare.CompareActivity::class.java))
                        }
                    })
                    NavigationBarItem(icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") }, selected = selectedTab == 4, onClick = { ctx.startActivity(Intent(ctx, SettingActivity::class.java)) })
                }
            }
        ) { padding ->
            when (selectedTab) {
                0 -> HomeTab(captains, context = ctx)
            }
        }
    }
}

@Composable
private fun DrawerItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(title) },
        selected = false,
        onClick = onClick,
        icon = { Icon(icon, null) }
    )
}

@Composable
private fun HomeTab(captains: List<Captain>, context: android.content.Context) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
        item {
            Text("Saved Captains (${captains.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp, 8.dp))
        }
        if (captains.isEmpty()) {
            item {
                Text("No saved captains.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Search for players to get started.", modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(captains, key = { it.id }) { captain ->
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable {
                    context.startActivity(Intent(context, ViewCaptainActivity::class.java).apply {
                        putExtra("captainId", captain.id)
                        putExtra("server", captain.server?.name)
                    })
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
        item { androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(bottom = 80.dp)) }
    }
}
