package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
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
    fun setupReminderListViewModel() {
        stopKoin()

        fakeDataSource = FakeDataSource()

        reminderViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }

    @Test
    fun loadTasks_Loading() {
        mainCoroutineRule.pauseDispatcher()
        reminderViewModel.loadReminders()

        assertThat(reminderViewModel.showLoading.getOrAwaitValue(), Matchers.`is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(reminderViewModel.showLoading.getOrAwaitValue(), Matchers.`is`(false))

    }

    @Test
    fun addNewReminder_setsNewReminderEvent() = mainCoroutineRule.runBlockingTest {
        val reminder = ReminderDTO("Shopping", "Buy new Jeans", "Waterway", 30.043001792549084, 31.47547344399169)

        // When adding a new task
        fakeDataSource.saveReminder(reminder)

        // Then the new task event is triggered
        val value = reminderViewModel.loadReminders()

        assertThat(value, Matchers.not(Matchers.nullValue()))

    }

    @Test
    fun reminderList_SnackbarUpdated() {
        fakeDataSource.setReturnError(true)

        reminderViewModel.loadReminders()


        assertThat(reminderViewModel.showSnackBar.getOrAwaitValue(), `is` ("Error getting reminders"))
    }
}