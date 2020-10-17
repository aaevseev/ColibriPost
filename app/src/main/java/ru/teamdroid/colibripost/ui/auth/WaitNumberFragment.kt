package ru.teamdroid.colibripost.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.fragment_channels_settings.*
import kotlinx.coroutines.launch
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.FragmentWaitNumberBinding
import ru.teamdroid.colibripost.remote.auth.AuthHolder
import ru.teamdroid.colibripost.remote.auth.AuthStates.*
import ru.teamdroid.colibripost.ui.core.BaseFragment
import ru.teamdroid.colibripost.ui.core.getColorFromResource
import ru.teamdroid.colibripost.ui.core.getColorState
import javax.inject.Inject

class WaitNumberFragment : BaseFragment() {

    override val layoutId = R.layout.fragment_wait_number
    override val toolbarTitle = 0
    private var _binding: FragmentWaitNumberBinding? = null
    private val binding: FragmentWaitNumberBinding
        get() = _binding!!

    lateinit var countryCodePicker: CountryCodePicker

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

        binding.etPhone.registerCarrierNumberEditText(binding.editTextCarrierNumber)
        binding.etPhone.setPhoneNumberValidityChangeListener (object : CountryCodePicker.PhoneNumberValidityChangeListener {
            override fun onValidityChanged(isValidNumber: Boolean) {
                setBtnOkState(isValidNumber)
            }
        })

        binding.btnOk.setOnClickListener {
            if (authHolder.authState.value == WAIT_FOR_NUMBER) {
                lifecycleScope.launch {
                    authHolder.insertPhoneNumber(binding.etPhone.fullNumberWithPlus)
                }
            }
            Log.d("WaitNumberFragment", "onViewCreated: click ${authHolder.authState.value}")
        }
    }

    fun setBtnOkState(isEnable: Boolean) {
        if (isEnable) {
            binding.btnOk.isEnabled = true
            binding.btnOk.setTextColor(requireContext().getColorFromResource(R.color.accent))
        } else {
            binding.btnOk.isEnabled = false
            binding.btnOk.setTextColor(requireContext().getColorFromResource(R.color.accentEnabled))
        }
    }

    private fun startWaitCodeFragment() {
        base { setNavigationFragment(WaitCodeFragment.newInstance(binding.etPhone.fullNumberWithPlus, binding.etPhone.formattedFullNumber))}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
