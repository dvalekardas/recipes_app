package com.example.recipes.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recipes.R
import com.example.recipes.data.model.Recipe
import com.example.recipes.ui.fragment.AddRecipeFragment
import com.example.recipes.ui.fragment.RecipesFragment
import com.example.recipes.ui.fragment.SingleRecipeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.recipes_container, RecipesFragment.newInstance())
                .commitNow()
        }
    }

    fun openRecipe(recipe: Recipe) {
        val invalidId = -1
        if(recipe.id != invalidId){
            val singleRecipeFragment = SingleRecipeFragment.newInstance(recipe.id)
            supportFragmentManager.beginTransaction()
                .replace(R.id.recipes_container, singleRecipeFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    fun openAddRecipe(){
        val addRecipeFragment = AddRecipeFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.recipes_container, addRecipeFragment)
            .addToBackStack(null)
            .commit()
    }
}