package ru.teamdroid.colibripost.ui.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.delay
import ru.teamdroid.colibripost.MainActivity
import ru.teamdroid.colibripost.base
import ru.teamdroid.colibripost.domain.type.Failure
import javax.inject.Inject

abstract class BaseFragment: Fragment() {

    abstract val layoutId:Int

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

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

    open fun handleFailure(failure: Failure?) = base { handleFailure(failure) }

    fun showMessage(message: String) = base { showMessage(message) }

    open fun updateRefresh (status: Boolean?){
        if(status == true){
            showRefreshing()
        }else{
            hideRefreshing()
        }
    }

    fun showRefreshing() = base { swipeRefreshStatus(true) }

    fun hideRefreshing() = base {
        swipeRefreshStatus(false) }

    inline fun base(block: MainActivity.() -> Unit){
        activity.base(block)//дочернее активити выполняет код у себя
    }

    inline fun <reified T : ViewModel> viewModel(body: T.() -> Unit): T {
        val vm = ViewModelProviders.of(this, viewModelFactory)[T::class.java]
        vm.body()
        return vm
    }
}