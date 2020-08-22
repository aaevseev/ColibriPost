package ru.teamdroid.colibripost.presentation.ui.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.teamdroid.colibripost.MainActivity
import ru.teamdroid.colibripost.base

abstract class BaseFragment: Fragment() {

    abstract val layoutId:Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    fun setToolbarTitle(title: String) {
        base {
            supportActionBar?.title = title
        }
    }

    inline fun base(block: MainActivity.() -> Unit){
        activity.base(block)//дочернее активити выполняет код у себя
    }
}