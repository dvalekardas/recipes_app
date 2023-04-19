package com.example.recipes.remote

import com.example.recipes.mockData.expectedRecipesData
import com.example.recipes.data.remote.RecipesApi
import com.example.recipes.data.remote.RecipesApiService
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RecipesApiServiceTest {
    private lateinit var recipesApiService: RecipesApiService

    @Mock
    private lateinit var recipesApi: RecipesApi

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        recipesApiService = RecipesApiService(recipesApi)
    }

    @Test
    fun `getRecipes should get recipes from api successfully when everything is ok`() {

        //Arrange
        `when`(recipesApi.getRecipes())
            .thenReturn(Single.just(expectedRecipesData.recipes))

        //Act
        val observer = recipesApi.getRecipes().test()

        // Assert
        observer.assertValue(expectedRecipesData.recipes)
    }

    @Test
    fun `getRecipes should receive error from api when error occurs`() {

        //Arrange
        `when`(recipesApi.getRecipes())
            .thenReturn(Single.error(expectedRecipesData.error))

        //Act
        val observer = recipesApi.getRecipes().test()

        // Assert
        observer.assertError(Throwable::class.java)
    }
}