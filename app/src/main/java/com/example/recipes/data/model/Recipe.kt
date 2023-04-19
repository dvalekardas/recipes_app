package com.example.recipes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Recipe(
	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("url")
	val imgUrl: String,

	@field:SerializedName("instructions")
	val instructions: List<String>,

	@field:SerializedName("ingredients")
	val ingredients: List<String>
)