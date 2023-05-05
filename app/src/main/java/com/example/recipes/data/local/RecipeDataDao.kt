package com.example.recipes.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.recipes.data.model.FavoriteRecipeEntity
import com.example.recipes.data.model.Recipe

@Dao
interface RecipeDataDao {
    @Insert
    suspend fun addRecipeToFavorites(recipe: FavoriteRecipeEntity)

    @Delete
    suspend fun removeRecipeFromFavorites(recipe: FavoriteRecipeEntity)

    @Query("SELECT * FROM 'favorite'")
    suspend fun getFavoriteRecipes():MutableList<FavoriteRecipeEntity>

    @Insert
    suspend fun addRecipe(newRecipe: Recipe)

    @Query("SELECT * FROM 'recipe'")
    suspend fun getSavedRecipesFromDB(): MutableList<Recipe>
}