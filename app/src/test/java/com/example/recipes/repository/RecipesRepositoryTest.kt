package com.example.recipes

import android.database.SQLException
import com.example.recipes.data.model.FavoriteRecipeEntity
import com.example.recipes.data.local.RecipeDataDao
import com.example.recipes.utils.BaseTest
import com.example.recipes.mockData.expectedRecipesData
import com.example.recipes.data.remote.RecipesApiService
import com.example.recipes.data.repository.RecipesRepository
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RecipesRepositoryTest: BaseTest() {
    private lateinit var recipesRepository: RecipesRepository

    @Mock
    private lateinit var recipesApiService : RecipesApiService

    @Mock
    private lateinit var recipesDataDao: RecipeDataDao

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        recipesRepository = RecipesRepository(recipesApiService, recipesDataDao)
    }

    @Test
    fun `fetchRecipes should get the recipes successfully from api service when everything is ok`() {

        //Arrange
        `when`(recipesApiService.getRecipes()).thenReturn(
            Single.just(expectedRecipesData.recipes)
        )

        //Act
        val observer = recipesRepository.fetchRecipes().test()

        //Assert
        observer.assertValue(expectedRecipesData.recipes)
    }

    @Test
    fun `fetchRecipes should receive error from api service when error occurs`() {

        //Arrange
        `when`(recipesApiService.getRecipes()).thenReturn(
            Single.error(expectedRecipesData.error)
        )

        //Act
        val observer = recipesRepository.fetchRecipes().test()

        //Assert
        observer.assertError(expectedRecipesData.error)
    }

    @Test
    fun `addRecipeToFavorites should add recipe to favorites successfully`() = runBlocking{
        //Arrange
        val favoriteRecipe = FavoriteRecipeEntity(1)

        //Act
        recipesRepository.addRecipeToFavorites(favoriteRecipe)

        //Assert
        verify(recipesDataDao).addRecipeToFavorites(favoriteRecipe)
    }

    @Test
    fun `addRecipeToFavorites should throw an exception when there is a database error`(): Unit = runBlocking {
        //Arrange
        //Act
        val favoriteRecipe = FavoriteRecipeEntity(1)
        `when`(recipesDataDao.addRecipeToFavorites(favoriteRecipe)).thenThrow(SQLException())

        //Assert
        Assert.assertThrows(SQLException::class.java){
            runBlocking {
                recipesRepository.addRecipeToFavorites(favoriteRecipe)
            }
        }
    }

    @Test
    fun `removeRecipeFromFavorites should remove recipe from favorites successfully`() = runBlocking{
        //Arrange
        val favoriteRecipe = FavoriteRecipeEntity(1)

        //Act
        recipesRepository.removeRecipeFromFavorites(favoriteRecipe)

        //Assert
        verify(recipesDataDao).removeRecipeFromFavorites(favoriteRecipe)
    }


    @Test
    fun `removeRecipeFromFavorites should throw an exception when there is a database error`(): Unit = runBlocking {
        //Arrange
        //Act
        val favoriteRecipe = FavoriteRecipeEntity(1)
        `when`(recipesDataDao.removeRecipeFromFavorites(favoriteRecipe)).thenThrow(SQLException())

        //Assert
        Assert.assertThrows(SQLException::class.java){
            runBlocking {
                recipesRepository.removeRecipeFromFavorites(favoriteRecipe)
            }
        }
    }

    @Test
    fun `getFavoriteRecipes should get favorite recipes successfully`() = runBlocking{
        //Arrange
        val expectedRecipes = expectedRecipesData.recipes
        val expectedFavRecipes = mutableListOf(FavoriteRecipeEntity(expectedRecipes[0].id))
        //Arrange
        `when`(recipesDataDao.getFavoriteRecipes()).thenReturn(expectedFavRecipes)

        //Act
        val favoriteRecipes = recipesRepository.getFavoriteRecipes()

        //Assert
        Assert.assertEquals(expectedFavRecipes, favoriteRecipes)
    }

    @Test
    fun `getFavoriteRecipes should return an empty list when there are no favorite recipes`() = runBlocking {
        //Arrange
        `when`(recipesDataDao.getFavoriteRecipes()).thenReturn(mutableListOf())

        //Act
        val favoriteRecipes = recipesRepository.getFavoriteRecipes()

        //Assert
        Assert.assertTrue(favoriteRecipes.isEmpty())
    }

    @Test
    fun `getFavoriteRecipes should throw an exception when there is a database error`(): Unit = runBlocking {
        //Arrange
        //Act
        `when`(recipesDataDao.getFavoriteRecipes()).thenThrow(SQLException())

        //Assert
        Assert.assertThrows(SQLException::class.java){
            runBlocking {
                recipesRepository.getFavoriteRecipes()
            }
        }
    }
}