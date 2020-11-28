package com.example.awesomehabit.sleeping.tracker

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.awesomehabit.R

class ClearAllDialogFragment : DialogFragment() {
    private var listener: ClearAllDialogListener? = null

    interface ClearAllDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

    fun setClearAllDialogLister(clearAllDialogListener: ClearAllDialogListener) {
        listener = clearAllDialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.setMessage(R.string.dialog_clear_all_title)
                        .setPositiveButton(R.string.clear,
                                DialogInterface.OnClickListener { dialog, id ->
                                    listener!!.onDialogPositiveClick(this)
                                })
                        .setNegativeButton(R.string.cancel,
                                DialogInterface.OnClickListener { dialog, id ->
                                    dialog.cancel()
                                })
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
    }


}