package com.example.recipes.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.*
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.material.tabs.TabLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher


open class MatcherExtensions {
    fun atPositionOnView(
        position: Int, itemMatcher: Matcher<View?>,
        targetViewId: Int
    ): Matcher<View?> {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has view id $itemMatcher at position $position")
            }

            override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                val targetView = viewHolder!!.itemView.findViewById<View>(targetViewId)
                return itemMatcher.matches(targetView)
            }
        }
    }

    fun withDrawable(@DrawableRes id: Int) = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("ImageView with drawable same as drawable with id $id")
        }

        override fun matchesSafely(view: View): Boolean {
            val context = view.context
            val expectedBitmap = context.getDrawable(id)?.toBitmap()
            return view is ImageView && view.drawable.toBitmap().sameAs(expectedBitmap)
        }
    }

    fun withBackground(@DrawableRes expectedResourceId: Int): Matcher<View?> {
        return object : BoundedMatcher<View?, View>(View::class.java) {
            override fun matchesSafely(view: View): Boolean {
                return sameBitmap(view.context, view.background, expectedResourceId)
            }

            override fun describeTo(description: Description) {
                description.appendText("has background resource $expectedResourceId")
            }
        }
    }
    fun clickChildViewWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isDescendantOfA(isAssignableFrom(RecyclerView::class.java))
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController, view: View) {
                val v = view.findViewById<View>(id)
                v.performClick()
            }
        }
    }

    private fun sameBitmap(context: Context, drawable: Drawable, expectedId: Int): Boolean {
        var drawable: Drawable? = drawable
        var expectedDrawable = ContextCompat.getDrawable(context, expectedId)
        if (drawable == null || expectedDrawable == null) {
            return false
        }
        if (drawable is StateListDrawable && expectedDrawable is StateListDrawable) {
            drawable = drawable.getCurrent()
            expectedDrawable = expectedDrawable.getCurrent()
        }
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val otherBitmap = (expectedDrawable as BitmapDrawable).bitmap
            return bitmap.sameAs(otherBitmap)
        }
        if (drawable is VectorDrawable ||
            drawable is VectorDrawableCompat ||
            drawable is GradientDrawable
        ) {
            val drawableRect: Rect = drawable.bounds
            val bitmap = Bitmap.createBitmap(
                drawableRect.width(),
                drawableRect.height(),
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            val otherBitmap = Bitmap.createBitmap(
                drawableRect.width(),
                drawableRect.height(),
                Bitmap.Config.ARGB_8888
            )
            val otherCanvas = Canvas(otherBitmap)
            expectedDrawable.setBounds(0, 0, otherCanvas.width, otherCanvas.height)
            expectedDrawable.draw(otherCanvas)
            return bitmap.sameAs(otherBitmap)
        }

        return false
    }

    fun selectTabAtPosition(tabIndex: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription() = "with tab at index $tabIndex"

            override fun getConstraints() = allOf(isDisplayed(), isAssignableFrom(TabLayout::class.java))

            override fun perform(uiController: UiController, view: View) {
                val tabLayout = view as TabLayout
                val tabAtIndex: TabLayout.Tab = tabLayout.getTabAt(tabIndex)
                    ?: throw PerformException.Builder()
                        .withCause(Throwable("No tab at index $tabIndex"))
                        .build()

                tabAtIndex.select()
            }
        }
    }

    fun searchText(text: String):ViewAction{
        return object : ViewAction {
            override fun getDescription() = "Change view text with $text"

            override fun getConstraints() = allOf(isDisplayed(), isAssignableFrom(SearchView::class.java))

            override fun perform(uiController: UiController, view: View) {
                (view as SearchView).setQuery(text, false)
            }
        }
    }
}