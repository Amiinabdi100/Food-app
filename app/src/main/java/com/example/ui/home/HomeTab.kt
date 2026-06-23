package com.example.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ui.LocalStrings
import com.example.ui.MainViewModel
import com.example.ui.DetectState
import com.example.ui.RecipeGenState
import com.example.ui.components.IngredientChip
import com.example.ui.components.RecipeCard
import com.example.network.RecipeResult

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeTab(
    viewModel: MainViewModel,
    onNavigateToRecipe: (RecipeResult) -> Unit
) {
    val context = LocalContext.current
    val strings = LocalStrings.current
    val detectState by viewModel.detectState.collectAsState()
    val recipeGenState by viewModel.recipeGenState.collectAsState()
    val languageCode by viewModel.language.collectAsState()

    var ingredients by remember { mutableStateOf(listOf<String>()) }
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var newIngredientText by remember { mutableStateOf("") }
    
    val availableFilters = listOf(
        strings.vegetarian, strings.vegan, strings.keto, strings.lowCarb,
        strings.highProtein, strings.glutenFree, strings.dairyFree,
        strings.halal, strings.healthyMeals
    )

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            viewModel.detectIngredients(bitmap, languageCode)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            if (bitmap != null) {
                viewModel.detectIngredients(bitmap, languageCode)
            }
        }
    }

    LaunchedEffect(detectState) {
        if (detectState is DetectState.Success) {
            ingredients = (detectState as DetectState.Success).ingredients
            // Avoid looping if recomposed
            viewModel.resetDetectState()
        }
    }

    Scaffold(
        floatingActionButton = {
            if (ingredients.isNotEmpty() && recipeGenState !is RecipeGenState.Success) {
                ExtendedFloatingActionButton(
                    text = { Text(strings.findRecipes) },
                    icon = { Icon(Icons.Default.Restaurant, contentDescription = null) },
                    onClick = { viewModel.generateRecipes(ingredients, selectedFilters.toList(), languageCode) }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Initial State / Image Capture
            if (ingredients.isEmpty() && recipeGenState !is RecipeGenState.Success && recipeGenState !is RecipeGenState.Loading) {
                item {
                    // Bento Dashboard Grid
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // AI Camera / Scan Section
                        Card(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.5f), androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    }
                                    Text(
                                        text = "AI VISION",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                                        letterSpacing = androidx.compose.ui.unit.TextUnit(2f, androidx.compose.ui.unit.TextUnitType.Sp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = strings.appName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Scan contents to detect ingredients",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { cameraLauncher.launch() },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text(strings.takePhoto, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = { galleryLauncher.launch("image/*") },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                ) {
                                    Text(strings.uploadPhoto, color = MaterialTheme.colorScheme.primary, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                }
                            }
                        }

                        // Bottom Row (Inventory Status & Quick Stats / Dietary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Inventory Status (Placeholder for Home tab)
                            Card(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                            ) {
                                Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                                    Text("KAYDKA", style = MaterialTheme.typography.labelSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f))
                                    Column {
                                        Text("0", style = MaterialTheme.typography.headlineLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Black, color = MaterialTheme.colorScheme.onTertiaryContainer)
                                        Text("Ingredients", style = MaterialTheme.typography.labelMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MaterialTheme.colorScheme.onTertiaryContainer)
                                    }
                                }
                            }
                            
                            // Quick Stats / Dietary
                            Card(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                            ) {
                                Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                                    Text("FILTER", style = MaterialTheme.typography.labelSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.6f))
                                    Column {
                                        Icon(Icons.Default.FilterList, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(32.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Ready", style = MaterialTheme.typography.labelMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = MaterialTheme.colorScheme.onErrorContainer)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Detecting State
            if (detectState is DetectState.Loading || recipeGenState is RecipeGenState.Loading) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(if (detectState is DetectState.Loading) strings.analyzing else strings.analyzing /* "Generating..." */)
                }
            }
            
            // Detected Ingredients List
            if (ingredients.isNotEmpty() && recipeGenState !is RecipeGenState.Success && recipeGenState !is RecipeGenState.Loading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(strings.detectedIngredients, style = MaterialTheme.typography.titleLarge)
                        IconButton(onClick = { showFilterDialog = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = strings.filters)
                        }
                    }
                    Text(strings.editIngredientsTip, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ingredients.forEach { ingredient ->
                            IngredientChip(
                                text = ingredient,
                                onRemove = {
                                    ingredients = ingredients.filter { it != ingredient }
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = newIngredientText,
                            onValueChange = { newIngredientText = it },
                            placeholder = { Text(strings.addIngredient) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newIngredientText.isNotBlank()) {
                                    ingredients = ingredients + newIngredientText.trim()
                                    newIngredientText = ""
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = strings.add)
                        }
                    }
                }
            }
            
            // Generated Recipes Result
            if (recipeGenState is RecipeGenState.Success) {
                val recipes = (recipeGenState as RecipeGenState.Success).recipes
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(strings.matchingRecipes, style = MaterialTheme.typography.titleLarge)
                        TextButton(onClick = { 
                            ingredients = emptyList() // reset
                            viewModel.resetRecipeGenState()
                        }) {
                            Text(strings.retry)
                        }
                    }
                }
                
                items(recipes) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onNavigateToRecipe(recipe) }
                    )
                }
            }
        }
        
        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                title = { Text(strings.filters) },
                text = {
                    LazyColumn {
                        items(availableFilters) { filter ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable {
                                    selectedFilters = if (selectedFilters.contains(filter)) {
                                        selectedFilters - filter
                                    } else {
                                        selectedFilters + filter
                                    }
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedFilters.contains(filter),
                                    onCheckedChange = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(filter)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFilterDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
