package ru.teamdroid.colibripost.di.components

import dagger.Component
import ru.teamdroid.colibripost.MainActivity
import ru.teamdroid.colibripost.di.modules.AppModule
import ru.teamdroid.colibripost.di.modules.CacheModule
import ru.teamdroid.colibripost.di.modules.RemoteModule
import ru.teamdroid.colibripost.di.modules.ViewModelsModule
import ru.teamdroid.colibripost.ui.auth.WaitCodeFragment
import ru.teamdroid.colibripost.ui.auth.WaitNumberFragment
import ru.teamdroid.colibripost.ui.main.MainFragment
import ru.teamdroid.colibripost.ui.newpost.NewPostFragment
import ru.teamdroid.colibripost.ui.settings.SettingsFragment
import ru.teamdroid.colibripost.ui.settings.channels.ChannelsSettingsFragment
import ru.teamdroid.colibripost.ui.splash.SplashFragment
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AppModule::class,
        CacheModule::class,
        RemoteModule::class,
        ViewModelsModule::class
    ]
)
interface AppComponent {
    fun inject(fragment: WaitNumberFragment)
    fun inject(fragment: WaitCodeFragment)
    fun inject(fragment: SplashFragment)
    fun inject(fragment: MainFragment)
    fun inject(fragment: NewPostFragment)
    fun inject(fragment: ChannelsSettingsFragment)
    fun inject(fragment: SettingsFragment)

    fun inject(mainActivity: MainActivity)
}