package com.example.ui.planner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.LocalStrings
import com.example.ui.MainViewModel
import com.example.ui.SomaliStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerTab(viewModel: MainViewModel, onNavigateToSaved: () -> Unit) {
    val mealPlans by viewModel.mealPlans.collectAsState()
    val strings = LocalStrings.current
    
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToSaved,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text(if (strings == SomaliStrings) "Kudar Cuntooyinka" else "Add from Saved") }
            )
        }
    ) { padding ->
        if (mealPlans.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(
                    text = if (strings == SomaliStrings) "Wax qorshe cunto ah halkan kuma jiraan." else "No meal plans yet. Add some from saved recipes!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp)) {
                items(mealPlans) { plan ->
                    val formatter = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
                    val dateString = formatter.format(Date(plan.scheduledDate))
                    
                    val title = if (strings == SomaliStrings) plan.recipeTitleSo else plan.recipeTitleEn
                    
                    ListItem(
                        headlineContent = { Text(title) },
                        supportingContent = { Text("${plan.mealType} - $dateString") },
                        trailingContent = {
                            IconButton(onClick = { viewModel.deleteMealPlan(plan.planId) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    )
                    Divider()
                }
            }
        }
    }
}
