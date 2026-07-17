package com.instagram.unfollowers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.instagram.unfollowers.data.model.AnalysisState
import com.instagram.unfollowers.ui.screens.HomeScreen
import com.instagram.unfollowers.ui.screens.ResultsScreen
import com.instagram.unfollowers.ui.theme.InstagramUnfollowersTheme
import com.instagram.unfollowers.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InstagramUnfollowersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InstagramUnfollowersApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun InstagramUnfollowersApp(viewModel: MainViewModel) {
    val analysisState by viewModel.analysisState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = analysisState,
            transitionSpec = {
                fadeIn() + slideInVertically { it / 8 } togetherWith
                        fadeOut() + slideOutVertically { -it / 8 }
            },
            label = "screen_transition"
        ) { state ->
            when (state) {
                is AnalysisState.Idle -> {
                    HomeScreen(viewModel = viewModel)
                }

                is AnalysisState.Loading -> {
                    LoadingScreen()
                }

                is AnalysisState.Success -> {
                    ResultsScreen(
                        result = state.result,
                        viewModel = viewModel
                    )
                }

                is AnalysisState.Error -> {
                    ErrorScreen(
                        message = state.message,
                        onRetry = { viewModel.reset() }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(56.dp)
            )
            Text(
                text = "Veriler analiz ediliyor...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("⚠️", style = MaterialTheme.typography.headlineLarge.copy())
            Text(
                text = "Bir hata oluştu",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Text("Tekrar Dene")
            }
        }
    }
}
