package com.example.recipes.ui.fragment

import android.os.Bundle
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
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
    private lateinit var gestureDetector: GestureDetector
    companion object {
        fun newInstance() = RecipesFragment()
    }

    private val  viewModel: RecipesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipesBinding.inflate(layoutInflater)

        viewModel.recipes.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                viewModel.getFavoriteRecipes()
                setRecipesAdapter(it)
                setUIElementsWhenRecipesNotEmpty()
                handleSearchView()
                handleToggleButton()
               // handleGestures()
            }else if(viewModel.error.value == false){
                viewModel.fetchRecipes()
                binding.refresh.isRefreshing = false
                setSwipeRefreshLayout(it)
            }
        }

        viewModel.error.observe(viewLifecycleOwner){
            if(it){
                setUIElementsWhenErrorOccurs()
            }else{
                binding.error.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner){
            if(it){
                setUIElementsWhenLoading()
            }else{
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner){
            Toast.makeText(context, getString(it), Toast.LENGTH_SHORT).show()
        }
//        val view = binding.root
//        view.setOnTouchListener(object : View.OnTouchListener {
//            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
//                return gestureDetector.onTouchEvent(motionEvent!!)
//            }
//        })
        return binding.root
    }

    private fun setUIElementsWhenLoading() {
        binding.error.visibility = View.GONE
        binding.recipesRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.searchView.visibility = View.GONE
        binding.tabsView.visibility = View.GONE
    }

    private fun setUIElementsWhenErrorOccurs() {
        binding.refresh.isRefreshing = false
        binding.error.visibility = View.VISIBLE
        binding.recipesRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.searchView.visibility = View.GONE
        binding.tabsView.visibility = View.GONE
    }

    private fun setUIElementsWhenRecipesNotEmpty() {
        binding.refresh.isRefreshing = false
        binding.refresh.isEnabled = false
        binding.recipesRecyclerView.visibility = View.VISIBLE
        binding.error.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.searchView.visibility = View.VISIBLE
        binding.tabsView.visibility = View.VISIBLE
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

    private fun setSwipeRefreshLayout(recipeList:List<Recipe>) {
        binding.refresh.setOnRefreshListener {
            if(recipeList.isEmpty()){
                binding.refresh.isEnabled = true
                viewModel.fetchRecipes()
            }else{
                binding.refresh.isEnabled = false
                binding.refresh.isRefreshing = false
            }
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
                        viewModel.changeFavoriteOnlyState()
                        viewModel.favoriteRecipes.value?.
                        let { context?.let { context -> recipesAdapter.filterRecipes(context, binding.searchView.query.toString(), false, it) } }
                    }else{
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
    
    private fun handleGestures(){
        gestureDetector = GestureDetector((activity as RecipesActivity), object : SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1.x < e2.x) {
                    // Swiped right
                    showAllRecipes()
                } else if (e1.x > e2.x) {
                    // Swiped left
                    showFavoriteRecipes()
                }
                return true
            }
        })
    }

    private fun showAllRecipes(){
        binding.tabs.selectTab(binding.tabs.getTabAt(0))
//        viewModel.favoriteRecipes.value?.
//        let { recipesAdapter.filterRecipes(binding.searchView.query.toString(), false, it) }
    }

    private fun showFavoriteRecipes(){
        binding.tabs.selectTab(binding.tabs.getTabAt(1))
//        viewModel.favoriteRecipes.value?.
//        let { recipesAdapter.filterRecipes(binding.searchView.query.toString(), true, it) }
    }
}