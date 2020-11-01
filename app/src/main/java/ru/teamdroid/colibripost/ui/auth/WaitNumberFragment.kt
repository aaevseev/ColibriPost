package ru.teamdroid.colibripost.ui.auth

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_wait_code.*
import kotlinx.android.synthetic.main.fragment_wait_number.*
import kotlinx.android.synthetic.main.fragment_wait_number.tvHints
import kotlinx.coroutines.launch
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.MainActivity
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.FragmentWaitNumberBinding
import ru.teamdroid.colibripost.di.viewmodel.AuthViewModel
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.domain.type.None
import ru.teamdroid.colibripost.other.SingleLiveData
import ru.teamdroid.colibripost.other.onFailure
import ru.teamdroid.colibripost.other.onSuccess
import ru.teamdroid.colibripost.remote.account.auth.AuthHolder
import ru.teamdroid.colibripost.remote.account.auth.AuthStates.*
import ru.teamdroid.colibripost.remote.core.NetworkHandler
import ru.teamdroid.colibripost.remote.core.setNetworkCallback
import ru.teamdroid.colibripost.ui.core.BaseFragment
import ru.teamdroid.colibripost.ui.core.getColorFromResource
import javax.inject.Inject


class WaitNumberFragment : BaseFragment() {

    @Inject
    lateinit var authHolder: AuthHolder

    @Inject
    lateinit var networkHandler: NetworkHandler

    lateinit var authViewModel: AuthViewModel

    override val layoutId = R.layout.fragment_wait_number
    override val toolbarTitle = 0
    private var _binding: FragmentWaitNumberBinding? = null
    private val binding: FragmentWaitNumberBinding
        get() = _binding!!

    var isAuth = false
    var networkCallbackIsInitializied = false

    lateinit var networkCallback: ConnectivityManager.NetworkCallback

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
                    if (isAuth) startWaitCodeFragment()
                }
                WAIT_FOR_PASSWORD -> Log.d(
                    "WaitNumberFragment",
                    "onViewCreated: state WAIT_FOR_PASSWORD"
                )
                AUTHENTICATED -> Log.d("WaitNumberFragment", "onViewCreated: state AUTHENTICATED")
                UNKNOWN -> Log.d("WaitNumberFragment", "onViewCreated: state UNKNOWN")
            }
        }

        authViewModel = viewModel {
            onSuccess<None, SingleLiveData<None>>(insertPhoneData, ::handleInsertPhoneNumber)
            onSuccess(progressData, ::updateRefresh)
            onFailure<SingleLiveData<Failure>>(failureData, ::handleFailure)
        }

        binding.etPhone.registerCarrierNumberEditText(binding.editTextCarrierNumber)
        binding.etPhone.setPhoneNumberValidityChangeListener(object :
            CountryCodePicker.PhoneNumberValidityChangeListener {
            override fun onValidityChanged(isValidNumber: Boolean) {
                setBtnOkState(isValidNumber)
            }
        })

        binding.btnOk.setOnClickListener {
            if (networkHandler.isConnected != null)
                insertPhoneNumber()
            else if(!networkCallbackIsInitializied) {
                setNetworkLostUi()
                (requireActivity() as MainActivity).connectivityManager.let {
                    networkCallback = it.setNetworkCallback({ setNetworkAvailbleUi(true) }, ::setNetworkLostUi)
                    networkCallbackIsInitializied = true
                }
            }
        }

        base {
            toolbar.visibility = View.VISIBLE
            lnWhiteBackStack.visibility = View.VISIBLE
            ibBackstack.setOnClickListener {
                setNavigationFragment(SignInFragment())
            }
        }

        binding.editTextCarrierNumber.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) { showKeyboard(v) }
        }
        binding.editTextCarrierNumber.requestFocusFromTouch()
    }


    fun insertPhoneNumber(){
        if (authHolder.authState.value == WAIT_FOR_NUMBER) {
            isAuth = true
            authViewModel.insertPhoneNumber(binding.etPhone.fullNumberWithPlus)
        }
        Log.d("WaitNumberFragment", "onViewCreated: click ${authHolder.authState.value}")
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
        base { setNavigationFragment(
            WaitCodeFragment.newInstance(
                binding.etPhone.fullNumberWithPlus,
                binding.etPhone.formattedFullNumber
            )
        )}
    }

    fun logOut(){
        lifecycleScope.launch { authHolder.logOut() }
    }

    override fun setNetworkAvailbleUi(isCallback: Boolean) {
        lifecycleScope.launch { tvHints.text = getString(R.string.will_send_you_code)}
    }

    override fun setNetworkLostUi() {
        lifecycleScope.launch { tvHints.text = getString(R.string.network_waiting) }

    }

    fun handleInsertPhoneNumber(none: None?){
        updateRefresh(false)
    }

    override fun handleFailure(failure: Failure?) {
        when(failure){
            is Failure.NumberHasBeenBannedError -> Toast.makeText(requireContext(), "Вы забанены", Toast.LENGTH_SHORT).show()
            else -> super.handleFailure(failure)
        }
    }

    override fun updateRefresh(status: Boolean?) {
        if(status == true) binding.tvHints.text = getString(R.string.waiting)
        else binding.tvHints.text = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("WaitNumberFragment", "OnDestroyView")
        _binding = null
        if(networkCallbackIsInitializied)
            (requireActivity() as MainActivity).connectivityManager.unregisterNetworkCallback(
                networkCallback
            )
        if(!isAuth) logOut()
    }


    companion object {

        const val TAG = "WaitNumberFragment"
        private const val IS_CODE_NEED = "isCodeNeed"

        fun newInstance(isCodeNeed: Boolean) = WaitNumberFragment().apply {
            arguments = bundleOf(IS_CODE_NEED to isCodeNeed)
        }
    }

}
