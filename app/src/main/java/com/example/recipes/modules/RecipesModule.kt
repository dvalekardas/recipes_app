package com.example.recipes.modules

import android.content.Context
import androidx.room.Room
import com.example.recipes.data.local.RecipeDatabase
import com.example.recipes.data.remote.RecipesApi
import com.example.recipes.utils.UrlConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(ViewModelComponent::class)
class RecipesModule {

    @Provides
    fun provideRecipesApi(retrofit: Retrofit): RecipesApi = retrofit.create(RecipesApi::class.java)

    @Provides
    fun provideRetrofit():Retrofit = Retrofit.Builder().baseUrl(UrlConstants.BASE_URL)
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()

    @Provides
    fun provideRecipeDatabase(@ApplicationContext context: Context)
    = Room.databaseBuilder(context, RecipeDatabase::class.java, "recipe_database").build()

    @Provides
    fun provideRecipeDao(
        db: RecipeDatabase
    ) = db.RecipeDataDao()
}