package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY timestamp DESC")
    fun getAllSavedRecipes(): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipeById(id: String)
    
    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: String): RecipeEntity?
}

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_list ORDER BY isPurchased ASC, timestamp DESC")
    fun getAllItems(): Flow<List<ShoppingListItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingListItemEntity)

    @Query("UPDATE shopping_list SET isPurchased = :isPurchased WHERE id = :id")
    suspend fun updatePurchasedState(id: Int, isPurchased: Boolean)

    @Query("DELETE FROM shopping_list WHERE id = :id")
    suspend fun deleteItemById(id: Int)

    @Query("DELETE FROM shopping_list WHERE isPurchased = 1")
    suspend fun clearCompleted()
}

@Dao
interface MealPlanDao {
    @Query("SELECT * FROM meal_plan ORDER BY scheduledDate ASC")
    fun getAllMealPlans(): Flow<List<MealPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(plan: MealPlanEntity)

    @Query("DELETE FROM meal_plan WHERE planId = :id")
    suspend fun deleteMealPlanById(id: Int)
}

@Dao
interface CookingHistoryDao {
    @Query("SELECT * FROM cooking_history ORDER BY timestamp DESC")
    fun getCookingHistory(): Flow<List<CookingHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: CookingHistoryEntity)
}
