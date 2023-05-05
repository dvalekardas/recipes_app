package com.example.recipes.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.data.viewmodel.AddRecipeViewModel
import com.example.recipes.databinding.AddIngredientItemBinding
import java.util.*
import kotlin.collections.ArrayList

class AddIngredientsAdapter(
    private val ingredients: ArrayList<String>,
    private val addRecipesViewModel: AddRecipeViewModel
    ): RecyclerView.Adapter<AddIngredientsAdapter.IngredientsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddIngredientsAdapter.IngredientsViewHolder {
        val binding = AddIngredientItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.bind(ingredient, position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = ingredients.size

    private fun removeIngredient(ingredient: String, position: Int){
        ingredients.removeAt(position)
        addRecipesViewModel.removeIngredient(ingredient)
        notifyItemRemoved(position)
    }

    inner class IngredientsViewHolder(private val binding: AddIngredientItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(ingredient: String, position: Int){
            binding.delete.setOnClickListener {
                removeIngredient(binding.ingredient.text.toString(), position)
            }

            binding.ingredient.text = ingredient.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }
    }
}