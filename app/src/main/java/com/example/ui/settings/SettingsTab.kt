package com.example.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ui.LocalStrings
import com.example.ui.MainViewModel
import com.example.ui.SomaliStrings

@Composable
fun SettingsTab(viewModel: MainViewModel) {
    val strings = LocalStrings.current
    val currentLang by viewModel.language.collectAsState()
    val isDarkMode by viewModel.darkMode.collectAsState()
    val isTtsEnabled by viewModel.ttsEnabled.collectAsState()
    
    var showGuide by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }
    
    // Fake account state for demonstration
    var isAccountCreated by remember { mutableStateOf(false) }
    var accountEmail by remember { mutableStateOf("") }
    
    if (showAccountDialog) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            title = { Text(if (strings == SomaliStrings) "Samayso Akoon Cusub" else "Create New Account") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(if (strings == SomaliStrings) "Magacaaga" else "Your Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(if (strings == SomaliStrings) "Iimeelkaaga" else "Email Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(if (strings == SomaliStrings) "Lambarka Sirta (Password)" else "Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { 
                    if (email.isNotBlank() && password.isNotBlank()) {
                        isAccountCreated = true
                        accountEmail = email
                        showAccountDialog = false 
                    }
                }) {
                    Text(if (strings == SomaliStrings) "Samayso Akoon" else "Create Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAccountDialog = false }) {
                    Text(if (strings == SomaliStrings) "Jooji" else "Cancel")
                }
            }
        )
    }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(if (strings == SomaliStrings) "Dejinta (Settings)" else "Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        // --- Account Creation Section ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (strings == SomaliStrings) "Akoonkaaga" else "Your Account",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (isAccountCreated) {
                    Text(text = if (strings == SomaliStrings) "Kusoo dhawoow, $accountEmail" else "Welcome, $accountEmail")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = { isAccountCreated = false }) {
                        Text(if (strings == SomaliStrings) "Ka Bax Akoonka (Logout)" else "Logout")
                    }
                } else {
                    Text(
                        text = if (strings == SomaliStrings) "Samayso akoon si aad u kaydiso xogtaada iyo cuntooyinka aad jeceshahay." else "Create an account to save your data and favorite recipes.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showAccountDialog = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (strings == SomaliStrings) "Samayso Akoon" else "Create Account")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // --- Language Settings (Dedicated Section) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (strings == SomaliStrings) "Dejinta Luqadda" else "Language Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (strings == SomaliStrings) "Fadlan dooro luqadaada hoo" else "Please choose your language",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Surface(
                    onClick = { viewModel.setLanguage("en") },
                    shape = MaterialTheme.shapes.medium,
                    color = if (currentLang == "en") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                        RadioButton(selected = currentLang == "en", onClick = { viewModel.setLanguage("en") })
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("English", fontWeight = FontWeight.Medium)
                    }
                }
                
                Surface(
                    onClick = { viewModel.setLanguage("so") },
                    shape = MaterialTheme.shapes.medium,
                    color = if (currentLang == "so") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                        RadioButton(selected = currentLang == "so", onClick = { viewModel.setLanguage("so") })
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Af-Soomaali", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // --- App Settings ---
        Text(if (strings == SomaliStrings) "Doorbidyada Abka" else "App Preferences", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
            Text(strings.darkMode, style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isDarkMode ?: false, onCheckedChange = { viewModel.setDarkMode(it) })
        }
        HorizontalDivider()
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
            Text(strings.ttsEnabled, style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isTtsEnabled, onCheckedChange = { viewModel.setTtsEnabled(it) })
        }
        HorizontalDivider()
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
            Text(if (strings == SomaliStrings) "Ogeysiisyada (Notifications)" else "Notifications", style = MaterialTheme.typography.bodyLarge)
            var notificationsEnabled by remember { mutableStateOf(true) }
            Switch(checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
        }
        HorizontalDivider()
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
            Text(if (strings == SomaliStrings) "Keydinta Qalabka (Offline Storage)" else "Offline Storage", style = MaterialTheme.typography.bodyLarge)
            var offlineEnabled by remember { mutableStateOf(true) }
            Switch(checked = offlineEnabled, onCheckedChange = { offlineEnabled = it })
        }
        HorizontalDivider()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // --- How to Use the App (User Guide) ---
        Card(
            modifier = Modifier.fillMaxWidth().clickable { showGuide = !showGuide },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (strings == SomaliStrings) "Sida Loo Isticmaalo Abka" else "How to Use the App",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(if (showGuide) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown, contentDescription = null)
                }
                AnimatedVisibility(visible = showGuide) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        GuideSection(
                            title = if (strings == SomaliStrings) "1. Bilawga" else "1. Getting Started",
                            content = if (strings == SomaliStrings) "Abka ka fur shaashadda gurigaaga. U gudub Settings si aad akoon u samaysato oo aad u doorato luqadda: 'Fadlan dooro luqadaada'." 
                                      else "Open the app from your home screen. Navigate to Settings to create an account and select your language."
                        )
                        GuideSection(
                            title = if (strings == SomaliStrings) "2. Baarista Agabka" else "2. Ingredient Detection",
                            content = if (strings == SomaliStrings) "Taabo astaanta kamarada qaybta Guriga (Home). Sawir ka qaad qaboojiyahaaga ama ka dooro gallery-ga, AI-ga ayaa aqoonsan doona wixii cunto ah." 
                                      else "Tap the camera icon on the Home tab. Take a photo of your fridge or upload from the gallery to let AI detect your ingredients."
                        )
                        GuideSection(
                            title = if (strings == SomaliStrings) "3. Raadinta Cuntada" else "3. Recipe Search",
                            content = if (strings == SomaliStrings) "Isticmaal qaybta Raadi (Discover) si aad cuntooyin cusub u baadho, ama marka agabka la aqoonsado, taabo 'Samee Cunto' si laguugu sameeyo cuntooyin." 
                                      else "Use the Discover tab to search for recipes manually, or after detecting ingredients, tap 'Generate Recipes' to get personalized ideas."
                        )
                        GuideSection(
                            title = if (strings == SomaliStrings) "4. Habka Karinta & Codka" else "4. Cooking Mode",
                            content = if (strings == SomaliStrings) "Fur cunto kasta oo taabo 'Karbi'. Abka wuxuu ku siin doonaa tilmaamo talaabo-talaabo ah waxaana loo akhrin karaa si cod ah haddii la daaro." 
                                      else "Open a recipe and tap 'Cook Now'. Follow step-by-step instructions. Enable Text-to-Speech in settings to hear instructions aloud."
                        )
                        GuideSection(
                            title = if (strings == SomaliStrings) "5. Liiska Dukaanka" else "5. Shopping List",
                            content = if (strings == SomaliStrings) "Agabka kaa dhiman waxaad si toos ah ugu dari kartaa Liiska Dukaanka (Shopping). Halkaas ayaad wax ka bedeli kartaa ama ka tirtiri kartaa markaad iibsato." 
                                      else "Missing ingredients can be added to your Shopping list. You can edit quantities or mark items as purchased directly from the tab."
                        )
                        GuideSection(
                            title = if (strings == SomaliStrings) "6. Adeegsiga Khadka La'aanta" else "6. Offline Usage",
                            content = if (strings == SomaliStrings) "Cuntooyinka aad kaydiso waxaa lagu arkayaa qaybta 'Cuntooyinka Joogtada' ee Profile-kaaga. Waxaad u isticmaali kartaa internet la'aan." 
                                      else "Recipes you save are stored locally. Access them from your Profile under 'Saved Recipes' without needing an internet connection."
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // --- About App ---
        Text(strings.aboutApp, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (strings == SomaliStrings) "Kaaliyaha Cuntada & Qaboojiyaha Casriga ah wuxuu isticmaalaa AI si uu kaaga caawiyo inaad qorsheyso cuntooyinka iyo karinta." else "Smart Fridge & Culinary Assistant uses AI to help you identify ingredients and cook amazing recipes.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(strings.version + ": 1.0.0", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text(strings.devInfo, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text(strings.privacyInfo, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun GuideSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(text = content, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
    }
}


