package com.example.awesomehabit.sleeping.tracker

import android.app.Application
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.awesomehabit.database.sleeping.SleepDatabaseDao

class SleepTrackerViewModelFactory(
        private val dataSource: SleepDatabaseDao,
        private val application: Application, private val fragmentManager: FragmentManager) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepTrackerViewModel::class.java)) {
            return SleepTrackerViewModel(dataSource, application, fragmentManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

