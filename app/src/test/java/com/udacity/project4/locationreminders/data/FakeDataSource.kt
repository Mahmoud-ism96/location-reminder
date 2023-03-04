package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminderDTO: MutableList<ReminderDTO> = mutableListOf()) :
    ReminderDataSource {

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> = if (shouldReturnError) {
        Result.Error("Error getting reminders")
    } else {
        Result.Success(reminderDTO)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderDTO.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> = if (shouldReturnError) {
        Result.Error("Error getting reminders")
    } else {
        val reminder = reminderDTO.find { it.id == id }

        if (reminder == null) {
            Result.Error("Reminder not found")
        } else {
            Result.Success(reminder)
        }
    }

    override suspend fun deleteAllReminders() {
        reminderDTO.clear()
    }

    override suspend fun deleteReminder(id: String) {
        val reminder = reminderDTO.find { it.id == id }
        reminderDTO.remove(reminder)
    }
}