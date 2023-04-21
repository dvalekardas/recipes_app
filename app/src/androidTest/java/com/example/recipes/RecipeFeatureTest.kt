package com.example.recipes

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest

import com.example.recipes.modules.RecipesModule
import com.example.recipes.ui.activity.RecipesActivity
import com.example.recipes.ui.adapter.RecipesAdapter
import com.example.recipes.utils.EspressoIdlingResource
import com.example.recipes.utils.MatcherExtensions
import com.example.recipes.utils.MockServerDispatcher

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules

import okhttp3.mockwebserver.MockWebServer

import org.junit.*
import org.junit.runner.RunWith

@UninstallModules(RecipesModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@SmallTest
class RecipeFeatureTest : MatcherExtensions() {

    @get:Rule val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityRule = ActivityScenarioRule(RecipesActivity::class.java)

    companion object{
        private lateinit var mockWebServer: MockWebServer
        @BeforeClass
        @JvmStatic
        fun setupClass(){
            mockWebServer = MockWebServer()
            mockWebServer.dispatcher = MockServerDispatcher().RequestDispatcher()
            mockWebServer.start(3000)
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            mockWebServer.shutdown()
        }
    }

    @Before
    fun setup(){
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.recipes_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecipesAdapter.RecipesViewHolder>(
                    1,
                    click()
                )
            )
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_single_recipe_fragment_is_displayed() {
        onView(withId(R.id.coordinatorLayout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_appbar_layout_is_displayed_correctly(){
        onView(withId(R.id.app_bar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_collapsing_toolbar_layout_is_displayed_correctly(){
        onView(withId(R.id.toolbar_layout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_recipe_image_is_displayed_correctly(){
        onView(withId(R.id.recipe_image))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_ingredients_text_is_displayed_correctly(){
        onView(withId(R.id.ingredients))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_cooking_steps_text_is_displayed_correctly(){
        onView(withId(R.id.steps))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_fab_is_displayed_correctly(){
        onView(withId(R.id.fab))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_ingredients_list_is_displayed_correctly(){
        onView(withId(R.id.ingredients_recycler_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_cooking_steps_list_is_displayed_correctly(){
        onView(withId(R.id.steps_recycler_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_fab_drawable_changes_when_clicked(){
        onView(withId(R.id.fab))
            .perform(click())
            .check(matches(withDrawable(R.drawable.ic_favorite)))
            .perform(click())
            .check(matches(withDrawable(R.drawable.ic_not_favorite)))
    }

    @Test
    fun test_expand_icon_is_replaced_with_collapse_icon_and_vice_versa_ingredients_arrow_icon_is_clicked(){
        onView(withId(R.id.expanded_ingredients))
            .check(matches(isDisplayed()))
            .perform(click())
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.collapsed_ingredients))
            .check(matches(isDisplayed()))
            .perform(click())
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.expanded_ingredients))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_expand_icon_is_replaced_with_collapse_icon_and_vice_versa_steps_arrow_icon_is_clicked(){
        onView(withId(R.id.expanded_steps))
            .check(matches(isDisplayed()))
            .perform(click())
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.collapsed_steps))
            .check(matches(isDisplayed()))
            .perform(click())
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.expanded_steps))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_ingredients_list_is_hidden_and_shown_when_expand_and_collapse_icon_is_clicked(){
        onView(withId(R.id.expanded_ingredients))
            .perform(click())
        onView(withId(R.id.ingredients_recycler_view))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.collapsed_ingredients))
            .perform(click())
        onView(withId(R.id.ingredients_recycler_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_cooking_steps_list_is_hidden_and_shown_when_expand_and_collapse_icon_is_clicked(){
        onView(withId(R.id.expanded_steps))
            .perform(click())
        onView(withId(R.id.steps_recycler_view))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.collapsed_steps))
            .perform(click())
        onView(withId(R.id.steps_recycler_view))
            .check(matches(isDisplayed()))
    }
}