package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.home.HomeTab
import com.example.ui.recipes.RecipeDetailScreen
import com.example.ui.recipes.SavedTab
import com.example.ui.recipes.StepByStepCookingScreen
import com.example.ui.settings.SettingsTab
import com.example.ui.shopping.ShoppingTab
import com.example.data.RecipeEntity
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val strings = LocalStrings.current
    
    val bottomBarsRoutes = listOf("home", "discover", "planner", "shopping", "profile")
    val subtitle = if (strings == SomaliStrings) "Kaliyaha Cuntada" else "Cooking Assistant"

    Scaffold(
        topBar = {
            if (currentRoute in bottomBarsRoutes) {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Smart Fridge",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            val languageCode by viewModel.language.collectAsState()
                            Surface(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                onClick = { 
                                    viewModel.setLanguage(if (languageCode == "so") "en" else "so")
                                }
                            ) {
                                Text(
                                    text = "EN / SO",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.CircleShape)
                                    .clickable {
                                        navController.navigate("profile") {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Text(
                                    text = "A",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (currentRoute in bottomBarsRoutes) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.CameraAlt, contentDescription = strings.tabHome) },
                        label = { Text(strings.tabHome) },
                        selected = currentRoute == "home",
                        onClick = {
                            navController.navigate("home") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = "Discover") },
                        label = { Text(if (strings == SomaliStrings) "Raadi" else "Discover") },
                        selected = currentRoute == "discover",
                        onClick = {
                            navController.navigate("discover") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.EventNote, contentDescription = "Planner") },
                        label = { Text(if (strings == SomaliStrings) "Qorshe" else "Planner") },
                        selected = currentRoute == "planner",
                        onClick = {
                            navController.navigate("planner") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = strings.tabShopping) },
                        label = { Text(strings.tabShopping) },
                        selected = currentRoute == "shopping",
                        onClick = {
                            navController.navigate("shopping") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text(if (strings == SomaliStrings) "Kunto" else "Profile") },
                        selected = currentRoute == "profile",
                        onClick = {
                            navController.navigate("profile") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeTab(
                    viewModel = viewModel,
                    onNavigateToRecipe = { recipe ->
                        viewModel.selectedRecipe = recipe
                        navController.navigate("recipeDetail")
                    }
                )
            }
            composable("discover") {
                com.example.ui.discover.DiscoverTab(
                    viewModel = viewModel,
                    onNavigateToRecipe = { recipe ->
                        viewModel.selectedRecipe = recipe
                        navController.navigate("recipeDetail")
                    }
                )
            }
            composable("planner") {
                com.example.ui.planner.PlannerTab(
                    viewModel = viewModel,
                    onNavigateToSaved = { navController.navigate("saved") }
                )
            }
            composable("shopping") {
                ShoppingTab(viewModel = viewModel)
            }
            composable("profile") {
                com.example.ui.profile.ProfileTab(
                    viewModel = viewModel,
                    onNavigateToSaved = { navController.navigate("saved") },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }
            composable("saved") {
                SavedTab(
                    viewModel = viewModel,
                    onNavigateToRecipe = { recipe ->
                        viewModel.selectedRecipe = recipe
                        navController.navigate("recipeDetail")
                    }
                )
            }
            composable("settings") {
                SettingsTab(viewModel = viewModel)
            }
            composable("recipeDetail") {
                val recipe = viewModel.selectedRecipe
                if (recipe != null) {
                    val scope = rememberCoroutineScope()
                    val savedRecipes by viewModel.savedRecipes.collectAsState()
                    val isSaved = savedRecipes.any { viewModel.entityToResult(it).title == recipe.title }

                    RecipeDetailScreen(
                        recipe = recipe,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onStartCooking = { navController.navigate("cooking") }
                    )
                }
            }
            composable("cooking") {
                val recipe = viewModel.selectedRecipe
                if (recipe != null) {
                    StepByStepCookingScreen(
                        recipe = recipe,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
