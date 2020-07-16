package ru.teamdroid.colibripost.presentation.ui.newpost

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import ru.teamdroid.colibripost.databinding.CalendarDialogFragmentBinding

class CalendarDialogFragment : DialogFragment() {
    companion object {
        const val TAG = "CalendarDialogFragment"
        const val REQUEST_DATE = "REQUEST_DATE"
        const val KEY_YEAR = "KEY_YEAR"
        const val KEY_MONTH = "KEY_MONTH"
        const val KEY_DAY = "KEY_DAY"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = CalendarDialogFragmentBinding.inflate(requireActivity().layoutInflater)
        binding.calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            parentFragmentManager.setFragmentResult(
                REQUEST_DATE, bundleOf(
                    KEY_YEAR to year,
                    KEY_MONTH to month,
                    KEY_DAY to dayOfMonth
                    )
            )
            this.dismiss()
        }

        return AlertDialog.Builder(requireActivity()).setView(binding.root).create()
    }
}