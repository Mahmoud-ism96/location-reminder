package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDB() {
        database.close()
    }

    @Test
    fun saveReminderAndGetById() = runBlockingTest {
        val reminder =
            ReminderDTO("Yoga", "Yoga Session", "Flexana", 30.011642516567647, 31.4382229282943)
        database.reminderDao().saveReminder(reminder)

        val loaded = database.reminderDao().getReminderById(reminder.id)

        assertThat(loaded as ReminderDTO, Matchers.notNullValue())
        assertThat(loaded.id, Matchers.`is`(reminder.id))
        assertThat(loaded.title, Matchers.`is`(reminder.title))
        assertThat(loaded.description, Matchers.`is`(reminder.description))
        assertThat(loaded.location, Matchers.`is`(reminder.location))
        assertThat(loaded.latitude, Matchers.`is`(reminder.latitude))
        assertThat(loaded.longitude, Matchers.`is`(reminder.longitude))
    }

    @Test
    fun getReminders() = runBlockingTest {
        val reminder =
            ReminderDTO("Yoga", "Yoga Session", "Flexana", 30.011642516567647, 31.4382229282943)
        val reminder2 =
            ReminderDTO("Yoga1", "Yoga Session", "Flexana", 30.011642516567647, 31.4382229282943)

        database.reminderDao().saveReminder(reminder)
        database.reminderDao().saveReminder(reminder2)

        val remindersList = database.reminderDao().getReminders()

        assertThat(remindersList, `is`(not(emptyList())))
    }

    @Test
    fun deleteAllReminders() = runBlockingTest {
        val reminder =
            ReminderDTO("Yoga", "Yoga Session", "Flexana", 30.011642516567647, 31.4382229282943)

        database.reminderDao().saveReminder(reminder)

        database.reminderDao().deleteAllReminders()

        val remindersList = database.reminderDao().getReminders()

        assertThat(remindersList, `is`(emptyList()))
    }

    @Test
    fun deleteReminder() = runBlockingTest {
        val reminder =
            ReminderDTO("Yoga", "Yoga Session", "Flexana", 30.011642516567647, 31.4382229282943)

        database.reminderDao().saveReminder(reminder)

        database.reminderDao().deleteReminder(reminder.id)

        val remindersList = database.reminderDao().getReminders()

        assertThat(remindersList, `is`(emptyList()))
    }

}