package com.half.wowsca.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.half.wowsca.ui.encyclopedia.EncyclopediaViewModel

@Composable
fun SplashScreen(
    encyclopediaViewModel: EncyclopediaViewModel,
    onContinue: () -> Unit,
) {
    val isLoading by encyclopediaViewModel.isLoading.collectAsState()
    val isLoaded by encyclopediaViewModel.isLoaded.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo image (200dp height as original)
            Image(
                painter = painterResource(id = com.half.wowsca.R.drawable.launcher_icon),
                contentDescription = "App Logo",
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress indicator
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading game data...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Continue button (shown when loaded or when data already exists)
            if (isLoaded) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onContinue) {
                    Text("Continue")
                }
            }
        }
    }
}
