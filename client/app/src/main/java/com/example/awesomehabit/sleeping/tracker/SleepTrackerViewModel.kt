package com.example.awesomehabit.sleeping.tracker

import android.app.Application
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import com.example.awesomehabit.CustomCalendarView
import com.example.awesomehabit.R
import com.example.awesomehabit.database.sleeping.SleepDatabaseDao
import com.example.awesomehabit.database.sleeping.SleepNight
import com.example.awesomehabit.sleeping.formatNights
import kotlinx.coroutines.launch
import java.util.*

class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application, private val fragmentManager: FragmentManager) : AndroidViewModel(application) {
    private var tonight = MutableLiveData<SleepNight?>()
    private var nights = database.getHabitFrom(CustomCalendarView.currentDay_Day, CustomCalendarView.currentDay_Month, CustomCalendarView.currentDay_Year)
//    private var nights = database.getAllNights();
    var snackbarString: String = ""

    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

    var startButtonVisible = Transformations.map(tonight) {
        null == it
    }

    var stopButtonVisible = Transformations.map(tonight) {
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
//        val currentTime = System.currentTimeMillis()
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = currentTime
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//        val month = calendar.get(Calendar.MONTH)
//        val year = calendar.get(Calendar.YEAR)
//        if (day == CustomCalendarView.currentDay_Day && month == CustomCalendarView.currentDay_Month && year == CustomCalendarView.currentDay_Year) {
//            viewModelScope.launch {
//                val newNight = SleepNight(day, month, year)
//                insert(newNight)
//                tonight.value = getTonightFromDatabase()
//            }
//        } else {
//            snackbarString = getApplication<Application>().applicationContext.getString(R.string.invalid_date)
//            _showSnackbarEvent.value = true
//        }
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = currentTime
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val newNight = SleepNight(day, month, year)
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
        }
    }

    fun onClear() {
        val clearAllDialog = ClearAllDialogFragment()
        clearAllDialog.setClearAllDialogLister(object : ClearAllDialogFragment.ClearAllDialogListener {
            override fun onDialogPositiveClick(dialog: DialogFragment) {
                viewModelScope.launch {
                    clear()
                    tonight.value = null
                }
                snackbarString = getApplication<Application>().applicationContext.getString(R.string.cleared_message)
                _showSnackbarEvent.value = true
            }
        })
        clearAllDialog.show(fragmentManager, "clearAllDialog")
    }

    fun onSetGoal() {
//        val sleepGoalDialog = SleepGoalDialogFragment()
//        sleepGoalDialog.setSleepGoalDialogListener(object:SleepGoalDialogFragment.SleepGoalDialogListener {
//            override fun showSnackBarSuccess() {
//                snackbarString =
//                        getApplication<Application>().applicationContext.getString(R.string.set_goal_snackbar_success) +
//                                " " + SleepGoal.getInstance(getApplication<Application>().applicationContext).toString() + "."
//                _showSnackbarEvent.value = true
//            }
//
//            override fun showSnackBarFail() {
//                snackbarString =
//                        getApplication<Application>().applicationContext.getString(R.string.set_goal_snackbar_fail)
//                _showSnackbarEvent.value = true
//            }
//        })
//        sleepGoalDialog.show(fragmentManager, "sleepGoalDialog")

        snackbarString = getApplication<Application>().applicationContext.getString(R.string.implementing)
        _showSnackbarEvent.value = true
    }
}
