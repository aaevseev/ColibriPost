package ru.teamdroid.colibripost.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.teamdroid.colibripost.di.ViewModelKey
import ru.teamdroid.colibripost.di.viewmodel.AccountViewModel
import ru.teamdroid.colibripost.di.viewmodel.AuthViewModel
import ru.teamdroid.colibripost.di.viewmodel.ChannelsViewModel
import ru.teamdroid.colibripost.di.viewmodel.ViewModelFactory
import ru.teamdroid.colibripost.ui.newpost.NewPostViewModel

@Module
abstract class ViewModelsModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(NewPostViewModel::class)
    abstract fun favoriteViewModel(viewModel: NewPostViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChannelsViewModel::class)
    abstract fun channelsViewModel(viewModel: ChannelsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun accountViewModel(viewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun authViewModel(viewModel: AuthViewModel): ViewModel

}