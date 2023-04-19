package com.example.recipes

import com.example.recipes.data.local.FavoriteRecipeEntity
import com.example.recipes.utils.BaseTest
import com.example.recipes.mockData.expectedRecipesData
import com.example.recipes.data.repository.RecipesRepository
import com.example.recipes.data.viewmodel.RecipesViewModel
import getOrAwaitValue
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class RecipesViewModelTest: BaseTest() {
    private lateinit var recipesViewModel: RecipesViewModel

    @Mock
    private lateinit var recipesRepository: RecipesRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        recipesViewModel = RecipesViewModel(recipesRepository)
    }

    @Test
    fun `fetchRecipes should get recipes successfully from repository when everything is ok`() = runBlocking{
        //Arrange
        `when`(recipesRepository.fetchRecipes()).thenReturn(Single.just(expectedRecipesData.recipes))

        //Act
        recipesViewModel.fetchRecipes().join()

        //Assert
        Assert.assertEquals(false, recipesViewModel.error.getOrAwaitValue())
        Assert.assertEquals(false, recipesViewModel.isLoading.getOrAwaitValue())
        Assert.assertEquals(expectedRecipesData.recipes, recipesViewModel.recipes.getOrAwaitValue())
    }

    @Test
    fun `fetchRecipes should get error from repository when error occurs`()= runBlocking{
        //Arrange
        `when`(recipesRepository.fetchRecipes()).thenReturn(Single.error(expectedRecipesData.error))

        //Act
        val result  = recipesViewModel.fetchRecipes()
        result.join()

        //Assert
        Assert.assertTrue(recipesViewModel.error.getOrAwaitValue())
        Assert.assertFalse(recipesViewModel.isLoading.getOrAwaitValue())
        Assert.assertTrue(recipesViewModel.recipes.getOrAwaitValue().isEmpty())
    }

    @Test
    fun `addRecipeToFavorites should add recipe to favorites`() = runBlocking{
        //Arrange
        val recipe = expectedRecipesData.recipes[0]
        val favoriteRecipe = FavoriteRecipeEntity(recipe.id)

        //Act
        val job = recipesViewModel.addRecipeToFavorites(recipe)
        job.join()

        //Assert
        val favoritesList = recipesViewModel.favoriteRecipes.getOrAwaitValue()
        Assert.assertTrue(favoritesList.contains(favoriteRecipe))
        verify(recipesRepository).addRecipeToFavorites(favoriteRecipe)
    }

    @Test
    fun `addRecipeToFavorites should throw an error when error occurs`(): Unit = runBlocking{
        //Arrange
        val recipe = expectedRecipesData.recipes[0]
        val favRecipe = FavoriteRecipeEntity(recipe.id)
        val exception = RuntimeException("Failed to add recipe to favorites")

        //Act
        doThrow(exception).`when`(recipesRepository).addRecipeToFavorites(favRecipe)

        //Assert
        val favoritesList = recipesViewModel.favoriteRecipes.getOrAwaitValue()
        Assert.assertFalse(favoritesList.contains(FavoriteRecipeEntity(recipe.id)))
        Assert.assertTrue(favoritesList.isEmpty())
        Assert.assertThrows(RuntimeException::class.java){
            throw exception
        }
    }

    @Test
    fun `removeRecipeFromFavorites should remove recipe from favorites`() = runBlocking{
        //Arrange
        val recipe = expectedRecipesData.recipes[0]
        val favoriteRecipe = FavoriteRecipeEntity(recipe.id)

        //Act
        recipesViewModel.addRecipeToFavorites(recipe).join()
        recipesViewModel.removeRecipeFromFavorites(recipe).join()

        //Assert
        val favoritesList = recipesViewModel.favoriteRecipes.getOrAwaitValue()
        Assert.assertTrue(!favoritesList.contains(favoriteRecipe))
        verify(recipesRepository).removeRecipeFromFavorites(favoriteRecipe)
    }

    @Test
    fun `removeRecipeFromFavorites should throw an error when error occurs`(): Unit = runBlocking{
        //Arrange
        val recipe = expectedRecipesData.recipes[0]
        val favRecipe = FavoriteRecipeEntity(recipe.id)
        val exception = RuntimeException("Failed to remove recipe from favorites")

        //Act
        doThrow(exception).`when`(recipesRepository).removeRecipeFromFavorites(favRecipe)

        //Assert
        Assert.assertThrows(RuntimeException::class.java){
            throw exception
        }
    }

    @Test
    fun `changeFavoriteOnlyState should toggle onlyFavoriteRecipes state`() {
        // Act
        recipesViewModel.changeFavoriteOnlyState()

        // Assert
        Assert.assertTrue(recipesViewModel.onlyFavoriteRecipes.value == true)

        // Act
        recipesViewModel.changeFavoriteOnlyState()

        // Assert
        Assert.assertTrue(recipesViewModel.onlyFavoriteRecipes.value == false)
    }
}