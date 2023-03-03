package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    // Subject under test
    private lateinit var reminderViewModel: SaveReminderViewModel
    private lateinit var fakeDataSource: FakeDataSource

    @Before
    fun setupSaveReminderViewModel() {
        stopKoin()

        fakeDataSource = FakeDataSource()

        reminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }

    @Test
    fun validateAndSaveRemind_dataAdded(){
        val reminder = ReminderDataItem("Yoga", "Yoga Session", "Flexana", 30.011642516567647, 31.4382229282943)

        assertThat(reminderViewModel.validateAndSaveReminder(reminder),`is`(true))
    }

    @Test
    fun validateAndSaveRemind_dataFailed(){
        val reminder = ReminderDataItem("", "Yoga Session", "", 30.011642516567647, 31.4382229282943)

        assertThat(reminderViewModel.validateAndSaveReminder(reminder),`is`(false))
    }

    @Test
    fun validateEnteredData_emptyTitle() {
        val reminder = ReminderDataItem("", "Yoga Session", "Flexana", 30.011642516567647, 31.4382229282943)

        assertThat(reminderViewModel.validateEnteredData(reminder),`is`(false))

        assertThat(reminderViewModel.showSnackBarInt.getOrAwaitValue(),`is`(R.string.err_enter_title))
    }

    @Test
    fun validateEnteredData_emptyLocation() {
        val reminder = ReminderDataItem("Yoga", "Yoga Session", "", 30.011642516567647, 31.4382229282943)

        assertThat(reminderViewModel.validateEnteredData(reminder),`is`(false))

        assertThat(reminderViewModel.showSnackBarInt.getOrAwaitValue(),`is`(R.string.err_select_location))
    }

}