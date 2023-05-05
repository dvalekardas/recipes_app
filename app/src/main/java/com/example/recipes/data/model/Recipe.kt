package com.example.recipes.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "recipe")
data class Recipe(

	@ColumnInfo(name = "id")
	@field:SerializedName("id")
	@PrimaryKey(autoGenerate = false)
	val id: Int,

	@ColumnInfo(name = "name")
	@field:SerializedName("name")
	val name: String,

	@ColumnInfo(name = "imgUrl")
	@field:SerializedName("url")
	val imgUrl: String,

	@ColumnInfo(name = "instructions")
	@field:SerializedName("instructions")
	val instructions: List<String>?,

	@ColumnInfo(name = "ingredients")
	@field:SerializedName("ingredients")
	val ingredients: List<String>?
)