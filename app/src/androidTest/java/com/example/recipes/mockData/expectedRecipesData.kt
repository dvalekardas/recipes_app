package com.example.recipes.mockData

import com.example.recipes.data.model.Recipe

object expectedRecipesData {
    val recipes = listOf(
        Recipe(
            1,
            "Spaghetti",
            "http://example.com",
            listOf("Boil the spaghetti for 8 min"),
            listOf("spaghetti no 7")
        ),
        Recipe(
            2,
            "Pizza",
            "http://example.com",
            listOf("Bake for 30min"),
            listOf("Dough")
        )
    )
    val error = Throwable()
}