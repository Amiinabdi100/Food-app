package com.example.ui.recipes

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.network.RecipeResult
import com.example.ui.LocalStrings
import com.example.ui.SomaliStrings
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipe: RecipeResult,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onStartCooking: () -> Unit
) {
    val strings = LocalStrings.current
    val savedRecipes by viewModel.savedRecipes.collectAsState()
    val isSaved = savedRecipes.any { viewModel.entityToResult(it).title == recipe.title }
    val scope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            if (isSaved) {
                                val savedItem = savedRecipes.find { it.id == recipe.id || viewModel.entityToResult(it).title == recipe.title }
                                savedItem?.let { viewModel.deleteRecipe(it.id) }
                            } else {
                                viewModel.saveRecipe(viewModel.resultToEntity(recipe))
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (isSaved) androidx.compose.material.icons.Icons.Default.Favorite else androidx.compose.material.icons.Icons.Default.FavoriteBorder,
                            contentDescription = "Save",
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                TextButton(onClick = {
                    val time = System.currentTimeMillis() + 86400000 // tomorrow roughly
                    viewModel.addMealPlan(
                        com.example.data.MealPlanEntity(
                            recipeId = recipe.id,
                            recipeTitleEn = recipe.en.title,
                            recipeTitleSo = recipe.so.title,
                            scheduledDate = time,
                            mealType = "Lunch"
                        )
                    )
                }) {
                    Text(if (strings == SomaliStrings) "Qorshey" else "Add to Plan")
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    viewModel.recordCookingHistory(
                        com.example.data.CookingHistoryEntity(
                            recipeId = recipe.id,
                            recipeTitleEn = recipe.en.title,
                            recipeTitleSo = recipe.so.title
                        )
                    )
                    onStartCooking()
                }, modifier = Modifier.padding(end = 16.dp)) {
                    Text(strings.startCooking)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val content = if (strings == SomaliStrings) recipe.so else recipe.en
            
            item {
                Text(content.description, style = MaterialTheme.typography.bodyLarge)
                if (content.origin.isNotBlank()) {
                    Text("Origin: ${content.origin}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
            
            // Nutrition
            item {
                Text(strings.nutrition, style = MaterialTheme.typography.titleLarge)
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        NutritionStat(strings.calories, "${recipe.calories}")
                        NutritionStat(strings.protein, "${recipe.protein}g")
                        NutritionStat(strings.carbs, "${recipe.carbs}g")
                        NutritionStat(strings.fat, "${recipe.fat}g")
                    }
                }
            }
            
            if (content.vitamins.isNotBlank() || content.minerals.isNotBlank() || content.healthBenefits.isNotBlank()) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (content.healthBenefits.isNotBlank()) {
                                Text(if(strings == SomaliStrings) "Faa'iidooyinka Caafimaad" else "Health Benefits", fontWeight = FontWeight.Bold)
                                Text(content.healthBenefits, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            if (content.vitamins.isNotBlank()) {
                                Text(if(strings == SomaliStrings) "Fiitamiinada" else "Vitamins", fontWeight = FontWeight.Bold)
                                Text(content.vitamins, style = MaterialTheme.typography.bodyMedium)
                            }
                            if (content.minerals.isNotBlank()) {
                                Text(if(strings == SomaliStrings) "Macdanta" else "Minerals", fontWeight = FontWeight.Bold)
                                Text(content.minerals, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            // Ingredients
            item {
                Text(strings.ingredients, style = MaterialTheme.typography.titleLarge)
            }
            itemsIndexed(content.ingredients) { _, ing ->
                Text("• $ing", style = MaterialTheme.typography.bodyLarge)
            }
            
            // Missing Items
            if (recipe.missingIngredientsList.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(strings.missing, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                }
                itemsIndexed(recipe.missingIngredientsList) { _, missing ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("• $missing", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        TextButton(onClick = { viewModel.addShoppingItem(missing) }) {
                            Text(strings.add)
                        }
                    }
                }
            }
            
            // Instructions
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(strings.instructions, style = MaterialTheme.typography.titleLarge)
            }
            itemsIndexed(content.instructions) { index, step ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
                        Text("${index + 1}", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(step, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun NutritionStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}
