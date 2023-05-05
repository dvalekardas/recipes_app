package com.example.recipes.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.R
import com.example.recipes.data.model.FavoriteRecipeEntity
import com.example.recipes.data.model.Recipe
import com.example.recipes.databinding.RecipeItemBinding
import com.example.recipes.utils.UrlConstants
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class RecipesAdapter(private var recipes: ArrayList<Recipe>): RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>(){
    private var onRecipeClickListener: OnRecipeClickListener? = null
    private val initialRecipes = ArrayList(recipes)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        val binding = RecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
        holder.itemView.setOnClickListener {
            if(onRecipeClickListener!=null){
                onRecipeClickListener!!.onRecipeClick(recipe)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = recipes.size

    interface OnRecipeClickListener {
        fun onRecipeClick(recipe:Recipe)
    }

    fun setOnRecipeClickListener(onRecipeClickListener:OnRecipeClickListener){
        this.onRecipeClickListener = onRecipeClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterRecipes(context: Context, query:String, onlyFavoriteRecipes: Boolean, favoriteRecipes: List<FavoriteRecipeEntity>){
        var filteredRecipes = ArrayList(initialRecipes)
        if(onlyFavoriteRecipes){
            filteredRecipes = ArrayList(filteredRecipes.filter { recipe-> favoriteRecipes.any{
                favoriteRecipe-> favoriteRecipe.id == recipe.id
            }})
        }
        if(query!=""){
            filteredRecipes = ArrayList(filteredRecipes.filter { it.name.lowercase(Locale.ROOT).contains(query) })
        }
        recipes = ArrayList(filteredRecipes)
        if(recipes.isEmpty()) {
            val noRecipeFound = listOf(Recipe(-1, context.getString(R.string.no_recipe),
                UrlConstants.DEFAULT_IMG, listOf(""), listOf("")) )
            recipes = ArrayList(noRecipeFound)
        }
        notifyDataSetChanged()
    }


    inner class RecipesViewHolder(private val binding: RecipeItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(recipe: Recipe){
            val recipeName = recipe.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            binding.recipeName.text = recipeName
            try{
                if(recipe.imgUrl.startsWith("http")){
                    Picasso.get().load(recipe.imgUrl).into(binding.recipeImageThumbnail)
                }else{
                    Picasso.get().load(recipe.imgUrl.toUri()).into(binding.recipeImageThumbnail)
                }
            }catch (e: Exception){
                Picasso.get().load(UrlConstants.DEFAULT_IMG).into(binding.recipeImageThumbnail)
            }

        }
    }
}