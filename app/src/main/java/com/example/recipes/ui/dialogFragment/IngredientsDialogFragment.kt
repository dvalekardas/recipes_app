package com.example.recipes.ui.dialogFragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.recipes.data.viewmodel.AddRecipeViewModel
import com.example.recipes.databinding.AddIngredientsDialogBinding

class IngredientsDialogFragment: DialogFragment() {
    private lateinit var binding: AddIngredientsDialogBinding
    private val addRecipeViewModel: AddRecipeViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = AddIngredientsDialogBinding.inflate(requireActivity().layoutInflater)
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle("Type the ingredients and separate them with \".\"")
            .setPositiveButton("SAVE") { _, _ ->
                val ingredientsString = binding.ingredients.text.toString()
                val ingredientsList = ingredientsString.split(".").map { it.trim()}.toMutableList()
                ingredientsList.removeAll(listOf(""))
                if(ingredientsList.isNotEmpty()){
                    addRecipeViewModel.addIngredients(ingredientsList)
                }
            }
            .create()
    }

    companion object {
        fun newInstance() = IngredientsDialogFragment()
        const val TAG = "IngredientsDialog"
    }

}