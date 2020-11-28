package com.example.awesomehabit.sleeping.tracker

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.awesomehabit.R
import com.example.awesomehabit.database.sleeping.SleepGoal
import kotlinx.android.synthetic.main.dialog_sleep_goal.view.*

class SleepGoalDialogFragment : DialogFragment() {
    private var listener: SleepGoalDialogListener? = null

    interface SleepGoalDialogListener {
        fun showSnackBarSuccess()
        fun showSnackBarFail()
    }

    fun setSleepGoalDialogListener(listener: SleepGoalDialogListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val sleepGoal = SleepGoal.getInstance(requireActivity().applicationContext)
            val inflater = it.layoutInflater;
            val inflatedView = inflater.inflate(R.layout.dialog_sleep_goal, null)
            val currentGoal = inflatedView.txtSleepGoal.text.toString() + " " + sleepGoal.toString()
            inflatedView.txtSleepGoal.text = currentGoal
            builder.setView(inflatedView)
            builder.setPositiveButton(R.string.set_goal,
                    DialogInterface.OnClickListener { dialog, id ->
                        var setHour = inflatedView.edtHour.text.toString()
                        if (setHour.isBlank()) setHour = "0"
                        var setMinute = inflatedView.edtMinute.text.toString()
                        if (setMinute.isBlank()) setMinute = "0"
                        if (sleepGoal.setSleepGoal(setHour.toInt(), setMinute.toInt()))
                            listener!!.showSnackBarSuccess()
                        else
                            listener!!.showSnackBarFail()
                    })
            builder.setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->  dialog.cancel()})
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}