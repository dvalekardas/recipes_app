package com.example.recipes.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipes.R
import com.example.recipes.data.model.Recipe
import com.example.recipes.data.viewmodel.AddRecipeViewModel
import com.example.recipes.data.viewmodel.RecipesViewModel
import com.example.recipes.databinding.FragmentAddRecipeBinding
import com.example.recipes.ui.activity.RecipesActivity
import com.example.recipes.ui.adapter.AddIngredientsAdapter
import com.example.recipes.ui.adapter.AddInstructionsAdapter
import com.example.recipes.ui.dialogFragment.IngredientsDialogFragment
import com.example.recipes.ui.dialogFragment.InstructionsDialogFragment
import com.example.recipes.utils.CameraUtils
import com.example.recipes.utils.UrlConstants.DEFAULT_IMG
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddRecipeFragment: Fragment() {

    private lateinit var binding: FragmentAddRecipeBinding

    private val recipesViewModel: RecipesViewModel by activityViewModels()
    private val addRecipeViewModel: AddRecipeViewModel by viewModels()

    private lateinit var instructionsAdapter: AddInstructionsAdapter
    private lateinit var ingredientsAdapter: AddIngredientsAdapter

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageFilesLauncher: ActivityResultLauncher<Intent>

    private var photoFile: File? = null

    companion object {
        fun newInstance() = AddRecipeFragment()
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddRecipeBinding.inflate(layoutInflater)
        handleCancelButton()
        handleDoneButton()
        handleAddRecipePhoto()
        handlePickPhotoFromFiles()
        handleAddIngredientsButton()
        handleAddInstructionsButton()
        handleRemovePhotoButton()
        observeRecyclerViews()
        observeRecipePhoto()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                addRecipeViewModel.setPhotoPath(photoFile?.toUri().toString())
                showImageAndHideAddImageButton()
            }else{
                photoFile = try{
                    photoFile?.delete()
                    null
                }catch (e: SecurityException){
                    null
                }
            }
        }

        imageFilesLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if(result.data?.data !=null){
                    val contentResolver = requireContext().contentResolver

                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                     contentResolver.takePersistableUriPermission(result.data!!.data!!, takeFlags)
                    addRecipeViewModel.setPhotoPath(result.data!!.data.toString())
                    showImageAndHideAddImageButton()
                }
            }
        }
        super.onCreate(savedInstanceState)
    }

    private fun observeRecyclerViews() {
        addRecipeViewModel.ingredients.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                binding.ingredientsRecyclerView.visibility = View.VISIBLE
                setIngredientsAdapter()
            }
        }

        addRecipeViewModel.instructions.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                binding.instructionsRecyclerView.visibility = View.VISIBLE
                setInstructionsAdapter()
            }
        }
    }

    private fun observeRecipePhoto(){
        addRecipeViewModel.recipePhotoPath.observe(viewLifecycleOwner){
            if(it!=DEFAULT_IMG){
                showImageAndHideAddImageButton()
            }
        }
    }

    private fun handleCancelButton() {
        binding.cancel.setOnClickListener {
            recipesViewModel.preventRecipeFragmentRecreation()
            recipesViewModel.preventRecipeToast()
            if(recipesViewModel.recipes.value!!.isEmpty()){
                recipesViewModel.setErrorValue()
            }
            (activity as RecipesActivity).onBackPressedDispatcher.onBackPressed()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun handleDoneButton() {
       binding.done.setOnClickListener {
           if(binding.editRecipeTitle.text.isEmpty() || binding.editRecipeTitle.text.isBlank()){
               Toast.makeText(requireContext(), R.string.mandatory_recipe, Toast.LENGTH_SHORT).show()
           }else{
               val timeStamp: Long = System.currentTimeMillis()/1000  //seconds
               val newRecipe = Recipe(
                   timeStamp.toInt(),
                   binding.editRecipeTitle.text.toString(),
                   addRecipeViewModel.recipePhotoPath.value.toString(),
                   addRecipeViewModel.instructions.value?.toList(),
                   addRecipeViewModel.ingredients.value?.toList(),
               )
               if(recipesViewModel.recipes.value!!.isNotEmpty()){
                   recipesViewModel.preventRecipeFragmentRecreation()
               }else{
                   recipesViewModel.allowRecipeFragmentRecreation()
               }
               recipesViewModel.addRecipe(newRecipe)
               (activity as RecipesActivity).onBackPressedDispatcher.onBackPressed()
           }

       }
    }

    private fun handleAddRecipePhoto() {
        binding.addRecipePhoto.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (CameraUtils.areCameraAndMediaImagePermissionsGranted(requireContext())) {
                   photoFile = CameraUtils.openCamera(requireContext(), cameraLauncher)
                }else{
                    CameraUtils.askForCameraAndMediaImagesPermissions(
                        requireActivity(),
                        REQUEST_CODE_PERMISSIONS
                    )
                }
            }else{
                if (CameraUtils.areCameraAndExternalStoragePermissionsGranted(requireContext())) {
                    photoFile = CameraUtils.openCamera(requireContext(), cameraLauncher)
                }else{
                    CameraUtils.askForCameraAndExternalStoragePermissions(
                        requireActivity(),
                        REQUEST_CODE_PERMISSIONS
                    )
                }
            }
        }
    }

    private fun handlePickPhotoFromFiles(){
        binding.pickPhotoFromFiles.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (CameraUtils.areMediaImagePermissionsGranted(requireContext())) {
                    CameraUtils.openImageFiles(imageFilesLauncher)
                }else{
                    CameraUtils.askForMediaImagesPermissions(
                        requireActivity(),
                        REQUEST_CODE_PERMISSIONS
                    )
                }
            }else{
                if (CameraUtils.areExternalStoragePermissionsGranted(requireContext())) {
                    CameraUtils.openImageFiles(imageFilesLauncher)
                }else{
                    CameraUtils.askForExternalStoragePermissions(
                        requireActivity(),
                        REQUEST_CODE_PERMISSIONS
                    )
                }
            }
        }
    }

    private fun handleRemovePhotoButton() {
       binding.imageClose.setOnClickListener {
           showAddImageButtonAndRemoveImage()
       }
    }

    private fun handleAddIngredientsButton() {
        binding.addIngredients.setOnClickListener {
            showIngredientsDialog()
        }
    }

    private fun showIngredientsDialog(){
        IngredientsDialogFragment.newInstance().show(childFragmentManager, IngredientsDialogFragment.TAG)
    }

    private fun handleAddInstructionsButton() {
        binding.addInstructions.setOnClickListener {
            showInstructionsDialog()
        }
    }

    private fun showInstructionsDialog(){
        InstructionsDialogFragment.newInstance().show(childFragmentManager, InstructionsDialogFragment.TAG)
    }

    private fun setIngredientsAdapter(){
        val ingredients = addRecipeViewModel.ingredients.value!!
        ingredientsAdapter = AddIngredientsAdapter(ingredients as ArrayList<String>, addRecipeViewModel)
        binding.ingredientsRecyclerView.adapter = ingredientsAdapter
        binding.ingredientsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.ingredientsRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )
    }

    private fun setInstructionsAdapter(){
        val instructions = addRecipeViewModel.instructions.value!!
        instructionsAdapter = AddInstructionsAdapter(instructions as ArrayList<String>, addRecipeViewModel)
        binding.instructionsRecyclerView.adapter = instructionsAdapter
        binding.instructionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.instructionsRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )
    }

    private fun showImageAndHideAddImageButton(){
        binding.recipePhoto.visibility = View.VISIBLE
        binding.imageClose.visibility = View.VISIBLE
        binding.recipePhotoLayout.visibility = View.VISIBLE
        binding.addRecipePhoto.visibility = View.GONE
        binding.pickPhotoFromFiles.visibility = View.GONE
        binding.recipePhotoText.visibility = View.GONE
        Picasso.get().load(addRecipeViewModel.recipePhotoPath.value.toString().toUri()).into(binding.recipePhoto)
    }

    private fun showAddImageButtonAndRemoveImage(){
        binding.recipePhoto.visibility = View.GONE
        binding.imageClose.visibility = View.GONE
        binding.recipePhotoLayout.visibility = View.GONE
        binding.addRecipePhoto.visibility = View.VISIBLE
        binding.pickPhotoFromFiles.visibility = View.VISIBLE
        binding.recipePhotoText.visibility = View.VISIBLE
        Picasso.get().invalidate(photoFile?.toUri())
        try{
            photoFile?.delete()
            photoFile = null
            addRecipeViewModel.removePhotoPath()
        }catch (e: SecurityException){
            photoFile = null
            addRecipeViewModel.removePhotoPath()
        }
    }
}