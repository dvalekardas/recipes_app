package com.example.recipes

import com.example.recipes.data.local.RecipeDatabase
import com.example.recipes.data.remote.RecipesApi
import com.example.recipes.modules.RecipesModule
import com.example.recipes.utils.UrlConstants

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.testing.TestInstallIn
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
@TestInstallIn(
    components = [ViewModelComponent::class],
    replaces = [RecipesModule::class]
)
class TestRecipesModule{

    @Provides
    fun provideRecipesApi(retrofit: Retrofit): RecipesApi = retrofit.create(RecipesApi::class.java)

    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(UrlConstants.TEST_BASE_URL)
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    @Provides
    fun provideRecipeDatabase(@ApplicationContext context: Context)
            = Room.inMemoryDatabaseBuilder(context, RecipeDatabase::class.java).build()

    @Provides
    fun provideRecipeDao(
        db: RecipeDatabase
    ) = db.RecipeDataDao()
}