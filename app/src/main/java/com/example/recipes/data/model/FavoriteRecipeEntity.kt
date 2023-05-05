package com.example.recipes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class FavoriteRecipeEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
)