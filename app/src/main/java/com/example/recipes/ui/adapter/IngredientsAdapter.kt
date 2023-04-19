package com.example.recipes.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.databinding.IngredientItemBinding
import java.util.*
import kotlin.collections.ArrayList

class IngredientsAdapter(private val ingredients: ArrayList<String>): RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        val binding = IngredientItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.bind(ingredient)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = ingredients.size

    inner class IngredientsViewHolder(private val binding: IngredientItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(ingredient: String){
            binding.ingredient.text =
                ingredient.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
        }
    }
}