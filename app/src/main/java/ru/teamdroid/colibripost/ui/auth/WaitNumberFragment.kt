package ru.teamdroid.colibripost.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.FragmentWaitNumberBinding
import ru.teamdroid.colibripost.remote.account.auth.AuthHolder
import ru.teamdroid.colibripost.remote.account.auth.AuthStates.*
import ru.teamdroid.colibripost.ui.core.BaseFragment
import ru.teamdroid.colibripost.ui.core.getColorFromResource
import javax.inject.Inject

class WaitNumberFragment : BaseFragment() {

    override val layoutId = R.layout.fragment_wait_number
    override val toolbarTitle = 0
    private var _binding: FragmentWaitNumberBinding? = null
    private val binding: FragmentWaitNumberBinding
        get() = _binding!!

    lateinit var countryCodePicker: CountryCodePicker

    var isAuth = false

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
                    if(isAuth) startWaitCodeFragment()
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
                isAuth = true
                lifecycleScope.launch {
                    authHolder.insertPhoneNumber(binding.etPhone.fullNumberWithPlus)
                }
            }
            Log.d("WaitNumberFragment", "onViewCreated: click ${authHolder.authState.value}")
        }

        base {
            toolbar.visibility = View.VISIBLE
            lnWhiteBackStack.visibility = View.VISIBLE
            ibBackstack.setOnClickListener {
                setNavigationFragment(SignInFragment())
            }
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
        if(!isAuth) logOut()
    }

    fun logOut(){
        lifecycleScope.launch { authHolder.logOut() }
    }

    companion object {

        const val TAG = "WaitNumberFragment"
        private const val IS_CODE_NEED = "isCodeNeed"

        fun newInstance(isCodeNeed:Boolean) = WaitNumberFragment().apply {
            arguments = bundleOf(IS_CODE_NEED to isCodeNeed)
        }
    }

}
