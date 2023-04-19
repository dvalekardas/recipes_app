package com.example.recipes.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

class RecyclerViewMatcher(private val recyclerViewId: Int) {

    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("with RecyclerView id: $recyclerViewId at position: $position")
            }

            override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                val targetView = if (targetViewId == -1) {
                    viewHolder?.itemView
                } else {
                    viewHolder?.itemView?.findViewById(targetViewId)
                }
                return viewHolder != null && targetView != null
            }
        }
    }
}