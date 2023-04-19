package com.example.recipes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe")
data class FavoriteRecipeEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
)