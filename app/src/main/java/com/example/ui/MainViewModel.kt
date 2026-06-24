package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.AppDatabase
import com.example.data.GeneralRepository
import com.example.data.SettingsRepository
import com.example.data.ShoppingListItemEntity
import com.example.network.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class DetectState {
    object Idle : DetectState()
    object Loading : DetectState()
    data class Success(val ingredients: List<String>) : DetectState()
    data class Error(val message: String) : DetectState()
}

sealed class RecipeGenState {
    object Idle : RecipeGenState()
    object Loading : RecipeGenState()
    data class Success(val recipes: List<RecipeResult>) : RecipeGenState()
    data class Error(val message: String) : RecipeGenState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repo = GeneralRepository(db.recipeDao(), db.shoppingListDao(), db.mealPlanDao(), db.cookingHistoryDao())
    val settingsRepo = SettingsRepository(application)
    
    val language = settingsRepo.languageFlow.stateIn(viewModelScope, SharingStarted.Lazily, "en")
    val darkMode = settingsRepo.darkModeFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)
    val ttsEnabled = settingsRepo.ttsFlow.stateIn(viewModelScope, SharingStarted.Lazily, true)
    
    val userName = settingsRepo.userNameFlow.stateIn(viewModelScope, SharingStarted.Lazily, "")
    val userEmail = settingsRepo.userEmailFlow.stateIn(viewModelScope, SharingStarted.Lazily, "")
    val dietaryPrefs = settingsRepo.dietaryPrefsFlow.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val savedRecipes = repo.allSavedRecipes.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val shoppingItems = repo.allShoppingItems.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val mealPlans = repo.allMealPlans.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val cookingHistory = repo.cookingHistory.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    // Mappers
    fun resultToEntity(result: RecipeResult): com.example.data.RecipeEntity {
        val adapter = moshi.adapter(RecipeContent::class.java)
        return com.example.data.RecipeEntity(
            id = result.id,
            prepTimeMins = result.prepTimeMins,
            cookTimeMins = result.cookTimeMins,
            servings = result.servings,
            calories = result.calories,
            protein = result.protein,
            carbs = result.carbs,
            fat = result.fat,
            fiber = result.fiber,
            matchPercentage = result.matchPercentage,
            missingIngredientsList = result.missingIngredientsList,
            enContent = adapter.toJson(result.en),
            soContent = adapter.toJson(result.so)
        )
    }

    fun entityToResult(entity: com.example.data.RecipeEntity): RecipeResult {
        val adapter = moshi.adapter(RecipeContent::class.java)
        return RecipeResult(
            id = entity.id,
            prepTimeMins = entity.prepTimeMins,
            cookTimeMins = entity.cookTimeMins,
            servings = entity.servings,
            calories = entity.calories,
            protein = entity.protein,
            carbs = entity.carbs,
            fat = entity.fat,
            fiber = entity.fiber,
            matchPercentage = entity.matchPercentage,
            missingIngredientsList = entity.missingIngredientsList,
            en = adapter.fromJson(entity.enContent)!!,
            so = adapter.fromJson(entity.soContent)!!
        )
    }

    private val _detectState = MutableStateFlow<DetectState>(DetectState.Idle)
    val detectState: StateFlow<DetectState> = _detectState
    
    private val _recipeGenState = MutableStateFlow<RecipeGenState>(RecipeGenState.Idle)
    val recipeGenState: StateFlow<RecipeGenState> = _recipeGenState
    
    var selectedRecipe by mutableStateOf<RecipeResult?>(null)

    fun setLanguage(lang: String) = viewModelScope.launch { settingsRepo.setLanguage(lang) }
    fun setDarkMode(enabled: Boolean) = viewModelScope.launch { settingsRepo.setDarkMode(enabled) }
    fun setTtsEnabled(enabled: Boolean) = viewModelScope.launch { settingsRepo.setTtsEnabled(enabled) }
    
    fun setUserName(name: String) = viewModelScope.launch { settingsRepo.setUserName(name) }
    fun setUserEmail(email: String) = viewModelScope.launch { settingsRepo.setUserEmail(email) }
    fun setDietaryPrefs(prefs: List<String>) = viewModelScope.launch { settingsRepo.setDietaryPrefs(prefs) }

    fun addShoppingItem(name: String) = viewModelScope.launch { repo.addShoppingItem(name) }
    fun updateShoppingItem(id: Int, isPurchased: Boolean) = viewModelScope.launch { repo.updateShoppingItem(id, isPurchased) }
    fun deleteShoppingItem(id: Int) = viewModelScope.launch { repo.deleteShoppingItem(id) }
    fun clearCompletedShoppingItems() = viewModelScope.launch { repo.clearCompletedShoppingItems() }
    
    fun saveRecipe(recipe: com.example.data.RecipeEntity) = viewModelScope.launch { repo.saveRecipe(recipe) }
    fun deleteRecipe(id: String) = viewModelScope.launch { repo.deleteRecipe(id) }

    fun addMealPlan(plan: com.example.data.MealPlanEntity) = viewModelScope.launch { repo.addMealPlan(plan) }
    fun deleteMealPlan(id: Int) = viewModelScope.launch { repo.deleteMealPlan(id) }

    fun recordCookingHistory(history: com.example.data.CookingHistoryEntity) = viewModelScope.launch { repo.addCookingHistory(history) }

    private fun cleanJson(text: String): String {
        var cleaned = text.trim()
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.removePrefix("```json")
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.removePrefix("```")
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.removeSuffix("```")
        }
        return cleaned.trim()
    }

    fun detectIngredients(bitmap: Bitmap, lang: String) {
        viewModelScope.launch {
            _detectState.value = DetectState.Loading
            try {
                if (!InternetChecker.isInternetAvailable(getApplication())) {
                    _detectState.value = DetectState.Error(
                        if (lang == "so") "📡 Fadlan hubi khadkaaga internet-ka oo isku day markale."
                        else "📡 Please check your internet connection and try again."
                    )
                    return@launch
                }
                
                val apiKey = BuildConfig.GEMINI_API_KEY
                val base64Params = bitmap.toBase64()
                val promptText = if (lang == "so") {
                    "Falanqee sawirkan oo ii sheeg magacyada cuntooyinka ku jira talaajadda dhexdeeda oo keliya. Keliya waxaad i siisaa JSON object oo leh field ah 'ingredients' kaas oo ah array of strings, habab kale yuusan yeelan json-ka. Ku soo celi luuqada Soomaaliga."
                } else {
                    "Analyze this photo of an open fridge and list only the names of the food ingredients visible. Return ONLY a valid JSON object with a single field 'ingredients' containing an array of strings representing the detected items."
                }

                val requestBody = GenerateContentRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(
                                Part(text = promptText),
                                Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Params))
                            )
                        )
                    ),
                    generationConfig = GenerationConfig(responseMimeType = "application/json")
                )

                val response = RetrofitClient.service.generateContent(apiKey, requestBody)
                val textResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                
                if (textResponse != null) {
                    val cleanedText = cleanJson(textResponse)
                    val adapter = moshi.adapter(IngredientsDetectionResponse::class.java)
                    val result = adapter.fromJson(cleanedText)
                    if (result != null && result.ingredients.isNotEmpty()) {
                        _detectState.value = DetectState.Success(result.ingredients)
                    } else {
                        _detectState.value = DetectState.Error(
                            if (lang == "so") "Natiijooyin ku filan lama helin. Fadlan isku day mar kale."
                            else "No ingredients found or bad format."
                        )
                    }
                } else {
                    _detectState.value = DetectState.Error(
                        if (lang == "so") "Jawaab madhan oo ku saabsan codsiga AI."
                        else "Empty response from API"
                    )
                }
            } catch (e: java.net.UnknownHostException) {
                Log.e("DetectError", "UnknownHostException: ${e.message}")
                _detectState.value = DetectState.Error(
                    if (lang == "so") "📡 Fadlan hubi khadkaaga internet-ka oo isku day markale."
                    else "📡 Please check your internet connection and try again."
                )
            } catch (e: retrofit2.HttpException) {
                Log.e("DetectError", "HttpException: ${e.code()} ${e.message()}")
                val msg = when (e.code()) {
                    429 -> if (lang == "so") "Kalkaalshaha AI aad buu u mashquulsan yahay hadda. Fadlan isku day xoogaa yar ka dib." else "The AI service is temporarily busy. Please try again in a moment."
                    400 -> if (lang == "so") "Cillad farsamo oo ku saabsan codsiga AI. Fadlan isku day mar kale." else "Invalid request format to AI. Please try again."
                    403 -> if (lang == "so") "Furaha API (API Key) waa khalad ama lama oggola. Fadlan hubi API Key-gaaga." else "Invalid API Key or permission denied. Please check your API key."
                    404 -> if (lang == "so") "Adeegga AI lama helin (404). Fadlan la xiriir horumariyaha." else "AI service not found (404). Please contact the developer."
                    else -> if (lang == "so") "Cillad xiriirka server-ka ah (Malaha khaladaadka API). Fadlan isku day mar kale." else "Server communication error. Please try again."
                }
                _detectState.value = DetectState.Error(msg)
            } catch (e: Exception) {
                Log.e("DetectError", "Exception: ${e.message}")
                _detectState.value = DetectState.Error(
                    if (lang == "so") "Waxbaa khaldamay. Fadlan isku day mar kale."
                    else "Something went wrong. Please try again."
                )
            }
        }
    }

    fun generateRecipes(ingredients: List<String>, filters: List<String>, lang: String) {
        viewModelScope.launch {
            _recipeGenState.value = RecipeGenState.Loading
            try {
                if (!InternetChecker.isInternetAvailable(getApplication())) {
                    _recipeGenState.value = RecipeGenState.Error(
                        if (lang == "so") "📡 Fadlan hubi khadkaaga internet-ka oo isku day markale."
                        else "📡 Please check your internet connection and try again."
                    )
                    return@launch
                }
                
                val apiKey = BuildConfig.GEMINI_API_KEY
                
                val joinedIngredients = ingredients.joinToString(", ")
                val joinedFilters = if (filters.isEmpty()) "None" else filters.joinToString(", ")
                
                val promptText = """
                    Act as an expert culinary API. Generate 3 to 5 realistic recipes that can be made using some or all of the following available ingredients.
                    Available ingredients: $joinedIngredients
                    Dietary Filters/Restrictions/Preferences: $joinedFilters
                    
                    For each recipe provide comprehensive details including nutritional facts, health benefits, side dishes, etc.
                    CRITICAL: Every recipe MUST be fully bilingual. You must provide all text fields in 'en' (English) and 'so' (Somali). The Somali content must be natural, fluent, and highly accurate.
                    
                    RETURN ONLY VALID JSON IN EXACTLY THIS FORMAT, WITH NO EXTRA TEXT STRAYING OUTSIDE THE JSON:
                    {
                      "recipes": [
                        {
                          "prepTimeMins": 10,
                          "cookTimeMins": 20,
                          "servings": 2,
                          "calories": 400,
                          "protein": 15,
                          "carbs": 30,
                          "fat": 10,
                          "fiber": 5,
                          "matchPercentage": 85,
                          "missingIngredientsList": ["sugar", "salt"],
                          "en": {
                            "title": "Recipe Name",
                            "description": "Short description of the food",
                            "origin": "Cultural background/origin",
                            "difficulty": "Easy/Medium/Hard",
                            "ingredients": ["1 cup rice", "2 tbsp oil"],
                            "instructions": ["Step 1...", "Step 2..."],
                            "storage": "How to store leftovers",
                            "safety": "Food safety tips",
                            "vitamins": "Vitamin A, B-complex...",
                            "minerals": "Iron, Potassium...",
                            "healthBenefits": "Explanation of benefits",
                            "allergens": "Contains dairy...",
                            "dietaryCategories": "Halal, Keto...",
                            "alternatives": "Substitute oil with butter...",
                            "sideDishes": "Serve with salad..."
                          },
                          "so": {
                            "title": "Magaca Cuntada",
                            "description": "Faahfaahin kooban oo ku saabsan cuntada",
                            "origin": "Asalka iyo dhaqanka",
                            "difficulty": "Fudud/Dhexdhexaad/Adag",
                            "ingredients": ["1 koob oo bariis ah", "2 qaado oo saliid ah"],
                            "instructions": ["Talaabada 1...", "Talaabada 2..."],
                            "storage": "Sida loo keydiyo",
                            "safety": "Talooyin badbaadada cuntada",
                            "vitamins": "Fiitamiin A, B-complex...",
                            "minerals": "Birta, Botaasiyam...",
                            "healthBenefits": "Faa'iidooyinka caafimaadka",
                            "allergens": "Waxaa ku jira caano...",
                            "dietaryCategories": "Xalaal, Keto...",
                            "alternatives": "Saliidda waxaa lagu bedeli karaa subag...",
                            "sideDishes": "Waxaa lala cuni karaa salad..."
                          }
                        }
                      ]
                    }
                """.trimIndent()

                val requestBody = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = promptText)))
                    ),
                    generationConfig = GenerationConfig(responseMimeType = "application/json")
                )

                val response = RetrofitClient.service.generateContent(apiKey, requestBody)
                val textResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                
                if (textResponse != null) {
                    val cleanedText = cleanJson(textResponse)
                    val adapter = moshi.adapter(RecipeGenerationResponse::class.java)
                    val result = adapter.fromJson(cleanedText)
                    if (result != null && result.recipes.isNotEmpty()) {
                        _recipeGenState.value = RecipeGenState.Success(result.recipes)
                    } else {
                        _recipeGenState.value = RecipeGenState.Error(
                            if (lang == "so") "Natiijooyin ku filan lama helin. Fadlan isku day mar kale."
                            else "No recipes generated."
                        )
                    }
                } else {
                    _recipeGenState.value = RecipeGenState.Error(
                        if (lang == "so") "Jawaab madhan oo ku saabsan codsiga AI."
                        else "Empty response from API"
                    )
                }
            } catch (e: java.net.UnknownHostException) {
                Log.e("RecipeGenError", "UnknownHostException: ${e.message}")
                _recipeGenState.value = RecipeGenState.Error(
                    if (lang == "so") "📡 Fadlan hubi khadkaaga internet-ka oo isku day markale."
                    else "📡 Please check your internet connection and try again."
                )
            } catch (e: retrofit2.HttpException) {
                Log.e("RecipeGenError", "HttpException: ${e.code()} ${e.message()}")
                val msg = when (e.code()) {
                    429 -> if (lang == "so") "Kalkaalshaha AI aad buu u mashquulsan yahay hadda. Fadlan isku day xoogaa yar ka dib." else "The AI service is temporarily busy. Please try again in a moment."
                    400 -> if (lang == "so") "Cillad farsamo oo ku saabsan codsiga AI. Fadlan hubi agabka aad dooratay oo isku day mar kale." else "Technical error with the AI request. Please check your selected ingredients and try again."
                    403 -> if (lang == "so") "Furaha API (API Key) waa khalad ama lama oggola. Fadlan hubi API Key-gaaga." else "Invalid API Key or permission denied. Please check your API key."
                    404 -> if (lang == "so") "Adeegga AI lama helin (404). Fadlan la xiriir horumariyaha." else "AI service not found (404). Please contact the developer."
                    else -> if (lang == "so") "Cillad xiriirka server-ka ah (Malaha khaladaadka API). Fadlan isku day mar kale." else "Server communication error. Please try again."
                }
                _recipeGenState.value = RecipeGenState.Error(msg)
            } catch (e: Exception) {
                Log.e("RecipeGenError", "Exception: ${e.message}")
                _recipeGenState.value = RecipeGenState.Error(
                    if (lang == "so") "Waxbaa khaldamay. Fadlan isku day mar kale."
                    else "Something went wrong. Please try again."
                )
            }
        }
    }
    
    fun resetDetectState() {
        _detectState.value = DetectState.Idle
    }
    
    fun resetRecipeGenState() {
        _recipeGenState.value = RecipeGenState.Idle
    }
}
