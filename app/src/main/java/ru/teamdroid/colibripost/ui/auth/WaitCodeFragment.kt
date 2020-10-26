package ru.teamdroid.colibripost.ui.auth

import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_wait_code.*
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.TdApi
import ru.teamdroid.colibripost.App
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

    private var mySMSBroadcastReceiver: MySMSBroadcastReceiver? = null

    @Inject
    lateinit var authHolder: AuthHolder

    lateinit var phoneNumber: String

    var isAuth = false
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

        base{
            smsBroadcast.initSmsListener(object : OnAuthNumberReceivedListener{
                override fun onAuthNumberReceived(authNumber: String?) {
                    binding.etCode.setText(authNumber)
                }
            })
        }
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
    }

    fun setUpUi(){
        setBtnSendState(false)
        refreshSendItAgainView()

        base {
            ibBackstack.setOnClickListener {
                setNavigationFragment(WaitNumberFragment())
            }
        }

        arguments?.let {
            binding.tvHints.text = String.format(
                getString(R.string.we_sent_you_code), it.getString(
                    FORMATTED_NUMBER_WITH_PLUS
                )
            )
            phoneNumber = it.getString(NUMBER_WITH_PLUS)!!
        }

        binding.etCode.setOnKeyListener { v, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                        isAuth = true
                        lifecycleScope.launch {
                            authViewModel.insertCode(binding.etCode.text.toString())
                            base {
                                toolbar.visibility = View.GONE
                                lnWhiteBackStack.visibility = View.GONE
                            }
                        }
                    }
                }
            }
            false
        }

        binding.etCode.addTextChangedListener { object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    TODO("Not yet implemented")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(count == 5) {
                        isAuth = true
                        lifecycleScope.launch {
                            authViewModel.insertCode(binding.etCode.text.toString())
                            base {
                                toolbar.visibility = View.GONE
                                lnWhiteBackStack.visibility = View.GONE }
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    TODO("Not yet implemented")
                }
            }
        }

        binding.btnSendAgain.setOnClickListener {
            if (authHolder.authState.value == AuthStates.WAIT_FOR_CODE) {

                lifecycleScope.launch {
                    //authHolder.insertPhoneNumber(phoneNumber, true)
                    authHolder.resendCode()
                }
                seconds = 59
                refreshSendItAgainView()
                setBtnSendState(false)
            }
        }
    }



    private fun refreshSendItAgainView(){
        if(_binding != null){
            binding.btnSendAgain.text = String.format(
                getString(R.string.seconds),
                seconds.toString()
            )
            seconds--
            if(seconds > 0)
                Handler().postDelayed({ refreshSendItAgainView() }, 1000)
            else setBtnSendState(true)
        }
    }

    private fun setBtnSendState(isEnable: Boolean) {
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

    private fun logOut(){
        lifecycleScope.launch { authHolder.logOut() }
    }

    private fun handleCheckAuthCode(none: None?){
        seconds = 0
        updateRefresh(false)
        startMainFragment()
    }

    override fun handleFailure(failure: Failure?) {
        when(failure){
            is Failure.InvalidCodeError -> tvHints.text = getString(R.string.invalid_code_oops)
            else -> super.handleFailure(failure)
        }
    }

    override fun updateRefresh(status: Boolean?) {
        if(status == true) tvHints.text = getString(R.string.checking)
        else tvHints.text = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if(!isAuth) logOut()
    }

    companion object {
        const val TAG = "WaitCodeFragment"
        private const val NUMBER_WITH_PLUS = "numberWithPlus"
        private const val FORMATTED_NUMBER_WITH_PLUS = "formattedNumberWithPlus"

        fun newInstance(numberWithPlus: String, formattedNumberWithPlus: String) = WaitCodeFragment().apply {
            arguments = bundleOf(
                NUMBER_WITH_PLUS to numberWithPlus,
                FORMATTED_NUMBER_WITH_PLUS to formattedNumberWithPlus
            )
        }
    }
}
