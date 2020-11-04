package ru.teamdroid.colibripost.ui.auth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status


class MySMSBroadcastReceiver constructor() : BroadcastReceiver() {

    private var smsReceiver: OnAuthNumberReceivedListener? = null

    fun initSmsListener(receiver: OnAuthNumberReceivedListener) {
        this.smsReceiver = receiver
    }

    override fun onReceive(context: Context?, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras: Bundle? = intent.extras
            val status: Status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
            Log.d("TAG", "MySMSBroadcastReceiver : onReceiver")
            when (status.getStatusCode()) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.
                    smsReceiver?.onAuthNumberReceived(message)
                    Log.d("TAG", "MySMSBroadcastReceiver : onReceiver(CommonStatusCodes.SUCCESS)")
                }
                CommonStatusCodes.TIMEOUT -> {
                    // Waiting for SMS timed out (5 minutes)
                    //LocalBroadcastManager.getInstance(context!!).unregisterReceiver(this)
                    Log.d("TAG", "MySMSBroadcastReceiver : onReceiver(CommonStatusCodes.TIMEOUT)")
                }
            }
        }
    }

    companion object {
        const val SMSRetrievedAction = "com.google.android.gms.auth.api.phone.SMS_RETRIEVED"
    }


}

interface OnAuthNumberReceivedListener {
    fun onAuthNumberReceived(authNumber: String?)
}