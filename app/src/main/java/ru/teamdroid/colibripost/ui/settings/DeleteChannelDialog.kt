package ru.teamdroid.colibripost.ui.settings

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.delete_channel_dialog.view.*
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.domain.channels.ChannelEntity

class DeleteChannelDialog (
    private val channelId: Long,
    private val deleteChannel: (channelId:Long) -> Unit
): DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.delete_channel_dialog, null))
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

        dialog?.findViewById<TextView>(R.id.tvDeleteChannel)?.setOnClickListener {
            deleteChannel(channelId)
            dialog!!.dismiss()
        }
        dialog?.findViewById<TextView>(R.id.tvCancelDelete)?.setOnClickListener { dialog!!.dismiss() }

    }

    companion object {
        const val TAG = "DeleteChannelDialogFragment"
    }
}