package com.example.recipes.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
@Dao
interface RecipeDataDao {
    @Insert
    suspend fun addRecipeToFavorites(recipe: FavoriteRecipeEntity)

    @Delete
    suspend fun removeRecipeFromFavorites(recipe: FavoriteRecipeEntity)

    @Query("SELECT * FROM 'recipe'")
    suspend fun getFavoriteRecipes():MutableList<FavoriteRecipeEntity>
}