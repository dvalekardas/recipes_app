package com.example.recipes.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.sportevents.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule

open class BaseTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesTestRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val schedulers = RxImmediateSchedulerRule()
}
