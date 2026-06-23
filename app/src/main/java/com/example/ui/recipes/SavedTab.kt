package com.example.ui.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.network.RecipeResult
import com.example.ui.LocalStrings
import com.example.ui.MainViewModel
import com.example.ui.components.RecipeCard

@Composable
fun SavedTab(
    viewModel: MainViewModel,
    onNavigateToRecipe: (RecipeResult) -> Unit
) {
    val strings = LocalStrings.current
    val savedRecipes by viewModel.savedRecipes.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(strings.tabSaved, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (savedRecipes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(strings.emptyList, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(savedRecipes, key = { it.id }) { entity ->
                    // Convert back from entity to result
                    val recipeResult = viewModel.entityToResult(entity)
                    RecipeCard(
                        recipe = recipeResult,
                        onClick = { onNavigateToRecipe(recipeResult) }
                    )
                }
            }
        }
    }
}
