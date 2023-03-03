package com.udacity.project4

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }


    @Test
    fun editTask() = runBlocking {

        // Set initial state.
        repository.saveTask(Task("TITLE1", "DESCRIPTION"))

        // Start up Tasks screen.
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the task on the list and verify that all the data is correct.
        Espresso.onView(ViewMatchers.withText("TITLE1")).perform(ViewActions.click())
        Espresso.onView(withId(R.id.task_detail_title_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("TITLE1")))
        Espresso.onView(withId(R.id.task_detail_description_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("DESCRIPTION")))
        Espresso.onView(withId(R.id.task_detail_complete_checkbox))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isChecked())))

        // Click on the edit button, edit, and save.
        Espresso.onView(withId(R.id.edit_task_fab)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.add_task_title_edit_text))
            .perform(ViewActions.replaceText("NEW TITLE"))
        Espresso.onView(withId(R.id.add_task_description_edit_text))
            .perform(ViewActions.replaceText("NEW DESCRIPTION"))
        Espresso.onView(withId(R.id.save_task_fab)).perform(ViewActions.click())

        // Verify task is displayed on screen in the task list.
        Espresso.onView(ViewMatchers.withText("NEW TITLE"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // Verify previous task is not displayed.
        Espresso.onView(ViewMatchers.withText("TITLE1")).check(ViewAssertions.doesNotExist())
        // Make sure the activity is closed before resetting the db.
        activityScenario.close()
    }

}
