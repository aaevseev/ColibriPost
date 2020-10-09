package ru.teamdroid.colibripost.ui.splash

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.remote.auth.AuthHolder
import ru.teamdroid.colibripost.remote.auth.AuthStates
import ru.teamdroid.colibripost.databinding.FragmentSplashBinding
import javax.inject.Inject

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding: FragmentSplashBinding
        get() = _binding!!

    @Inject
    lateinit var authHolder: AuthHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.inject(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authHolder.authState.observe(viewLifecycleOwner) { state ->
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

    /*override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(2000)
            if (authHolder.authState.value == AuthStates.AUTHENTICATED) {
                findNavController().navigate(R.id.action_splashFragment_to_bottomNavigationFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
            }
        }
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
