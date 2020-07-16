package ru.teamdroid.colibripost.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.teamdroid.colibripost.di.ViewModelFactory
import ru.teamdroid.colibripost.di.ViewModelKey
import ru.teamdroid.colibripost.presentation.ui.newpost.NewPostViewModel

@Module
abstract class ViewModelsModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(NewPostViewModel::class)
    internal abstract fun favoriteViewModel(viewModel: NewPostViewModel): ViewModel





}