package com.example.recipes.data.viewmodel

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipes.R
import com.example.recipes.data.model.FavoriteRecipeEntity
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
    private val _createRecipesFragment = MutableLiveData(false)
    val createRecipesFragment: LiveData<Boolean>
        get() = _createRecipesFragment

    private val _recipeAdded = MutableLiveData(false)
    val recipeAdded: LiveData<Boolean>
        get() = _recipeAdded

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
        _recipeAdded.value = false
        _createRecipesFragment.value = true
        _recipes.value = emptyList()
        _favoriteRecipes.value = mutableListOf()
        _onlyFavoriteRecipes.value = false
    }

    fun initialize(){
        _recipeAdded.value = false
        _createRecipesFragment.value = true
        _recipes.value = emptyList()
        _isLoading.value = false
        _error.value = false
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
                    _recipes.postValue(_recipes.value?.plus(recipesTemp))
                    EspressoIdlingResource.decrement()
                }

                override fun onError(e: Throwable) {
                    if(e.localizedMessage?.contains("Failed to connect to") == true){
                        _toastMessage.value = R.string.server_error
                    }
                    if(_recipes.value?.isEmpty() == true){
                        _isLoading.value = false
                        _error.value = true
                    }
                    EspressoIdlingResource.decrement()
                }
            })
    }

    fun addRecipeToFavorites(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        val currentList = favoriteRecipes.value
        val recipeEntity = FavoriteRecipeEntity(recipe.id)
        try{
            recipesRepository.addRecipeToFavorites(recipeEntity)
            currentList!!.add(recipeEntity)
            _favoriteRecipes.postValue(currentList!!)
        }catch(e: SQLiteException){
            _toastMessage.value = R.string.favorite_recipe_add_error
        }
    }

    fun removeRecipeFromFavorites(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        val currentList = favoriteRecipes.value
        val recipeEntity = FavoriteRecipeEntity(recipe.id)
        try{
            recipesRepository.removeRecipeFromFavorites(recipeEntity)
            currentList!!.remove(recipeEntity)
            _favoriteRecipes.postValue(currentList!!)
        }catch(e:SQLiteException){
            _toastMessage.value =  R.string.favorite_recipe_remove_error
        }
    }

    fun getFavoriteRecipes() = viewModelScope.launch(Dispatchers.IO){
        _favoriteRecipes.postValue(recipesRepository.getFavoriteRecipes())
    }

    fun changeFavoriteOnlyState() {
        _onlyFavoriteRecipes.value = !_onlyFavoriteRecipes.value!!
    }

    fun addRecipe(newRecipe: Recipe) {
        try{
            viewModelScope.launch(Dispatchers.IO){
                recipesRepository.addRecipe(newRecipe)
            }
            val recipeList = mutableListOf(newRecipe)
            _recipes.value?.let { recipeList.addAll(it) }
            _recipes.value = recipeList
            _recipeAdded.value = true
            _toastMessage.value =  R.string.add_new_recipe_success
        }catch(e:SQLiteException){
            _recipeAdded.value = true
            _toastMessage.value =  R.string.add_new_recipe_error
        }
    }

    fun addSavedRecipesFromDB() = viewModelScope.launch(Dispatchers.IO){
        try{
            val savedRecipesFromDB = recipesRepository.getSavedRecipesFromDB()
            if(savedRecipesFromDB.isNotEmpty()){
                _recipes.postValue(_recipes.value?.plus(savedRecipesFromDB))
            }
        }catch(_:SQLiteException){ }
    }

    fun preventRecipeFragmentRecreation(){
        _createRecipesFragment.value= false
    }

    fun allowRecipeFragmentRecreation(){
        _createRecipesFragment.value= true
    }


    fun preventRecipeToast(){
        _recipeAdded.value = false
    }

    fun setErrorValue() {
       _error.value = true
    }
}