package ru.teamdroid.colibripost.di.components

import dagger.Component
import ru.teamdroid.colibripost.MainActivity
import ru.teamdroid.colibripost.di.modules.AppModule
import ru.teamdroid.colibripost.di.modules.ViewModelsModule
import ru.teamdroid.colibripost.presentation.ui.auth.WaitCodeFragment
import ru.teamdroid.colibripost.presentation.ui.auth.WaitNumberFragment
import ru.teamdroid.colibripost.presentation.ui.main.MainFragment
import ru.teamdroid.colibripost.presentation.ui.newpost.NewPostFragment
import ru.teamdroid.colibripost.presentation.ui.newpost.NewPostViewModel
import ru.teamdroid.colibripost.presentation.ui.splash.SplashFragment
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AppModule::class,
        ViewModelsModule::class
    ]
)
interface AppComponent {
    fun injectWaitPhoneFragment(fragment: WaitNumberFragment)
    fun injectWaitCodeFragment(fragment: WaitCodeFragment)
    fun injectSplashFragment(fragment: SplashFragment)
    fun injectMainFragment(fragment: MainFragment)
    fun injectNewPostFragment(fragment: NewPostFragment)

    fun injectMainActivity(mainActivity: MainActivity)
}