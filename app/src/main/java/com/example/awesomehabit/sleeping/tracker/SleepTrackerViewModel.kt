package com.example.awesomehabit.sleeping.tracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.awesomehabit.database.sleeping.SleepDatabaseDao
import com.example.awesomehabit.database.sleeping.SleepNight
import com.example.awesomehabit.sleeping.formatNights
import kotlinx.coroutines.launch
import java.util.*

class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {
    private var tonight = MutableLiveData<SleepNight?>()
    private val nights = database.getAllNights()
    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

    val startButtonVisible = Transformations.map(tonight) {
        null == it
    }

    val stopButtonVisible = Transformations.map(tonight) {
        null != it
    }

    val clearButtonVisible = Transformations.map(nights) {
        it?.isNotEmpty()
    }

    private var _showSnackbarEvent = MutableLiveData<Boolean>()

    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }

    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality

    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }

    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        viewModelScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    private fun getTonightFromDatabase(): SleepNight? {
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            return night
    }

    private fun clear() {
        database.clear()
    }

    private fun update(night: SleepNight) {
        database.update(night)
    }

    private fun insert(night: SleepNight) {
        database.insert(night)
    }

    fun onStartTracking() {
        viewModelScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }

    fun onStopTracking() {
        viewModelScope.launch {
            val oldNight = tonight.value?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
            _navigateToSleepQuality.value = oldNight
//            Log.d("@@@@", database.type.toString())
//            val today = Calendar.getInstance()
//            today.set(Calendar.HOUR, 0)
//            today.set(Calendar.MINUTE, 0)
//            today.set(Calendar.SECOND, 0)
//            today.set(Calendar.MILLISECOND, 0)
//            val habits = database.getHabitFrom(today) as? List<SleepNight?>
//            if (habits != null) {
//                habits.forEach {
//                    Log.d("@@@@", today.timeInMillis.toString())
//                    Log.d("@@@@", it!!.sleepQuality.toString())
//                }
//            } else {
//                Log.d("@@@@", "NULL")
//            }
        }
    }

    fun onClear() {
        viewModelScope.launch {
            clear()
            tonight.value = null
        }
        _showSnackbarEvent.value = true
    }

}
