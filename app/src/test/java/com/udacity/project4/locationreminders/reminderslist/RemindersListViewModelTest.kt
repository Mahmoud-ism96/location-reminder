package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    // Subject under test
    private lateinit var reminderViewModel: RemindersListViewModel
    private lateinit var fakeDataSource: FakeDataSource


    @Before
    fun setup() {
        stopKoin()

        fakeDataSource = FakeDataSource()

        reminderViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }

    @Test
    fun loadReminders_showLoading() {
        mainCoroutineRule.pauseDispatcher()
        reminderViewModel.loadReminders()

        assertThat(reminderViewModel.showLoading.getOrAwaitValue(), Matchers.`is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(reminderViewModel.showLoading.getOrAwaitValue(), Matchers.`is`(false))

    }




}