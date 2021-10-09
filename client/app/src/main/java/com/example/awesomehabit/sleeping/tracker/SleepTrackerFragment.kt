package com.example.awesomehabit.sleeping.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.awesomehabit.R
import com.example.awesomehabit.database.AppDatabase
import com.example.awesomehabit.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar
import com.example.awesomehabit.CustomCalendarView
import java.util.*

class SleepTrackerFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = AppDatabase.getDatabase(application).sleepDao();

        val fragmentManager = requireNotNull(this.activity).supportFragmentManager

        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application, fragmentManager)

        val sleepTrackerViewModel =
                ViewModelProvider(
                        this, viewModelFactory).get(SleepTrackerViewModel::class.java)

        binding.sleepTrackerViewModel = sleepTrackerViewModel

        binding.lifecycleOwner = this

        sleepTrackerViewModel.showSnackBarEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        sleepTrackerViewModel.snackbarString,
                        Snackbar.LENGTH_SHORT
                ).show()
                sleepTrackerViewModel.doneShowingSnackbar()
            }
        })

        sleepTrackerViewModel.navigateToSleepQuality.observe(viewLifecycleOwner, Observer { night ->
            night?.let {
                this.findNavController().navigate(
                        SleepTrackerFragmentDirections
                                .actionSleepTrackerFragmentToSleepQualityFragment(night.id))
                sleepTrackerViewModel.doneNavigating()
            }
        })

        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        if (day != CustomCalendarView.currentDay_Day || month != CustomCalendarView.currentDay_Month || year != CustomCalendarView.currentDay_Year) {
            sleepTrackerViewModel.startButtonVisible = MutableLiveData<Boolean>(false)
            sleepTrackerViewModel.startButtonVisible = MutableLiveData<Boolean>(false)
        }

        return binding.root
    }
}
