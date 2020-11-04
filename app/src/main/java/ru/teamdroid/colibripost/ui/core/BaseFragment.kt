package ru.teamdroid.colibripost.ui.core

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import ru.teamdroid.colibripost.MainActivity
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.base
import ru.teamdroid.colibripost.domain.type.Failure
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    abstract val layoutId: Int

    abstract val toolbarTitle: Int

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    fun setToolbarTitle(stringTitle: String = "") {
        base {
            supportActionBar?.apply {
                if (stringTitle == "")
                    this.title = getString(toolbarTitle)
                else if(stringTitle == "empty") {
                    this.title = ""
                }
                else {
                    this.title = stringTitle
                }
            }
        }
    }

    open fun handleFailure(failure: Failure?) = base { handleFailure(failure) }

    fun showMessage(message: String) = base { showMessage(message) }

    open fun updateRefresh(status: Boolean?) {
        if (status == true) {
            showRefreshing()
        } else {
            hideRefreshing()
        }
    }

    open fun showKeyboard(v:View) {
        base{
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    open fun hideSoftKeyboard() {
        base{
            if(currentFocus != null)
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    fun showRefreshing() = base { swipeRefreshStatus(true) }

    fun hideRefreshing() = base {
        swipeRefreshStatus(false)
    }

    open fun setNetworkAvailbleUi(isCallback: Boolean) {
        setToolbarTitle(getString(R.string.app_name))
    }

    open fun setNetworkLostUi() {
        setToolbarTitle(getString(R.string.network_waiting))
    }



    inline fun base(block: MainActivity.() -> Unit) {
        activity.base(block)//дочернее активити выполняет код у себя
    }

    inline fun <reified T : ViewModel> viewModel(body: T.() -> Unit): T {
        val vm = ViewModelProviders.of(this, viewModelFactory)[T::class.java]
        vm.body()
        return vm
    }
}