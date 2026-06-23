package com.example.ui.discover

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.network.RecipeResult
import com.example.ui.LocalStrings
import com.example.ui.MainViewModel
import com.example.ui.RecipeGenState
import com.example.ui.SomaliStrings
import com.example.ui.components.RecipeCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverTab(viewModel: MainViewModel, onNavigateToRecipe: (RecipeResult) -> Unit) {
    val recipeGenState by viewModel.recipeGenState.collectAsState()
    val languageCode by viewModel.language.collectAsState()
    val strings = LocalStrings.current
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    
    // Automatically load Somali popular recipes on first launch
    LaunchedEffect(Unit) {
        if (recipeGenState !is RecipeGenState.Success && recipeGenState !is RecipeGenState.Loading) {
            val defaultFood = "Canjeero, Muufo, Bariis Iskukaris, Baasto Soomaali, Maraq, Suqaar, Hilib Ari, Sambuus, Malawax, Shuuro, Ukun"
            viewModel.generateRecipes(emptyList(), listOf("Must only be traditional somali dishes: " + defaultFood), languageCode)
        }
    }

    val categories = listOf(
        if (strings == SomaliStrings) "Cuntooyinka Soomaalida" else "Somali Recipes",
        if (strings == SomaliStrings) "Caafimaad" else "Healthy Recipes",
        if (strings == SomaliStrings) "Khudaar Leh" else "Vegetarian Recipes",
        "Keto",
        if (strings == SomaliStrings) "Quraac" else "Breakfast Recipes",
        if (strings == SomaliStrings) "Qado" else "Lunch Recipes",
        if (strings == SomaliStrings) "Casho" else "Dinner Recipes"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (strings == SomaliStrings) "Raadi cunto (Xalaal, Suqaar...)" else "Search recipes...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            trailingIcon = {
                Button(
                    onClick = {
                        if (searchQuery.isNotBlank()) {
                            viewModel.generateRecipes(emptyList(), listOf(searchQuery), languageCode)
                        }
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(if (strings == SomaliStrings) "Raadi" else "Search")
                }
            }
        )

        // Category Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                ElevatedFilterChip(
                    selected = searchQuery == category,
                    onClick = {
                        searchQuery = category
                        viewModel.generateRecipes(emptyList(), listOf(category), languageCode)
                    },
                    label = { Text(category, fontWeight = FontWeight.Medium) }
                )
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (strings == SomaliStrings) "Natiijooyinka La Helay" else "Discover Results",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (recipeGenState is RecipeGenState.Success) {
                TextButton(onClick = {
                    val recipes = (recipeGenState as RecipeGenState.Success).recipes
                    coroutineScope.launch {
                        recipes.forEach { recipe ->
                            viewModel.saveRecipe(viewModel.resultToEntity(recipe))
                        }
                    }
                }) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (strings == SomaliStrings) "Soo Deji (Xariiq La'aan)" else "Download All (Offline)")
                }
            }
        }

        when (val state = recipeGenState) {
            is RecipeGenState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is RecipeGenState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is RecipeGenState.Success -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.recipes) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onNavigateToRecipe(recipe) }
                        )
                    }
                }
            }
            else -> {
                // Idle
            }
        }
    }
}

