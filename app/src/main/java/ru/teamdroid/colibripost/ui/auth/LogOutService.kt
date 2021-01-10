package ru.teamdroid.colibripost.ui.auth

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.remote.account.auth.AuthHolder
import ru.teamdroid.colibripost.remote.account.auth.AuthStates
import javax.inject.Inject

class LogOutService constructor(): Service() {

    protected val TAG = this.javaClass.name

    @Inject
    lateinit var authHolder: AuthHolder

    override fun onCreate() {
        super.onCreate()
        App.instance.appComponent.inject(this)
        Log.i(TAG, "onCreate(): Service Started.");
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        GlobalScope.launch {
            Log.i(TAG, "onTaskRemoved(): ${authHolder.authState.value}.")
            if(authHolder.authState.value != AuthStates.AUTHENTICATED && authHolder.authState.value != AuthStates.UNAUTHENTICATED) authHolder.logOut()
            stopSelf()
        }
    }
}