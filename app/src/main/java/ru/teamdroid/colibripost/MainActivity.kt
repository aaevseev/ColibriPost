package ru.teamdroid.colibripost


import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.teamdroid.colibripost.data.AuthHolder
import ru.teamdroid.colibripost.data.AuthStates
import ru.teamdroid.colibripost.presentation.ui.auth.SignInFragment
import ru.teamdroid.colibripost.presentation.ui.bottomnavigation.BottomNavigationFragment
import ru.teamdroid.colibripost.presentation.ui.newpost.NewPostFragment
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var authHolder: AuthHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        App.instance.appComponent.injectMainActivity(this)
        setSupportActionBar(toolbar)

    }

    override fun onResume() {
        super.onResume()
        authStateLog()
        lifecycleScope.launch {
            //TODO: проверка авторизации должна быть без Delay
            delay(500)
            if (authHolder.authState.value == AuthStates.AUTHENTICATED) {
                setNavigationFragment(BottomNavigationFragment())
            }
            else {
                setNavigationFragment(SignInFragment())
            }
        }
    }

     fun setNavigationFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment).addToBackStack(BottomNavigationFragment.TAG)
        }.commit()
    }

    override fun onBackPressed() {
        val childFragmentManager = supportFragmentManager.fragments[0].childFragmentManager
        val fragment = childFragmentManager.findFragmentByTag(NewPostFragment.TAG)

        if (fragment != null && fragment.isVisible) {
            val backPressListener = supportFragmentManager.fragments[0] as OnBackPressedListener
            backPressListener.backPressed()
        } else {
            if (supportFragmentManager.backStackEntryCount > 1)
                supportFragmentManager.popBackStack()
            else
                finish()
        }
    }

    fun authStateLog(){
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
}

inline fun Activity?.base(block: MainActivity.() -> Unit){
    (this as? MainActivity)?.let(block)
}

interface OnBackPressedListener {
    fun backPressed()
}