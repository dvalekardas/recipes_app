package com.example.recipes

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry

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

import org.hamcrest.Matchers.allOf
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith

@UninstallModules(RecipesModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@SmallTest
class RecipesFeatureTest : MatcherExtensions() {

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
    }

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.recipes", appContext.packageName)
    }

    @Test
    fun test_recipes_fragment_is_displayed() {
        onView(withId(R.id.recipes_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun test_display_of_recipes_list_is_shown_after_recipes_load() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.recipes_recycler_view))
            .check(matches(isDisplayed()))
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_display_of_recipes_list_is_not_visible_before_recipes_load() {
        onView(withId(R.id.recipes_recycler_view))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun test_that_second_recipe_list_item_is_correct() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.recipes_recycler_view))
            .perform(RecyclerViewActions.scrollToPosition<RecipesAdapter.RecipesViewHolder>(1))
            .check(matches(atPositionOnView(1, withText("Chocolate Chip Cookies"), R.id.recipe_name)))
            .check(matches(atPositionOnView(1, withId(R.id.recipe_image_thumbnail), R.id.recipe_image_thumbnail)))
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_click_second_recipe_list_item_and_open_recipe() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.recipes_recycler_view))
            .perform(actionOnItemAtPosition<RecipesAdapter.RecipesViewHolder>(1, click()))
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_display_of_progressBar_is_shown_before_recipes_load(){
        onView(withId(R.id.progressBar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_display_of_progressBar_is_gone_after_recipes_load(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.progressBar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_display_error_text_is_visible_and_rest_are_gone_when_fetch_recipes_request_fails(){
        mockWebServer.shutdown()
        mockWebServer = MockWebServer()
        mockWebServer.dispatcher = MockServerDispatcher().ErrorDispatcher()
        mockWebServer.start(3000)
        onView(withId(R.id.error))
            .check(matches(isDisplayed()))
        onView(withId(R.id.progressBar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.searchView))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.tabs))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.recipes_recycler_view))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        mockWebServer.shutdown()
        mockWebServer = MockWebServer()
        mockWebServer.dispatcher = MockServerDispatcher().RequestDispatcher()
        mockWebServer.start(3000)
    }

    @Test
    fun test_display_of_error_text_is_gone_before_during_and_after_recipes_load_successfully(){
        onView(withId(R.id.error))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.error))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.error))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun test_display_of_tabs_is_shown_after_recipes_load(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.tabs))
            .check(matches(isDisplayed()))
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_display_of_tabs_is_not_visible_before_recipes_load(){
        onView(withId(R.id.tabs))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun test_favorite_and_all_tabs_are_selected_when_clicked(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        val tabLayout = onView(withId(R.id.tabs))
        tabLayout.perform(selectTabAtPosition(1))
        onView(allOf(withText(R.string.favorites), isDescendantOfA(withId(R.id.tabs)), isSelected()))
            .check(matches(isDisplayed()))
        tabLayout.perform(selectTabAtPosition(0))
        onView(allOf(withText(R.string.all), isDescendantOfA(withId(R.id.tabs)), isSelected()))
            .check(matches(isDisplayed()))
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_click_favorite_tab_and_expect_recyclerView_to_be_empty_when_no_favorite_exists(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        val tabLayout = onView(withId(R.id.tabs))
        tabLayout.perform(selectTabAtPosition(1))
        onView(allOf(withText(R.string.favorites), isDescendantOfA(withId(R.id.tabs)), isSelected()))

        onView(withId(R.id.recipes_recycler_view))
            .perform(RecyclerViewActions.scrollToPosition<RecipesAdapter.RecipesViewHolder>(0))
            .check(matches(atPositionOnView(0, withText(R.string.no_recipe), R.id.recipe_name)))
            .check(matches(hasChildCount(1)))
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_display_of_searchBar_is_not_visible_before_recipes_load(){
        onView(withId(R.id.searchView))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun test_display_of_searchBar_is_shown_after_recipes_load(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.searchView))
            .check(matches(isDisplayed()))
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_display_of_all_recipes_list_is_properly_filtered_and_equal_to_one_when_chocolate_is_typed(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.searchView))
            .perform(click())
            .perform(searchText("chocolate"))
        onView(withId(R.id.recipes_recycler_view))
            .perform(RecyclerViewActions.scrollToPosition<RecipesAdapter.RecipesViewHolder>(0))
            .check(matches(atPositionOnView(0, withText("Chocolate Chip Cookies"), R.id.recipe_name)))
            .check(matches(hasChildCount(1)))
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_display_of_all_recipes_list_contains_no_recipe_when_unknown_keyword_is_typed(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.searchView))
            .perform(click())
            .perform(searchText("asdfg"))
        onView(withId(R.id.recipes_recycler_view))
            .perform(RecyclerViewActions.scrollToPosition<RecipesAdapter.RecipesViewHolder>(0))
            .check(matches(atPositionOnView(0, withText(R.string.no_recipe), R.id.recipe_name)))
            .check(matches(hasChildCount(1)))
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_display_of_fav_recipes_list_contains_no_recipe_when_chocolate_is_typed_and_no_fav_recipe(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        val tabLayout = onView(withId(R.id.tabs))
        tabLayout.perform(selectTabAtPosition(1))
        onView(withId(R.id.searchView))
            .perform(click())
            .perform(searchText("chocolate"))
        onView(withId(R.id.recipes_recycler_view))
            .perform(RecyclerViewActions.scrollToPosition<RecipesAdapter.RecipesViewHolder>(0))
            .check(matches(atPositionOnView(0, withText(R.string.no_recipe), R.id.recipe_name)))
            .check(matches(hasChildCount(1)))
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_display_of_fav_recipes_list_contains_cookies_when_cookies_are_the_only_favorite() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.recipes_recycler_view))
            .perform(actionOnItemAtPosition<RecipesAdapter.RecipesViewHolder>(1, click()))
        onView(withId(R.id.fab)).perform(click())
        onView(isRoot()).perform(pressBack())
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1))
        onView(withId(R.id.recipes_recycler_view))
            .perform(RecyclerViewActions.scrollToPosition<RecipesAdapter.RecipesViewHolder>(0))
            .check(matches(atPositionOnView(0, withText("Chocolate Chip Cookies"), R.id.recipe_name)))
            .check(matches(hasChildCount(1)))
    }

    @Test
    fun test_display_of_fav_recipes_list_contains_no_recipe_when_cookies_is_favorite_and_search_text_not_in_recipe_name() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.recipes_recycler_view))
            .perform(actionOnItemAtPosition<RecipesAdapter.RecipesViewHolder>(1, click()))
        onView(withId(R.id.fab)).perform(click())
        onView(isRoot()).perform(pressBack())
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1))
        onView(withId(R.id.searchView))
            .perform(click())
            .perform(searchText("asdfg"))
        onView(withId(R.id.recipes_recycler_view))
            .perform(RecyclerViewActions.scrollToPosition<RecipesAdapter.RecipesViewHolder>(0))
            .check(matches(atPositionOnView(0, withText(R.string.no_recipe), R.id.recipe_name)))
            .check(matches(hasChildCount(1)))
    }

    @Test
    fun test_display_of_fav_recipes_list_contains_cookies_when_cookies_is_favorite_and_search_is_chocolate(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.recipes_recycler_view))
            .perform(actionOnItemAtPosition<RecipesAdapter.RecipesViewHolder>(1, click()))
        onView(withId(R.id.fab)).perform(click())
        onView(isRoot()).perform(pressBack())
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1))
        onView(withId(R.id.searchView))
            .perform(click())
            .perform(searchText("chocolate"))
        onView(withId(R.id.recipes_recycler_view))
            .perform(RecyclerViewActions.scrollToPosition<RecipesAdapter.RecipesViewHolder>(0))
            .check(matches(atPositionOnView(0, withText("Chocolate Chip Cookies"), R.id.recipe_name)))
            .check(matches(hasChildCount(1)))
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
}