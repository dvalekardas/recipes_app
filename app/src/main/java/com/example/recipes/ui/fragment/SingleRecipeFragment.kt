package com.example.recipes.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipes.R
import com.example.recipes.data.local.FavoriteRecipeEntity
import com.example.recipes.data.model.Recipe

import com.example.recipes.data.viewmodel.RecipesViewModel
import com.example.recipes.databinding.FragmentSingleRecipeBinding
import com.example.recipes.ui.activity.RecipesActivity
import com.example.recipes.ui.adapter.IngredientsAdapter
import com.example.recipes.ui.adapter.InstructionsAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates


class SingleRecipeFragment : Fragment() {
    private lateinit var binding: FragmentSingleRecipeBinding
    private var recipeId by Delegates.notNull<Int>()
    private val viewModel: RecipesViewModel by activityViewModels()
    private lateinit var recipe : Recipe
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipeId = it.getInt("recipeId")
        }
        recipe = viewModel.recipes.value?.single { it.id == recipeId }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSingleRecipeBinding.inflate(layoutInflater)
        setToolBar()
        handleFAB()
        handleAppBar()
        handleArrowBackButton()
        setIngredientsAdapter(recipe.ingredients)
        setStepsToCookAdapter(recipe.instructions)
        handleExpandCollapseIcons()
        return binding.root
    }

    private fun setIngredientsAdapter(ingredients: List<String>) {
        val ingredientsAdapter = IngredientsAdapter(ingredients as ArrayList<String>)
        binding.ingredientsRecyclerView.adapter =  ingredientsAdapter
        binding.ingredientsRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        binding.ingredientsRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )
    }

    private fun setStepsToCookAdapter(instructions: List<String>) {
        val ingredientsAdapter = InstructionsAdapter(instructions as ArrayList<String>)
        binding.stepsRecyclerView.adapter =  ingredientsAdapter
        binding.stepsRecyclerView.layoutManager= LinearLayoutManager(requireContext())
    }

    private fun setToolBar(){
        val toolBar: Toolbar = binding.toolbar
        Picasso.get().load(recipe.imgUrl).into(binding.recipeImage)
        (activity as RecipesActivity).setSupportActionBar(toolBar)
        toolBar.title = recipe.name
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            toolBar.tooltipText =  recipe.name
        }
    }

    private fun handleFAB(){
        val fab: FloatingActionButton = binding.fab
        viewModel.favoriteRecipes.observe(viewLifecycleOwner){

            if(it.contains(FavoriteRecipeEntity(recipe.id))){
                fab.setImageResource(R.drawable.ic_favorite)
            }else{
                fab.setImageResource(R.drawable.ic_not_favorite)
            }
        }
        fab.setOnClickListener {
            if(viewModel.favoriteRecipes.value?.contains(FavoriteRecipeEntity(recipe.id)) == false){
                viewModel.addRecipeToFavorites(recipe)
            }else{
                viewModel.removeRecipeFromFavorites(recipe)
            }
        }
    }

    private fun handleAppBar(){
        val appBar = binding.appBar
        appBar.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true
                } else if (isShow) {
                    isShow = false
                }
            }
        })
    }

    private fun handleExpandCollapseIcons(){
        binding.ingredientsLinearLayout.setOnClickListener {
            if(binding.collapsedIngredients.visibility == View.GONE){
                binding.collapsedIngredients.visibility = View.VISIBLE
                binding.expandedIngredients.visibility = View.GONE
                binding.ingredientsRecyclerView.visibility = View.GONE
            }else{
                binding.collapsedIngredients.visibility = View.GONE
                binding.expandedIngredients.visibility = View.VISIBLE
                binding.ingredientsRecyclerView.visibility = View.VISIBLE
            }
        }

        binding.stepsLinearLayout.setOnClickListener {
            if(binding.collapsedSteps.visibility == View.GONE){
                binding.collapsedSteps.visibility = View.VISIBLE
                binding.expandedSteps.visibility = View.GONE
                binding.stepsRecyclerView.visibility = View.GONE
            }else{
                binding.collapsedSteps.visibility = View.GONE
                binding.expandedSteps.visibility = View.VISIBLE
                binding.stepsRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun handleArrowBackButton() {
        binding.toolbar.setNavigationOnClickListener{
           (activity as RecipesActivity).onBackPressedDispatcher.onBackPressed()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(recipeId: Int) =
            SingleRecipeFragment().apply {
                arguments = Bundle().apply {
                    putInt("recipeId", recipeId)
                }
            }
    }
}