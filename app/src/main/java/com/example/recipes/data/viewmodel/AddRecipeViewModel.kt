package com.example.recipes.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipes.utils.UrlConstants.DEFAULT_IMG
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddRecipeViewModel  @Inject constructor() : ViewModel(){

    private val _ingredients = MutableLiveData<MutableList<String>>()
    val ingredients: LiveData<MutableList<String>>
        get() = _ingredients

    private val _instructions = MutableLiveData<MutableList<String>>()
    val instructions: LiveData<MutableList<String>>
        get() = _instructions

    private val _recipePhotoPath = MutableLiveData<String>()
    val recipePhotoPath: LiveData<String>
        get() = _recipePhotoPath

    init{
        _ingredients.value = mutableListOf()
        _instructions.value = mutableListOf()
        _recipePhotoPath.value = DEFAULT_IMG
    }

    fun addIngredients(ingredients: MutableList<String>){
        _ingredients.value = (_ingredients.value?.plus(ingredients))?.toMutableList()
    }

    fun addInstructions(instructions: MutableList<String>){
        _instructions.value = (_instructions.value?.plus(instructions))?.toMutableList()
    }

    fun setPhotoPath(photoPath: String){
        _recipePhotoPath.value = photoPath
    }

    fun removePhotoPath(){
        _recipePhotoPath.value = DEFAULT_IMG
    }

    fun removeIngredient(ingredient: String) {
        _ingredients.value?.remove(ingredient)
    }

}