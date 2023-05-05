package com.example.recipes.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.data.viewmodel.AddRecipeViewModel
import com.example.recipes.databinding.AddInstructionItemBinding
import java.util.*
import kotlin.collections.ArrayList

class AddInstructionsAdapter(
    private val instructions: ArrayList<String>,
    private val addRecipesViewModel: AddRecipeViewModel
    ): RecyclerView.Adapter<AddInstructionsAdapter.InstructionsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddInstructionsAdapter.InstructionsViewHolder {
        val binding = AddInstructionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InstructionsViewHolder(binding)
    }
    override fun onBindViewHolder(holder: InstructionsViewHolder, position: Int) {
      val instruction = instructions[position]
        holder.bind(instruction, position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = instructions.size

    private fun removeInstruction(instruction: String, position: Int){
        instructions.removeAt(position)
        addRecipesViewModel.removeIngredient(instruction)
        notifyItemRemoved(position)
    }

    inner class InstructionsViewHolder(private val binding: AddInstructionItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(instruction: String, position: Int){
            binding.delete.setOnClickListener {
                removeInstruction(binding.instruction.text.toString(), position)
            }

            binding.instruction.text =
                instruction.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
        }
    }
}