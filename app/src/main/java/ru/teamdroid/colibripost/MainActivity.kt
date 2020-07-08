package ru.teamdroid.colibripost


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.teamdroid.colibripost.presentation.ui.bottomnavigation.BottomNavigationFragment
import ru.teamdroid.colibripost.presentation.ui.newpost.NewPostFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setNavigationFragment(WaitNumberFragment())
    }


    private fun setNavigationFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment).addToBackStack(BottomNavigationFragment.TAG)
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

}


interface OnBackPressedListener {
    fun backPressed()
}