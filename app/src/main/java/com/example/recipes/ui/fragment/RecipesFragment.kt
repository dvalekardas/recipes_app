package com.example.recipes.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipes.data.model.Recipe
import com.example.recipes.data.viewmodel.RecipesViewModel
import com.example.recipes.databinding.FragmentRecipesBinding
import com.example.recipes.ui.activity.RecipesActivity
import com.example.recipes.ui.adapter.RecipesAdapter
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RecipesFragment : Fragment() {
    private lateinit var binding: FragmentRecipesBinding
    private lateinit var recipesAdapter: RecipesAdapter
    companion object {
        fun newInstance() = RecipesFragment()
    }

    private val  viewModel: RecipesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipesBinding.inflate(layoutInflater)

        binding.fab.setOnClickListener {
            (activity as RecipesActivity).openAddRecipe()
        }
        handleRefreshLayout()
        viewModel.recipes.observe(viewLifecycleOwner){
                if(it.isNotEmpty()){
                    viewModel.getFavoriteRecipes()
                    setRecipesAdapter(it)
                    setUIElementsWhenRecipesNotEmpty()
                    handleSearchView()
                    handleToggleButton()
                }else if((viewModel.error.value == false) || it.isEmpty()){
                    if(viewModel.createRecipesFragment.value == true){
                        viewModel.fetchRecipes()
                        viewModel.addSavedRecipesFromDB()
                        binding.refresh.isRefreshing = false
                    }
                }
        }

        viewModel.error.observe(viewLifecycleOwner){
            if(it && viewModel.recipes.value!!.isEmpty()){
                setUIElementsWhenErrorOccurs()
            }else{
                binding.error.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner){
            if(it && viewModel.recipes.value!!.isEmpty()){
                setUIElementsWhenLoading()
            }else{
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner){
            if(viewModel.createRecipesFragment.value == true || viewModel.recipeAdded.value == true) {
                Toast.makeText(context, getString(it), Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    private fun setUIElementsWhenLoading() {
        binding.error.visibility = View.GONE
        binding.recipesRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.searchView.visibility = View.GONE
        binding.tabsView.visibility = View.GONE
        binding.fab.visibility = View.GONE
    }

    private fun setUIElementsWhenErrorOccurs() {
        binding.refresh.isRefreshing = false
        binding.error.visibility = View.VISIBLE
        binding.recipesRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.searchView.visibility = View.GONE
        binding.tabsView.visibility = View.GONE
        binding.fab.visibility = View.VISIBLE
    }

    private fun setUIElementsWhenRecipesNotEmpty() {
        binding.refresh.isRefreshing = false
        binding.recipesRecyclerView.visibility = View.VISIBLE
        binding.error.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.searchView.visibility = View.VISIBLE
        binding.tabsView.visibility = View.VISIBLE
        binding.fab.visibility = View.VISIBLE
    }

    private fun setRecipesAdapter(recipeList:List<Recipe>){
        recipesAdapter = RecipesAdapter(recipeList as ArrayList<Recipe>)
        binding.recipesRecyclerView.adapter =  recipesAdapter
        binding.recipesRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        handleRecipeClick()
    }

    private fun handleRecipeClick(){
        recipesAdapter.setOnRecipeClickListener(object : RecipesAdapter.OnRecipeClickListener{
            override fun onRecipeClick(recipe: Recipe) {
                (activity as RecipesActivity).openRecipe(recipe)
            }
        })
    }

    private fun handleRefreshLayout() {
        binding.refresh.isEnabled = true
        binding.refresh.setOnRefreshListener {
            viewModel.initialize()
            binding.refresh.isRefreshing = false
        }
    }

    private fun handleSearchView(){
        binding.searchView.query.toString().let { viewModel.onlyFavoriteRecipes.value?.let { onlyFavRecipes ->
            viewModel.favoriteRecipes.value?.let { favoriteRecipes ->
                context?.let { context ->
                    recipesAdapter.filterRecipes(
                        context,
                        binding.searchView.query.toString(),
                        onlyFavRecipes,
                        favoriteRecipes
                    )
                }
            }
        } }
        binding.searchView.setOnClickListener{
            binding.searchView.isIconified = false
        }

        binding.searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.onlyFavoriteRecipes.value?.let { onlyFavRecipes ->
                    viewModel.favoriteRecipes.value?.let { favoriteRecipes ->
                        context?.let { context ->
                            recipesAdapter.filterRecipes(
                                context,
                                query,
                                onlyFavRecipes,
                                favoriteRecipes
                            )
                        }
                    }
                } }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.let{ viewModel.onlyFavoriteRecipes.value?.let { onlyFavRecipes ->
                    viewModel.favoriteRecipes.value?.let { favoriteRecipes ->
                        context?.let { context ->
                            recipesAdapter.filterRecipes(
                                context,
                                query,
                                onlyFavRecipes,
                                favoriteRecipes
                            )
                        }
                    }
                } }
                return true
            }
        })
    }

    private fun handleToggleButton(){
        if(viewModel.onlyFavoriteRecipes.value == true){
            showFavoriteRecipes()
        }
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    if(tab.text =="All"){
                        binding.fab.visibility = View.VISIBLE
                        viewModel.changeFavoriteOnlyState()
                        viewModel.favoriteRecipes.value?.
                        let { context?.let { context -> recipesAdapter.filterRecipes(context, binding.searchView.query.toString(), false, it) } }
                    }else{
                        binding.fab.visibility = View.GONE
                        viewModel.changeFavoriteOnlyState()
                        viewModel.favoriteRecipes.value?.
                        let { context?.let { context -> recipesAdapter.filterRecipes(context, binding.searchView.query.toString(), true, it) } }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun showFavoriteRecipes(){
        binding.tabs.selectTab(binding.tabs.getTabAt(1))
    }
}