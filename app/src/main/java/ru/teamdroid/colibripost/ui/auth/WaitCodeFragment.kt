package ru.teamdroid.colibripost.ui.auth

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.fragment_wait_code.*
import kotlinx.coroutines.launch
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.FragmentWaitCodeBinding
import ru.teamdroid.colibripost.remote.auth.AuthHolder
import ru.teamdroid.colibripost.remote.auth.AuthStates
import ru.teamdroid.colibripost.ui.bottomnavigation.BottomNavigationFragment
import ru.teamdroid.colibripost.ui.core.BaseFragment
import ru.teamdroid.colibripost.ui.core.getColorFromResource
import ru.teamdroid.colibripost.ui.core.getColorState
import javax.inject.Inject

class WaitCodeFragment : BaseFragment() {

    override val layoutId = R.layout.fragment_wait_code
    override val toolbarTitle = 0

    private var _binding: FragmentWaitCodeBinding? = null
    private val binding: FragmentWaitCodeBinding
        get() = _binding!!

    @Inject
    lateinit var authHolder: AuthHolder

    lateinit var phoneNumber: String

    var seconds: Int = 59

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.inject(this)
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


        setBtnSendState(false)
        refreshSendItAgainView()

        arguments?.let {
            binding.tvHints.text = String.format(getString(R.string.we_sent_you_code), it.getString(FORMATTED_NUMBER_WITH_PLUS))
            phoneNumber = it.getString(NUMBER_WITH_PLUS)!!
        }

        binding.etCode.setOnKeyListener { v, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                        lifecycleScope.launch {
                            seconds = 0
                            binding.tvHints.text = getString(R.string.checking)
                            authHolder.insertCode(binding.etCode.text.toString())
                        }
                    }
                }
            }
            false
        }

        binding.btnSendAgain.setOnClickListener {
            if (authHolder.authState.value == AuthStates.WAIT_FOR_CODE) {
                lifecycleScope.launch {
                    authHolder.insertPhoneNumber(phoneNumber)
                }
                seconds = 59
                refreshSendItAgainView()
                setBtnSendState(false)
            }
        }
    }

    fun refreshSendItAgainView(){
        if(_binding != null){
            binding.btnSendAgain.text = String.format(getString(R.string.seconds), seconds.toString())
            seconds--
            if(seconds > 0)
                Handler().postDelayed({refreshSendItAgainView()}, 1000)
            else setBtnSendState(true)
        }
    }

    fun setBtnSendState(isEnable: Boolean) {
        if (isEnable) {
            binding.btnSendAgain.isEnabled = true
            binding.btnSendAgain.setTextColor(requireContext().getColorFromResource(R.color.accent))
            binding.btnSendAgain.text = getString(R.string.send_it_again)
        } else {
            binding.btnSendAgain.isEnabled = false
            binding.btnSendAgain.setTextColor(requireContext().getColorFromResource(R.color.accentEnabled))
        }
    }

    private fun startMainFragment() {
        base { setNavigationFragment(BottomNavigationFragment()) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val NUMBER_WITH_PLUS = "numberWithPlus"
        private const val FORMATTED_NUMBER_WITH_PLUS = "formattedNumberWithPlus"

        fun newInstance(numberWithPlus: String, formattedNumberWithPlus: String) = WaitCodeFragment().apply {
            arguments = bundleOf(NUMBER_WITH_PLUS to numberWithPlus, FORMATTED_NUMBER_WITH_PLUS to formattedNumberWithPlus)
        }
    }
}
