package ru.teamdroid.colibripost.presentation.ui.bottomnavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.teamdroid.colibripost.OnBackPressedListener
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.FragmentBottomNavigationBinding
import ru.teamdroid.colibripost.presentation.ui.main.MainFragment
import ru.teamdroid.colibripost.presentation.ui.newpost.NewPostFragment
import ru.teamdroid.colibripost.presentation.ui.settings.SettingsFragment

class BottomNavigationFragment : Fragment(), OnBackPressedListener {

    private var selectedFragment = MainFragment.TAG

    private var _binding: FragmentBottomNavigationBinding? = null
    private val binding: FragmentBottomNavigationBinding
        get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomNavigationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomNavigator()
    }

    private fun setupBottomNavigator() {

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_main -> {
                    displayFragment(MainFragment.TAG)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_newPost -> {
                    binding.bottomNavigation.visibility = View.GONE
                    displayFragment(NewPostFragment.TAG)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_settings -> {
                    displayFragment(SettingsFragment.TAG)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        displayFragment(selectedFragment)
    }

    private fun displayFragment(tag: String) {
        childFragmentManager.beginTransaction().apply {
            childFragmentManager.findFragmentByTag(selectedFragment)?.let { if (it.isAdded) hide(it) }
            selectedFragment = tag
            val fragment = childFragmentManager.findFragmentByTag(selectedFragment) ?: createFragment(selectedFragment)
            if (fragment.isAdded) {
                show(fragment)
            } else {
                add(R.id.navigationContainer, fragment, selectedFragment).addToBackStack(selectedFragment)
            }
        }.commit()
    }

    private fun createFragment(tag: String): Fragment {
        return when (tag) {
            MainFragment.TAG -> MainFragment()
            NewPostFragment.TAG -> NewPostFragment()
            SettingsFragment.TAG -> SettingsFragment()
            else -> MainFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "BottomNavigationFragment"
    }

    override fun backPressed() {
        displayFragment(MainFragment.TAG)
        binding.bottomNavigation.selectedItemId = R.id.navigation_main
        binding.bottomNavigation.visibility = View.VISIBLE
    }
}