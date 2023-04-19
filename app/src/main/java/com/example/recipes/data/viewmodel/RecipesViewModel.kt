package com.example.recipes.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.R
import com.example.recipes.data.local.FavoriteRecipeEntity
import com.example.recipes.data.model.Recipe

import com.example.recipes.data.repository.RecipesRepository
import com.example.recipes.utils.EspressoIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(private val recipesRepository: RecipesRepository) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _error = MutableLiveData(false)
    val error: LiveData<Boolean>
        get() = _error

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>>
        get() = _recipes

    private val _favoriteRecipes = MutableLiveData<MutableList<FavoriteRecipeEntity>>()
    val favoriteRecipes: LiveData<MutableList<FavoriteRecipeEntity>>
        get() = _favoriteRecipes

    private val _toastMessage = MutableLiveData<Int>()
    val toastMessage: LiveData<Int>
        get() = _toastMessage

    private val _onlyFavoriteRecipes = MutableLiveData<Boolean>()
    val onlyFavoriteRecipes: LiveData<Boolean>
        get() = _onlyFavoriteRecipes

    init{
        _recipes.value = emptyList()
        _favoriteRecipes.value = mutableListOf()
        _onlyFavoriteRecipes.value = false
    }
    fun fetchRecipes() = viewModelScope.launch(Dispatchers.IO) {
        EspressoIdlingResource.increment()
        _isLoading.postValue(true)
        _error.postValue(false)
        recipesRepository.fetchRecipes()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<List<Recipe>>(){
                override fun onSuccess(incomingRecipes: List<Recipe>) {
                    _isLoading.value = false
                    _error.value = false
                    val recipesTemp: MutableList<Recipe> = mutableListOf()
                    incomingRecipes.forEach {
                        recipesTemp.add(it)
                    }
                    _recipes.postValue(recipesTemp)
                    EspressoIdlingResource.decrement()
                }

                override fun onError(e: Throwable) {
                    if(e.localizedMessage?.contains("Failed to connect to") == true){
                        _toastMessage.value = R.string.server_error
                    }
                    _isLoading.value = false
                    _error.value = true
                    _recipes.value = emptyList()
                    EspressoIdlingResource.decrement()
                }
            })
    }

    fun addRecipeToFavorites(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        val currentList = favoriteRecipes.value
        val recipeEntity = FavoriteRecipeEntity(recipe.id)
        try{
            currentList!!.add(recipeEntity)
            _favoriteRecipes.postValue(currentList!!)
            recipesRepository.addRecipeToFavorites(recipeEntity)
        }catch(e:Exception){
            _toastMessage.value = R.string.favorite_recipe_add_error
        }
    }

    fun removeRecipeFromFavorites(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        val currentList = favoriteRecipes.value
        val recipeEntity = FavoriteRecipeEntity(recipe.id)
        try{
            currentList!!.remove(recipeEntity)
            _favoriteRecipes.postValue(currentList!!)
            recipesRepository.removeRecipeFromFavorites(recipeEntity)
        }catch(e:Exception){
            _toastMessage.value =  R.string.favorite_recipe_remove_error
        }
    }

    fun getFavoriteRecipes() = viewModelScope.launch(Dispatchers.IO){
        _favoriteRecipes.postValue(recipesRepository.getFavoriteRecipes())
    }

    fun changeFavoriteOnlyState() {
        _onlyFavoriteRecipes.value = !_onlyFavoriteRecipes.value!!
    }
}