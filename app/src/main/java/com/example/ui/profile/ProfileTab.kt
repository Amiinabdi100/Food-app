package com.example.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.LocalStrings
import com.example.ui.MainViewModel
import com.example.ui.SomaliStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTab(
    viewModel: MainViewModel,
    onNavigateToSaved: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val dietaryPrefs by viewModel.dietaryPrefs.collectAsState()
    val strings = LocalStrings.current
    
    var isEditing by remember { mutableStateOf(userName.isEmpty()) }
    var editName by remember { mutableStateOf(userName) }
    var editEmail by remember { mutableStateOf(userEmail) }
    var editPrefs by remember { mutableStateOf(dietaryPrefs.joinToString(", ")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isEditing) {
            OutlinedTextField(
                value = editName,
                onValueChange = { editName = it },
                label = { Text(if (strings == SomaliStrings) "Magacaaga" else "Name") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = editEmail,
                onValueChange = { editEmail = it },
                label = { Text(if (strings == SomaliStrings) "Iimeelkaaga" else "Email") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = editPrefs,
                onValueChange = { editPrefs = it },
                label = { Text(if (strings == SomaliStrings) "Wixii Aan Loo Oggolayn (Xalaal, iwm)" else "Dietary Preferences (comma separated)") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                viewModel.setUserName(editName)
                viewModel.setUserEmail(editEmail)
                viewModel.setDietaryPrefs(editPrefs.split(",").map { it.trim() }.filter { it.isNotBlank() })
                isEditing = false
            }) {
                Text(if (strings == SomaliStrings) "Kaydi" else "Save")
            }
        } else {
            Text(text = if(userName.isEmpty()) "User" else userName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = userEmail, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (dietaryPrefs.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = (if (strings == SomaliStrings) "Xaaladaha cuntada: " else "Preferences: ") + dietaryPrefs.joinToString(", "),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            ListItem(
                headlineContent = { Text(if (strings == SomaliStrings) "Cuntooyinka Joogtada" else "Saved Recipes") },
                leadingContent = { Icon(Icons.Default.Favorite, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateToSaved() }
            )
            ListItem(
                headlineContent = { Text(if (strings == SomaliStrings) "Dejinta" else "Settings") },
                leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) },
                modifier = Modifier.clickable { onNavigateToSettings() }
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { isEditing = true }) {
                Text(if (strings == SomaliStrings) "Beddel Macluumaadka" else "Edit Profile")
            }
        }
    }
}
