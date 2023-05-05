package com.example.recipes.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.databinding.InstructionItemBinding
import java.util.*
import kotlin.collections.ArrayList

class InstructionsAdapter(private val instructions: ArrayList<String>): RecyclerView.Adapter<InstructionsAdapter.InstructionsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructionsViewHolder {
        val binding = InstructionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InstructionsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InstructionsViewHolder, position: Int) {
        val instruction = instructions[position]
        holder.bind(instruction)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount() = instructions.size

    inner class InstructionsViewHolder(private val binding: InstructionItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(instruction: String){
            binding.instruction.text  =
                instruction.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
        }
    }
}