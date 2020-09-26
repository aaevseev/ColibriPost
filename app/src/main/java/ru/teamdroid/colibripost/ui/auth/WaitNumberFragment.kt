package ru.teamdroid.colibripost.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import kotlinx.coroutines.launch
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.remote.AuthHolder
import ru.teamdroid.colibripost.remote.AuthStates.*
import ru.teamdroid.colibripost.databinding.FragmentWaitNumberBinding
import ru.teamdroid.colibripost.ui.core.BaseFragment
import javax.inject.Inject

class WaitNumberFragment : BaseFragment() {

    override val layoutId = R.layout.fragment_wait_number
    private var _binding: FragmentWaitNumberBinding? = null
    private val binding: FragmentWaitNumberBinding
        get() = _binding!!

    @Inject
    lateinit var authHolder: AuthHolder

    companion object {
        const val TAG = "WaitNumberFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.inject(this)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWaitNumberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authHolder.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UNAUTHENTICATED -> Log.d(
                    "WaitNumberFragment",
                    "onViewCreated: state UNAUTHENTICATED"
                )
                WAIT_FOR_NUMBER -> {
                    Log.d("WaitNumberFragment", "onViewCreated: state WAIT_FOR_NUMBER")
                }
                WAIT_FOR_CODE -> {
                    Log.d("WaitNumberFragment", "onViewCreated: state WAIT_FOR_CODE")
                    startWaitCodeFragment()
                }
                WAIT_FOR_PASSWORD -> Log.d(
                    "WaitNumberFragment",
                    "onViewCreated: state WAIT_FOR_PASSWORD"
                )
                AUTHENTICATED -> Log.d("WaitNumberFragment", "onViewCreated: state AUTHENTICATED")
                UNKNOWN -> Log.d("WaitNumberFragment", "onViewCreated: state UNKNOWN")
            }
        }
        binding.btnOk.setOnClickListener {
            if (authHolder.authState.value == WAIT_FOR_NUMBER) {
                lifecycleScope.launch {
                    authHolder.insertPhoneNumber(binding.etPhone.text.toString())
                }
            }
            Log.d("WaitNumberFragment", "onViewCreated: click ${authHolder.authState.value}")
        }
        binding.btnLogOut.setOnClickListener {
            lifecycleScope.launch {
                authHolder.logOut()
            }
        }
        binding.btnStartAuth.setOnClickListener {
            lifecycleScope.launch {
                authHolder.startAuthentication()
            }
        }
    }

    private fun startWaitCodeFragment() {
        base { setNavigationFragment(WaitCodeFragment()) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
