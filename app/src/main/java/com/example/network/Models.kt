package com.example.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecipeContent(
    val title: String,
    val description: String,
    val origin: String = "",
    val difficulty: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val storage: String = "",
    val safety: String = "",
    val vitamins: String = "",
    val minerals: String = "",
    val healthBenefits: String = "",
    val allergens: String = "",
    val dietaryCategories: String = "",
    val alternatives: String = "",
    val sideDishes: String = ""
)

@JsonClass(generateAdapter = true)
data class RecipeResult(
    val id: String = java.util.UUID.randomUUID().toString(),
    val prepTimeMins: Int,
    val cookTimeMins: Int,
    val servings: Int,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val fiber: Int = 0,
    val matchPercentage: Int,
    val missingIngredientsList: List<String>,
    val en: RecipeContent,
    val so: RecipeContent
) {
    @Transient val title: String = en.title
    @Transient val difficulty: String = en.difficulty
    @Transient val ingredientsList: List<String> = en.ingredients
    @Transient val instructions: List<String> = en.instructions
}

@JsonClass(generateAdapter = true)
data class RecipeGenerationResponse(
    val recipes: List<RecipeResult>
)

@JsonClass(generateAdapter = true)
data class IngredientsDetectionResponse(
    val ingredients: List<String>
)
