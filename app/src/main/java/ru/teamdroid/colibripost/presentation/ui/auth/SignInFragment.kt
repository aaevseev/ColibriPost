package ru.teamdroid.colibripost.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.FragmentSignInBinding
import ru.teamdroid.colibripost.presentation.ui.core.BaseFragment

class SignInFragment : BaseFragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding: FragmentSignInBinding
        get() = _binding!!

    companion object {
        const val TAG = "SignInFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override val layoutId = R.layout.fragment_sign_in


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSignIn.setOnClickListener {
            base{setNavigationFragment(WaitNumberFragment())}
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
