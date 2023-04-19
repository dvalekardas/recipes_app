package com.example.recipes.data.remote

import com.example.recipes.data.model.Recipe
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface RecipesApi {
    @GET("recipes")
    fun getRecipes(): Single<List<Recipe>>
}
