package ru.teamdroid.colibripost.di

import android.os.Bundle
import androidx.lifecycle.ViewModel

interface ViewModelAssistedFactory<T : ViewModel> {
    fun create(arguments: Bundle): T
}