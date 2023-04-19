package com.example.recipes.data.remote

import com.example.recipes.data.model.Recipe
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class RecipesApiService @Inject constructor(private val recipesApi: RecipesApi) {
    fun getRecipes(): Single<List<Recipe>> {
        return  recipesApi.getRecipes()
    }
}
