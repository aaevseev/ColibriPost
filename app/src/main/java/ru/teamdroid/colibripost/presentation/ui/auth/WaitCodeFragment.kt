package ru.teamdroid.colibripost.presentation.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.data.AuthHolder
import ru.teamdroid.colibripost.data.AuthStates
import ru.teamdroid.colibripost.databinding.FragmentWaitCodeBinding
import javax.inject.Inject

class WaitCodeFragment : Fragment() {
    private var _binding: FragmentWaitCodeBinding? = null
    private val binding: FragmentWaitCodeBinding
        get() = _binding!!

    @Inject
    lateinit var authHolder: AuthHolder


    companion object {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectWaitCodeFragment(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWaitCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authHolder.authState.observe(viewLifecycleOwner) { state: AuthStates ->
            when (state) {
                AuthStates.UNAUTHENTICATED -> Log.d(
                    "WaitCodeFragment",
                    "onViewCreated: state UNAUTHENTICATED"
                )
                AuthStates.WAIT_FOR_NUMBER -> {
                    Log.d("WaitCodeFragment", "onViewCreated: state WAIT_FOR_NUMBER")
                }
                AuthStates.WAIT_FOR_CODE -> {
                    Log.d("WaitCodeFragment", "onViewCreated: state WAIT_FOR_CODE")
                }
                AuthStates.WAIT_FOR_PASSWORD -> {
                    Log.d("WaitCodeFragment", "onViewCreated: state WAIT_FOR_PASSWORD")
                }
                AuthStates.AUTHENTICATED -> {
                    Log.d("WaitCodeFragment", "onViewCreated: state AUTHENTICATED")
                    startMainFragment()
                }
                AuthStates.UNKNOWN -> {
                    Log.d("WaitCodeFragment", "onViewCreated: state UNKNOWN")
                }
            }
        }
        binding.btnOk.setOnClickListener {
            if (authHolder.authState.value == AuthStates.WAIT_FOR_CODE) {
                lifecycleScope.launch {
                    authHolder.insertCode(binding.etPhone.text.toString())
                }
            }
        }
    }

    private fun startMainFragment() {
        findNavController().navigate(R.id.action_waitCodeFragment_to_bottomNavigationFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
