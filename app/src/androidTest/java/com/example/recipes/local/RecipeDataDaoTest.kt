package com.example.recipes.local

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.recipes.data.local.FavoriteRecipeEntity
import com.example.recipes.data.local.RecipeDataDao
import com.example.recipes.data.local.RecipeDatabase
import com.example.recipes.mockData.expectedRecipesData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RecipeDataDaoTest {

    private lateinit var recipesDatabase: RecipeDatabase
    private lateinit var recipeDataDao: RecipeDataDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        recipesDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RecipeDatabase::class.java
        ).allowMainThreadQueries().build()
        recipeDataDao = recipesDatabase.RecipeDataDao()
    }

    @After
    fun teardown() {
        recipesDatabase.close()
    }

    @Test
    fun addRecipeToFavorites_should_add_recipes_to_db_successfully() = runBlocking {
        //Arrange
        val recipe = expectedRecipesData.recipes[0]
        val favoriteRecipe = FavoriteRecipeEntity(recipe.id)

        //Act
        recipeDataDao.addRecipeToFavorites(favoriteRecipe)
        val favoriteRecipes = recipeDataDao.getFavoriteRecipes()

        //Assert
        Assert.assertTrue(favoriteRecipes.contains(favoriteRecipe))
    }

    @Test
    fun addRecipeToFavorites_should_add_recipe_only_once_when_already_in_favorites() = runBlocking {
        //Arrange
        val recipe = expectedRecipesData.recipes[0]
        val favoriteRecipe = FavoriteRecipeEntity(recipe.id)

        //Act
        recipeDataDao.addRecipeToFavorites(favoriteRecipe)

        //Assert
        Assert.assertThrows(SQLiteConstraintException::class.java){
            runBlocking {
                //Act
                recipeDataDao.addRecipeToFavorites(favoriteRecipe)
            }
        }
        val favoriteRecipes = recipeDataDao.getFavoriteRecipes()

        //Assert
        Assert.assertTrue(favoriteRecipes.contains(favoriteRecipe))
        Assert.assertTrue(favoriteRecipes.size == 1)
    }

    @Test
    fun removeRecipeFromFavorites_should_remove_recipe_successfully() = runBlocking {
        //Arrange
        val recipe = expectedRecipesData.recipes[0]
        val favoriteRecipe = FavoriteRecipeEntity(recipe.id)

        //Act
        recipeDataDao.addRecipeToFavorites(favoriteRecipe)
        recipeDataDao.removeRecipeFromFavorites(favoriteRecipe)
        val favoriteRecipes = recipeDataDao.getFavoriteRecipes()

        //Assert
        Assert.assertTrue(!favoriteRecipes.contains(favoriteRecipe))
    }

    @Test
    fun removeRecipeFromFavorites_should_not_remove_recipe_when_recipe_does_not_exist() = runBlocking {
        //Arrange
        val recipe = expectedRecipesData.recipes[0]
        val favoriteRecipe = FavoriteRecipeEntity(recipe.id)

        //Act
        recipeDataDao.removeRecipeFromFavorites(favoriteRecipe)
        val favoriteRecipes = recipeDataDao.getFavoriteRecipes()

        //Assert
        Assert.assertTrue(!favoriteRecipes.contains(favoriteRecipe))
    }

    @Test
    fun getFavoriteRecipes_should_get_recipes_successfully_when_recipes_exist() = runBlocking {
        //Arrange
        val recipe1 = expectedRecipesData.recipes[0]
        val recipe2 = expectedRecipesData.recipes[1]
        val favoriteRecipe1 = FavoriteRecipeEntity(recipe1.id)
        val favoriteRecipe2 = FavoriteRecipeEntity(recipe2.id)

        //Act
        recipeDataDao.addRecipeToFavorites(favoriteRecipe1)
        recipeDataDao.addRecipeToFavorites(favoriteRecipe2)

        val favoriteRecipes = recipeDataDao.getFavoriteRecipes()

        //Assert
        Assert.assertTrue(favoriteRecipes.contains(favoriteRecipe1))
        Assert.assertTrue(favoriteRecipes.contains(favoriteRecipe2))
    }

    @Test
    fun getFavoriteRecipes_should_get_empty_favorite_recipes_when_no_favorite_recipe() = runBlocking {
        //Arrange
        val recipe1 = expectedRecipesData.recipes[0]
        val favoriteRecipe1 = FavoriteRecipeEntity(recipe1.id)

        //Act
        val favoriteRecipes = recipeDataDao.getFavoriteRecipes()

        //Assert
        Assert.assertFalse(favoriteRecipes.contains(favoriteRecipe1))
        Assert.assertTrue(favoriteRecipes.isEmpty())
    }
}