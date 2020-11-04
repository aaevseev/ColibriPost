package ru.teamdroid.colibripost.ui.auth

import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_wait_code.*
import kotlinx.coroutines.launch
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.MainActivity
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.FragmentWaitCodeBinding
import ru.teamdroid.colibripost.di.viewmodel.AuthViewModel
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.other.SingleLiveData
import ru.teamdroid.colibripost.other.onFailure
import ru.teamdroid.colibripost.other.onSuccess
import ru.teamdroid.colibripost.remote.account.auth.AuthHolder
import ru.teamdroid.colibripost.remote.account.auth.AuthStates
import ru.teamdroid.colibripost.remote.core.NetworkHandler
import ru.teamdroid.colibripost.remote.core.setNetworkCallback
import ru.teamdroid.colibripost.ui.bottomnavigation.BottomNavigationFragment
import ru.teamdroid.colibripost.ui.core.BaseFragment
import ru.teamdroid.colibripost.ui.core.getColorFromResource
import javax.inject.Inject

class WaitCodeFragment : BaseFragment() {

    override val layoutId = R.layout.fragment_wait_code
    override val toolbarTitle = 0

    private var _binding: FragmentWaitCodeBinding? = null
    private val binding: FragmentWaitCodeBinding
        get() = _binding!!

    lateinit var authViewModel: AuthViewModel

    @Inject
    lateinit var authHolder: AuthHolder

    @Inject
    lateinit var networkHandler: NetworkHandler

    var networkCallbackIsInitializied = false
    lateinit var networkCallback: ConnectivityManager.NetworkCallback

    lateinit var phoneNumber: String
    lateinit var formattedPhoneNumberHint: String

    var isAuth = false
    var seconds: Int = 59
    private var countOfAttempts = 5

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

        authViewModel = viewModel {
            onSuccess<None, SingleLiveData<None>>(codeData, ::handleCheckAuthCode)
            onSuccess(progressData, ::updateRefresh)
            onFailure<SingleLiveData<Failure>>(failureData, ::handleFailure)
        }

        setUpUi()

        binding.etCode1.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showKeyboard(v)
            }
        }
        binding.etCode1.requestFocus()
    }

    private fun setUpUi() {
        base {
            ibBackstack.setOnClickListener {
                setNavigationFragment(WaitNumberFragment())
            }
        }

        arguments?.let {
            formattedPhoneNumberHint = String.format(
                getString(R.string.we_sent_you_code), it.getString(
                    FORMATTED_NUMBER_WITH_PLUS
                )
            )
            binding.tvHints.text = formattedPhoneNumberHint
            phoneNumber = it.getString(NUMBER_WITH_PLUS)!!
        }

        //GenericTextWatcher here works only for moving to next EditText when a number is entered
        //first parameter is the current EditText and second parameter is next EditText
        binding.etCode1.addTextChangedListener(AuthCodeTextWatcher(etCode1, etCode2))
        binding.etCode2.addTextChangedListener(AuthCodeTextWatcher(etCode2, etCode3))
        binding.etCode3.addTextChangedListener(AuthCodeTextWatcher(etCode3, etCode4))
        binding.etCode4.addTextChangedListener(AuthCodeTextWatcher(etCode4, etCode5))
        binding.etCode5.addTextChangedListener(AuthCodeTextWatcher(etCode5, null) { insertCode() })

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        binding.etCode1.setOnKeyListener(GenericKeyEvent(etCode1, null))
        binding.etCode2.setOnKeyListener(GenericKeyEvent(etCode2, etCode1))
        binding.etCode3.setOnKeyListener(GenericKeyEvent(etCode3, etCode2))
        binding.etCode4.setOnKeyListener(GenericKeyEvent(etCode4, etCode3))
        binding.etCode5.setOnKeyListener(GenericKeyEvent(etCode5, etCode4))

        binding.tvSendSmsCode.setOnClickListener {
            if (authHolder.authState.value == AuthStates.WAIT_FOR_CODE) {
                /*base{
                    smsBroadcast = MySMSBroadcastReceiver()
                    smsReceiverCall()
                    registerSmsReciver()
                    smsBroadcast?.initSmsListener(object : OnAuthNumberReceivedListener{
                        override fun onAuthNumberReceived(authNumber: String?) {
                            binding.etCode.setText(authNumber)
                        }
                    })
                }*/
                binding.tvSendSmsCode.visibility = View.GONE
                lifecycleScope.launch {
                    authHolder.resendCode()
                }
            }
        }
    }

    private fun refreshSendItAgainView() {
        if (_binding != null) {
            binding.tvSendSmsCode.text = String.format(
                getString(R.string.seconds),
                seconds.toString()
            )
            seconds--
            if (seconds > 0)
                Handler().postDelayed({ refreshSendItAgainView() }, 1000)
            else setBtnSendState(true)
        }
    }

    private fun setBtnSendState(isEnable: Boolean) {
        if (isEnable) {
            binding.tvSendSmsCode.isEnabled = true
            binding.tvSendSmsCode.setTextColor(requireContext().getColorFromResource(R.color.white))
            binding.tvSendSmsCode.text = getString(R.string.send_it_by_sms)
        } else {
            binding.tvSendSmsCode.isEnabled = false
            binding.btnSendAgain.setTextColor(requireContext().getColorFromResource(R.color.white))
        }
    }

    private fun startMainFragment() {
        base { setNavigationFragment(BottomNavigationFragment()) }
    }

    private fun insertCode() {
        if (networkHandler.isConnected != null) {
            isAuth = true
            lifecycleScope.launch {
                val code =
                    etCode1.text.toString() + etCode2.text.toString() + etCode3.text.toString() + etCode4.text.toString() + etCode5.text.toString()
                //Toast.makeText(requireContext(), code, Toast.LENGTH_SHORT).show()
                authViewModel.insertCode(code)
                base {
                    toolbar.visibility = View.GONE
                    lnWhiteBackStack.visibility = View.GONE
                }
            }
        } else if (!networkCallbackIsInitializied) {
            setNetworkLostUi()
            (requireActivity() as MainActivity).connectivityManager.let {
                networkCallback =
                    it.setNetworkCallback({ setNetworkAvailbleUi(true) }, ::setNetworkLostUi)
                networkCallbackIsInitializied = true
            }
        }
    }

    private fun logOut() {
        lifecycleScope.launch { authHolder.logOut() }
    }

    override fun setNetworkAvailbleUi(isCallback: Boolean) {
        lifecycleScope.launch { tvHints.text = formattedPhoneNumberHint }
    }

    override fun setNetworkLostUi() {
        lifecycleScope.launch { tvHints.text = getString(R.string.network_waiting) }
    }

    private fun handleCheckAuthCode(none: None?) {
        seconds = 0
        updateRefresh(false)
        startMainFragment()
    }

    override fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.InvalidCodeError -> {
                isAuth = false
                countOfAttempts--
                if (countOfAttempts == 0) {
                    base { setNavigationFragment(WaitNumberFragment.newInstance(true)) }
                } else tvHints.text = String.format(
                    getString(R.string.count_of_attempts),
                    countOfAttempts
                )
            }
            else -> super.handleFailure(failure)
        }
    }

    override fun updateRefresh(status: Boolean?) {
        if (status == true) binding.tvHints.text = getString(R.string.waiting)
        else binding.tvHints.text = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (networkCallbackIsInitializied)
            (requireActivity() as MainActivity).connectivityManager.unregisterNetworkCallback(
                networkCallback
            )
        if (!isAuth) logOut()
    }

    companion object {
        const val TAG = "WaitCodeFragment"
        private const val NUMBER_WITH_PLUS = "numberWithPlus"
        private const val FORMATTED_NUMBER_WITH_PLUS = "formattedNumberWithPlus"

        fun newInstance(numberWithPlus: String, formattedNumberWithPlus: String) =
            WaitCodeFragment().apply {
                arguments = bundleOf(
                    NUMBER_WITH_PLUS to numberWithPlus,
                    FORMATTED_NUMBER_WITH_PLUS to formattedNumberWithPlus
                )
            }
    }
}
