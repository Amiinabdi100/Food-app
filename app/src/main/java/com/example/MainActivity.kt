package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.EnglishStrings
import com.example.ui.LocalStrings
import com.example.ui.MainScreen
import com.example.ui.MainViewModel
import com.example.ui.SomaliStrings
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val languageCode by viewModel.language.collectAsState()
            val darkModePreference by viewModel.darkMode.collectAsState()
            
            val useDarkTheme = darkModePreference ?: isSystemInDarkTheme()
            val currentStrings = if (languageCode == "so") SomaliStrings else EnglishStrings
            
            MyApplicationTheme(darkTheme = useDarkTheme) {
                CompositionLocalProvider(LocalStrings provides currentStrings) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}

