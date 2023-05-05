package com.example.recipes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recipes.data.model.FavoriteRecipeEntity
import com.example.recipes.data.model.Recipe

@Database(entities = [FavoriteRecipeEntity::class, Recipe::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RecipeDatabase: RoomDatabase() {
    abstract fun RecipeDataDao(): RecipeDataDao
}