package com.example.ui.shopping

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.ui.LocalStrings
import com.example.ui.MainViewModel

@Composable
fun ShoppingTab(viewModel: MainViewModel) {
    val strings = LocalStrings.current
    val items by viewModel.shoppingItems.collectAsState()
    
    var newItemText by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(strings.tabShopping, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = newItemText,
                onValueChange = { newItemText = it },
                placeholder = { Text(strings.manualAddHint) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newItemText.isNotBlank()) {
                        viewModel.addShoppingItem(newItemText.trim())
                        newItemText = ""
                    }
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Default.Add, contentDescription = strings.add)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(strings.emptyList, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { viewModel.clearCompletedShoppingItems() }) {
                    Text(strings.clearCompleted)
                }
            }
            LazyColumn {
                items(items, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.isPurchased,
                                onCheckedChange = { checked -> viewModel.updateShoppingItem(item.id, checked) }
                            )
                            Text(
                                text = item.name,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge,
                                textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                            )
                            IconButton(onClick = { viewModel.deleteShoppingItem(item.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
