package ru.teamdroid.colibripost

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.teamdroid.colibripost.ui.screens.main.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MainFragment())
                .commit()
        }

    }

}