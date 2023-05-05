package com.example.recipes.ui.dialogFragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.recipes.data.viewmodel.AddRecipeViewModel
import com.example.recipes.databinding.AddInstructionsDialogBinding

class InstructionsDialogFragment: DialogFragment() {
    private lateinit var binding: AddInstructionsDialogBinding
    private val addRecipeViewModel: AddRecipeViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = AddInstructionsDialogBinding.inflate(requireActivity().layoutInflater)
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle("Type the instructions and separate them with  \".\"")
            .setPositiveButton("SAVE") { _, _ ->
                val instructionsString = binding.instructions.text.toString()
                val instructionsList = instructionsString.split(".").map { it.trim()}.toMutableList()
                instructionsList.removeAll(listOf(""))
                if(instructionsList.isNotEmpty()){
                    addRecipeViewModel.addInstructions(instructionsList)
                }
            }
            .create()
    }

    companion object {
        fun newInstance() = InstructionsDialogFragment()
        const val TAG = "InstructionsDialog"
    }

}