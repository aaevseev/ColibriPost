package ru.teamdroid.colibripost.ui.auth

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.teamdroid.colibripost.R

class CodeIsNotActualDialog (
    private val text: String
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.dialog_window, null))
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog?.findViewById<TextView>(R.id.tvMessage)?.setText(text)

        dialog?.findViewById<TextView>(R.id.tvOk)?.setOnClickListener {
            dialog!!.dismiss()
        }
        dialog?.findViewById<TextView>(R.id.tvCancel)?.visibility = View.GONE

    }

    companion object {
        const val TAG = "CodeIsNotActualDialogDialog"
    }
}