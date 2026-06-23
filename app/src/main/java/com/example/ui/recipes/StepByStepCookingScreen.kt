package com.example.ui.recipes

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.network.RecipeResult
import com.example.ui.LocalStrings
import com.example.ui.MainViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepByStepCookingScreen(
    recipe: RecipeResult,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val strings = LocalStrings.current
    val languageCode by viewModel.language.collectAsState()
    val ttsEnabled by viewModel.ttsEnabled.collectAsState()
    
    var currentStep by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val loc = if (languageCode == "so") Locale("so", "SO") else Locale.ENGLISH
                tts?.language = loc
            }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    val content = if (strings == com.example.ui.SomaliStrings) recipe.so else recipe.en

    LaunchedEffect(currentStep, isPlaying) {
        if (isPlaying && ttsEnabled && tts != null) {
            tts?.speak(content.instructions[currentStep], TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            tts?.stop()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(content.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { if (currentStep > 0) currentStep-- },
                        enabled = currentStep > 0
                    ) {
                        Text(strings.previous)
                    }
                    
                    if (ttsEnabled) {
                        FloatingActionButton(
                            onClick = { isPlaying = !isPlaying }
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) strings.pauseTts else strings.playTts
                            )
                        }
                    }

                    TextButton(
                        onClick = { 
                            if (currentStep < content.instructions.size - 1) {
                                currentStep++ 
                            } else {
                                onBack() // Finish
                            }
                        }
                    ) {
                        Text(if (currentStep < content.instructions.size - 1) strings.next else strings.finish)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LinearProgressIndicator(
                progress = { (currentStep + 1) / content.instructions.size.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "${strings.step} ${currentStep + 1} / ${content.instructions.size}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = content.instructions[currentStep],
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
