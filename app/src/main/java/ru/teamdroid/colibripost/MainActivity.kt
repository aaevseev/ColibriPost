package ru.teamdroid.colibripost

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.remote.account.auth.AuthHolder
import ru.teamdroid.colibripost.remote.account.auth.AuthStates
import ru.teamdroid.colibripost.ui.SwitchTransparentView
import ru.teamdroid.colibripost.ui.auth.SignInFragment
import ru.teamdroid.colibripost.ui.bottomnavigation.BottomNavigationFragment
import ru.teamdroid.colibripost.ui.core.BaseFragment
import ru.teamdroid.colibripost.ui.newpost.NewPostFragment
import javax.inject.Inject

class MainActivity : AppCompatActivity(), SwitchTransparentView {

    @Inject
    lateinit var authHolder: AuthHolder

    lateinit var connectivityManager: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        App.instance.appComponent.inject(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        swipeRefreshLayout.setColorSchemeResources(R.color.accent)
        swipeRefreshLayout.isEnabled = false


        lifecycleScope.launch {
            //TODO: проверка авторизации должна быть без Delay
            delay(500)
            if (authHolder.authState.value == AuthStates.AUTHENTICATED) {
                setNavigationFragment(BottomNavigationFragment())
            } else {
                setNavigationFragment(SignInFragment())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        authStateLog()
    }

    fun setNavigationFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment).addToBackStack(BottomNavigationFragment.TAG)
        }.commit()
    }

    override fun onBackPressed() {
        val childFragmentManager = supportFragmentManager.fragments[0].childFragmentManager
        val fragment = childFragmentManager.findFragmentByTag(NewPostFragment.TAG)

        childFragmentManager.also {
            if (fragment != null && fragment.isVisible) {
                val backPressListener = supportFragmentManager.fragments[0] as OnBackPressedListener
                backPressListener.backPressed()
            } else {
                if (it.backStackEntryCount > 1) {
                    setBackStackToolBarTitle(it)
                    it.popBackStack()
                    if (it.backStackEntryCount == 2) supportActionBar?.setDisplayHomeAsUpEnabled(
                        false
                    )
                } else
                    finish()
            }
        }
    }

    fun setBackStackToolBarTitle(fragmentManager: FragmentManager) {
        fragmentManager.also {
            if (it.fragments.size > 1) {
                (it.fragments[it.fragments.size - 2] as BaseFragment).setToolbarTitle()
            }
        }
    }

    fun authStateLog() {
        authHolder.authState.observe(this) { state ->
            when (state) {
                AuthStates.UNAUTHENTICATED -> {
                    Log.d("SplashFragment", "onViewCreated: state UNAUTHENTICATED")
                }
                AuthStates.WAIT_FOR_NUMBER -> {
                    Log.d("SplashFragment", "onViewCreated: state WAIT_FOR_NUMBER")
                }
                AuthStates.WAIT_FOR_CODE -> {
                    Log.d("SplashFragment", "onViewCreated: state WAIT_FOR_CODE")
                }
                AuthStates.WAIT_FOR_PASSWORD -> {
                    Log.d("SplashFragment", "onViewCreated: state WAIT_FOR_PASSWORD")
                }
                AuthStates.AUTHENTICATED -> {
                    Log.d("SplashFragment", "onViewCreated: state AUTHENTICATED")
                }
                AuthStates.UNKNOWN -> {
                    Log.d("SplashFragment", "onViewCreated: state UNKNOWN")
                }
            }
        }
    }

    fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun handleFailure(failure: Failure?) {
        //hide progress
        when (failure) {
            is Failure.NetworkConnectionError -> showMessage(getString(R.string.error_network_toast))
            is Failure.ServerError -> showMessage(getString(R.string.error_server))
            is Failure.ChannelsListIsEmptyError -> showMessage(getString(R.string.channels_list_empty_error))
        }
    }

    override fun setTranspViewVisibility(isVisible: Boolean) {
        if (isVisible)
            transpView.visibility = View.VISIBLE
        else transpView.visibility = View.GONE
    }

    fun swipeRefreshStatus(refreshStatus: Boolean) {
        swipeRefreshLayout.isRefreshing = refreshStatus
    }

}

inline fun Activity?.base(block: MainActivity.() -> Unit) {
    (this as? MainActivity)?.let(block)
}

interface OnBackPressedListener {
    fun backPressed()
}