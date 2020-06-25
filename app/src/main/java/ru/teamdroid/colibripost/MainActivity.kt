package ru.teamdroid.colibripost

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.teamdroid.colibripost.presentation.ui.bottomnavigation.BottomNavigationFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setNavigationFragment(BottomNavigationFragment())
    }

    private fun setNavigationFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment).addToBackStack(BottomNavigationFragment.TAG)
        }.commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1)
            supportFragmentManager.popBackStack()
        else
            finish()
    }
}