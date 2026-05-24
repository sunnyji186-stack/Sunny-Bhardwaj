package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AdOverlayLayout
import com.example.ui.components.MediaDetailSheet
import com.example.ui.components.MediaPlayerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.OttViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: OttViewModel = viewModel()
        val activePlaying = viewModel.activePlayingMedia
        val selectedDetails = viewModel.selectedDetailsMedia

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Main navigation container based on media playback status
            if (activePlaying == null) {
              HomeScreen(viewModel = viewModel)
            } else {
              MediaPlayerScreen(viewModel = viewModel)
            }

            // Global Ad Overlay for voluntary home screen boosters
            if (viewModel.isShowingAdOverlay && activePlaying == null) {
              AdOverlayLayout(
                sponsor = viewModel.currentSponsor,
                countdown = viewModel.adCountdownSeconds,
                canSkip = viewModel.canSkipAd,
                onSkip = { viewModel.skipAdAndStartPlay() }
              )
            }

            // Global Overlay Details Sheet
            if (selectedDetails != null) {
              MediaDetailSheet(viewModel = viewModel)
            }
          }
        }
      }
    }
  }
}

