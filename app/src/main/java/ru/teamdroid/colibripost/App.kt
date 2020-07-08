package ru.teamdroid.colibripost

import android.app.Application
import ru.teamdroid.colibripost.di.components.AppComponent
import ru.teamdroid.colibripost.di.components.DaggerAppComponent
import ru.teamdroid.colibripost.di.modules.AppModule

class App : Application() {
    lateinit var appComponent: AppComponent
        private set

    companion object {
        lateinit var instance: App
            private set

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this)).build()
    }
}