package com.half.wowsca.ui.search

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.half.wowsca.managers.CaptainManager
import com.half.wowsca.managers.CompareManager
import com.half.wowsca.model.Captain
import com.half.wowsca.model.enums.Server

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onCompareClick: () -> Unit,
    onCaptainClick: (Captain) -> Unit,
) {
    val ctx = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedServer by remember { mutableStateOf(Server.NA) }
    var showServerMenu by remember { mutableStateOf(false) }
    var bookmarkCaptain by remember { mutableStateOf<Captain?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        // Server selector
                        Box {
                            Row(
                                modifier = Modifier.clickable { showServerMenu = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedServer.name.uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.ArrowDropDown, "Server", tint = Color.White)
                            }
                            DropdownMenu(expanded = showServerMenu, onDismissRequest = { showServerMenu = false }) {
                                Server.entries.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text("${s.name.uppercase()} - ${s.serverName}") },
                                        onClick = { selectedServer = s; showServerMenu = false }
                                    )
                                }
                            }
                        }
                        // Search field
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Player name", color = Color.White.copy(alpha = 0.5f)) },
                            leadingIcon = { Icon(Icons.Default.Search, "Search", tint = Color.White) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = ""; viewModel.resetState() }) {
                                        Icon(Icons.Default.Close, "Clear", tint = Color.White)
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { viewModel.search(searchQuery, selectedServer) }),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White.copy(alpha = 0.5f),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (val state = uiState) {
                    is SearchUiState.Idle -> {
                        val saved = CaptainManager.getCaptains(ctx)?.values?.filterNotNull() ?: emptyList()
                        if (saved.isNotEmpty() && searchQuery.isBlank()) {
                            LazyColumn {
                                item { Text("Saved Captains (${saved.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp, 12.dp)) }
                                items(saved, key = { it.id }) { c ->
                                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp).clickable { onCaptainClick(c) }, elevation = CardDefaults.cardElevation(2.dp)) {
                                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Column(modifier = Modifier.weight(1f)) { Text(c.name ?: "Unknown", fontWeight = FontWeight.Medium) }
                                            Text(c.server?.name?.uppercase() ?: "", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        } else {
                            Text("Enter a player name to search", modifier = Modifier.align(Alignment.Center).padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    is SearchUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    is SearchUiState.Success -> {
                        LazyColumn {
                            items(state.results, key = { it.id }) { captain ->
                                val idStr = CaptainManager.createCapIdStr(captain.server, captain.id)
                                val isSaved = CaptainManager.getCaptains(ctx)?.containsKey(idStr) == true
                                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp).clickable { CompareManager.addCaptain(captain, false) }, elevation = CardDefaults.cardElevation(2.dp)) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(captain.name ?: "Unknown", fontWeight = FontWeight.Medium)
                                            Text("ID: ${captain.id}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text(captain.server?.name?.uppercase() ?: "", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        if (isSaved) Text("Saved", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(start = 4.dp))
                                    }
                                }
                            }
                        }
                    }
                    is SearchUiState.Error -> Text(state.message, modifier = Modifier.align(Alignment.Center).padding(16.dp), color = MaterialTheme.colorScheme.error)
                }
            }
            // Bottom bar
            val compareCap = CompareManager.getCaptains().filterNotNull()
            val compareSize = compareCap.size
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    when { compareSize > 1 -> compareCap.joinToString(" vs ") { it.name ?: "?" }
                        compareSize == 1 -> "1 captain selected. Long-press to add more."
                        else -> "Select 2 or 3 captains to compare."
                    },
                    style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f).padding(end = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(onClick = onCompareClick, enabled = compareSize > 1) { Text("Compare") }
            }
        }
    }

    bookmarkCaptain?.let { cap ->
        val idStr = CaptainManager.createCapIdStr(cap.server, cap.id)
        val already = CaptainManager.getCaptains(ctx)?.containsKey(idStr) == true
        AlertDialog(
            onDismissRequest = { bookmarkCaptain = null },
            title = { Text(if (already) "Remove Captain?" else "Save Captain?") },
            text = { Text(if (already) "Remove ${cap.name} from saved?" else "Save ${cap.name} to your list?") },
            confirmButton = {
                TextButton(onClick = {
                    if (already) CaptainManager.removeCaptain(ctx, idStr)
                    else CaptainManager.saveCaptain(ctx, cap)
                    bookmarkCaptain = null
                }) { Text(if (already) "Remove" else "Save") }
            },
            dismissButton = { TextButton(onClick = { bookmarkCaptain = null }) { Text("Cancel") } }
        )
    }
}
