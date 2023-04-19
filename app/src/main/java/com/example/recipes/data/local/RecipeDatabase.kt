package com.example.recipes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteRecipeEntity::class], version = 1, exportSchema = false)
abstract class RecipeDatabase: RoomDatabase() {
    abstract fun RecipeDataDao(): RecipeDataDao
}