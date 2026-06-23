package com.example.data

class GeneralRepository(
    private val recipeDao: RecipeDao,
    private val shoppingListDao: ShoppingListDao,
    private val mealPlanDao: MealPlanDao,
    private val cookingHistoryDao: CookingHistoryDao
) {
    val allSavedRecipes = recipeDao.getAllSavedRecipes()
    val allShoppingItems = shoppingListDao.getAllItems()
    val allMealPlans = mealPlanDao.getAllMealPlans()
    val cookingHistory = cookingHistoryDao.getCookingHistory()

    suspend fun saveRecipe(recipe: RecipeEntity) = recipeDao.insertRecipe(recipe)
    suspend fun deleteRecipe(id: String) = recipeDao.deleteRecipeById(id)
    suspend fun getRecipe(id: String) = recipeDao.getRecipeById(id)

    suspend fun addShoppingItem(name: String) {
        shoppingListDao.insertItem(ShoppingListItemEntity(name = name))
    }
    
    suspend fun updateShoppingItem(id: Int, isPurchased: Boolean) {
        shoppingListDao.updatePurchasedState(id, isPurchased)
    }
    
    suspend fun deleteShoppingItem(id: Int) = shoppingListDao.deleteItemById(id)
    suspend fun clearCompletedShoppingItems() = shoppingListDao.clearCompleted()

    suspend fun addMealPlan(plan: MealPlanEntity) = mealPlanDao.insertMealPlan(plan)
    suspend fun deleteMealPlan(id: Int) = mealPlanDao.deleteMealPlanById(id)

    suspend fun addCookingHistory(history: CookingHistoryEntity) = cookingHistoryDao.insertHistory(history)
}
