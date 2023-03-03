package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        localDataSource = RemindersLocalRepository(
            database.reminderDao(), Dispatchers.Main
        )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminderAndGetById() = runBlocking {
        val reminder =
            ReminderDTO("Yoga", "Yoga Session", "Flexana", 30.011642516567647, 31.4382229282943)
        localDataSource.saveReminder(reminder)

        val loaded = localDataSource.getReminder(reminder.id)

        assertThat(loaded is Result.Success, `is`(true))
        loaded as Result.Success

        assertThat(loaded.data.id, Matchers.`is`(reminder.id))
        assertThat(loaded.data.title, Matchers.`is`(reminder.title))
        assertThat(loaded.data.description, Matchers.`is`(reminder.description))
        assertThat(loaded.data.location, Matchers.`is`(reminder.location))
        assertThat(loaded.data.latitude, Matchers.`is`(reminder.latitude))
        assertThat(loaded.data.longitude, Matchers.`is`(reminder.longitude))
    }

    @Test
    fun getReminders() = runBlocking {
        val reminder =
            ReminderDTO("Yoga", "Yoga Session", "Flexana", 30.011642516567647, 31.4382229282943)
        val reminder2 =
            ReminderDTO("Yoga1", "Yoga Session", "Flexana", 30.011642516567647, 31.4382229282943)

        localDataSource.saveReminder(reminder)
        localDataSource.saveReminder(reminder2)

        val remindersList = localDataSource.getReminders()

        assertThat(remindersList is Result.Success, `is`(true))
        remindersList as Result.Success

        assertThat(remindersList.data, `is`(not(emptyList())))
    }

    @Test
    fun deleteAllReminders() = runBlocking {
        val reminder =
            ReminderDTO("Yoga", "Yoga Session", "Flexana", 30.011642516567647, 31.4382229282943)

        localDataSource.saveReminder(reminder)

        localDataSource.deleteAllReminders()

        val remindersList = localDataSource.getReminders()

        assertThat(remindersList is Result.Success, `is`(true))
        remindersList as Result.Success

        assertThat(remindersList.data, `is`(emptyList()))
    }

    @Test
    fun deleteReminder() = runBlocking {
        val reminder =
            ReminderDTO("Yoga", "Yoga Session", "Flexana", 30.011642516567647, 31.4382229282943)

        localDataSource.saveReminder(reminder)

        localDataSource.deleteReminder(reminder.id)

        val remindersList = localDataSource.getReminders()

        assertThat(remindersList is Result.Success, `is`(true))
        remindersList as Result.Success

        assertThat(remindersList.data, `is`(emptyList()))
    }

    @Test
    fun getReminderById_ReturnError() = runBlocking {
        val reminder =
            ReminderDTO("Yoga", "Yoga Session", "Flexana", 30.011642516567647, 31.4382229282943)

        localDataSource.saveReminder(reminder)

        localDataSource.deleteAllReminders()

        val result = localDataSource.getReminder(reminder.id)

        assertThat(result is Result.Error, `is`(true))
        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }


}