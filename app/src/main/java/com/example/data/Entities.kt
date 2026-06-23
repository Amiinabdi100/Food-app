package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey
    val id: String,
    val prepTimeMins: Int,
    val cookTimeMins: Int,
    val servings: Int,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val fiber: Int,
    val matchPercentage: Int, // Stored value for historical purposes
    val missingIngredientsList: List<String>,
    val enContent: String, // JSON string of RecipeContent
    val soContent: String, // JSON string of RecipeContent
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cooking_history")
data class CookingHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val historyId: Int = 0,
    val recipeId: String,
    val recipeTitleEn: String,
    val recipeTitleSo: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "meal_plan")
data class MealPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val planId: Int = 0,
    val recipeId: String,
    val recipeTitleEn: String,
    val recipeTitleSo: String,
    val scheduledDate: Long, // Epoch millis (start of requested day)
    val mealType: String // "Breakfast", "Lunch", "Dinner", "Snack"
)

@Entity(tableName = "shopping_list")
data class ShoppingListItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val isPurchased: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
