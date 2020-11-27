@file:Suppress("DEPRECATION")

package com.example.awesomehabit.sleeping.tracker

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import com.example.awesomehabit.R
import com.example.awesomehabit.sleeping.SleepGoal
import kotlinx.android.synthetic.main.dialog_sleep_goal.view.*

class SleepGoalDialogFragment : DialogFragment() {
    private var listener: SleepGoalDialogListener? = null

    interface SleepGoalDialogListener {
        fun showSnackBar()
    }

    fun setSleepGoalDialogListener(listener: SleepGoalDialogListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val sleepGoal = SleepGoal.getInstance()
            val inflater = it.layoutInflater;
            val inflatedView = inflater.inflate(R.layout.dialog_sleep_goal, null)
            val currentGoal = inflatedView.txtSleepGoal.text.toString() + " " + sleepGoal.toString()
            inflatedView.txtSleepGoal.text = currentGoal
            builder.setView(inflatedView)
            builder.setPositiveButton(R.string.set_goal,
                    DialogInterface.OnClickListener { dialog, which ->
                        sleepGoal.setSleepGoal(inflatedView.edtHour.text.toString().toInt(),
                                inflatedView.edtMinute.text.toString().toInt())
                        listener!!.showSnackBar()
                    })
            builder.setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, which ->  dialog.cancel()})
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}