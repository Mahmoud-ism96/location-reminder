package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminderDTO: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error(
                "Error getting reminders"
            )
        }
        reminderDTO?.let { return Result.Success(it) }
        return Result.Error("Could not find reminders")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderDTO?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val reminder = reminderDTO?.find { it ->
            it.id == id
        }
        return when {
            shouldReturnError -> {
                Result.Error("Could not find reminders")
            }

            reminder != null -> {
                Result.Success(reminder)
            }
            else -> {
                Result.Error("Could not find reminders")
            }
        }
    }

    override suspend fun deleteAllReminders() {
        reminderDTO?.clear()
    }


}