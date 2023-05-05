package com.example.recipes.data.repository

import com.example.recipes.data.model.FavoriteRecipeEntity
import com.example.recipes.data.local.RecipeDataDao
import com.example.recipes.data.model.Recipe
import com.example.recipes.data.remote.RecipesApiService

import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class RecipesRepository
@Inject constructor(private val recipesApiService: RecipesApiService, private val recipeDataDao: RecipeDataDao) {
    fun fetchRecipes(): Single<List<Recipe>> {
        return recipesApiService.getRecipes()
    }

    suspend fun addRecipeToFavorites(recipe: FavoriteRecipeEntity)
    = recipeDataDao.addRecipeToFavorites(recipe)

    suspend fun removeRecipeFromFavorites(recipe: FavoriteRecipeEntity)
    = recipeDataDao.removeRecipeFromFavorites(recipe)

    suspend fun getFavoriteRecipes(): MutableList<FavoriteRecipeEntity>{
        return recipeDataDao.getFavoriteRecipes()
    }

    suspend fun addRecipe(newRecipe: Recipe) = recipeDataDao.addRecipe(newRecipe)

    suspend fun getSavedRecipesFromDB(): MutableList<Recipe>{
        return recipeDataDao.getSavedRecipesFromDB()
    }

}
