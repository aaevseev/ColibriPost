package ru.teamdroid.colibripost.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import kotlinx.android.synthetic.main.fragment_settings.*
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.di.viewmodel.AccountViewModel
import ru.teamdroid.colibripost.domain.account.AccountEntity
import ru.teamdroid.colibripost.domain.type.Failure
import ru.teamdroid.colibripost.other.SingleLiveData
import ru.teamdroid.colibripost.other.onFailure
import ru.teamdroid.colibripost.other.onSuccess
import ru.teamdroid.colibripost.ui.auth.SignInFragment
import ru.teamdroid.colibripost.ui.bottomnavigation.BottomNavigationFragment
import ru.teamdroid.colibripost.ui.core.BaseFragment
import ru.teamdroid.colibripost.ui.core.PicassoHelper
import ru.teamdroid.colibripost.ui.core.getColorFromResource
import ru.teamdroid.colibripost.ui.core.getImageDrawable
import ru.teamdroid.colibripost.ui.settings.channels.ChannelsSettingsFragment

class SettingsFragment : BaseFragment() {


    override val layoutId = R.layout.fragment_settings

    override val toolbarTitle = R.string.settings

    private lateinit var gestureDetector: GestureDetector

    lateinit var accountViewModel: AccountViewModel

    val iconList = listOf(
        R.drawable.ic_account, R.drawable.ic_channels, R.drawable.ic_admin,
        R.drawable.ic_card, R.drawable.ic_bail, R.drawable.ic_help_circle, R.drawable.ic_quit
    )
    val blueIconList = listOf(
        R.drawable.ic_blue_account, R.drawable.ic_blue_channels,
        R.drawable.ic_blue_admin, R.drawable.ic_blue_card,
        R.drawable.ic_blue_bail, R.drawable.ic_blue_help_circle, R.drawable.ic_blue_quit
    )

    val menuFragmentList =
        listOf("", ChannelsSettingsFragment.TAG, "", "", "", "", SignInFragment.TAG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gestureDetector = GestureDetector(requireContext(), SingleTapConfirm())

        setUpUi()

        accountViewModel = viewModel {
            onSuccess<AccountEntity,
                    SingleLiveData<AccountEntity>>(accountData, ::handleAccount)
            onSuccess(progressData, ::updateRefresh)
            onFailure<SingleLiveData<Failure>>(failureData, ::handleFailure)
        }

        accountViewModel.getAccount()

    }

    @SuppressLint("ClickableViewAccessibility")
    fun setUpUi() {
        val linearList = listOf<LinearLayout>(
            lrAccountsSettings, lrChannelsSettings,
            lrManagersSettings, lrPayingSettings, lrNotificationsSettings, lrHelp, lrQuit
        )
        val textViewList = listOf<TextView>(
            tvAccounts, tvChannels, tvManagers,
            tvPaying, tvNotifications, tvHelp, tvQuit
        )
        val imageViewList = listOf<ImageView>(
            ivAccounts, ivChannels, ivManagers,
            ivPaying, ivNotifications, ivHelp, ivQuit
        )

        for (i in 0..6) {
            linearList.get(i).setOnTouchListener { v: View, motionEvent: MotionEvent ->
                onTouchMenuItem(
                    linearList.get(i), textViewList.get(i), imageViewList.get(i),
                    iconList.get(i), blueIconList.get(i), motionEvent, menuFragmentList.get(i)
                )
                return@setOnTouchListener true
            }
        }
    }

    fun onTouchMenuItem(
        linearLayout: LinearLayout, textView: TextView, imageView: ImageView,
        whiteIconId: Int, blueIconId: Int, motionEvent: MotionEvent, tag: String
    ) {
        if (gestureDetector.onTouchEvent(motionEvent)) {
            setDefaultMenuItemState(linearLayout, textView, imageView, whiteIconId = whiteIconId)
            if (tag == ChannelsSettingsFragment.TAG || tag == SignInFragment.TAG) onClickMenuItem(
                tag
            )
        } else {
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    setDefaultMenuItemState(
                        linearLayout,
                        textView,
                        imageView,
                        blueIconId = blueIconId
                    )
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    setDefaultMenuItemState(
                        linearLayout,
                        textView,
                        imageView,
                        whiteIconId = whiteIconId
                    )
                }
            }
        }
    }

    fun setDefaultMenuItemState(
        linearLayout: LinearLayout,
        textView: TextView,
        imageView: ImageView,
        whiteIconId: Int = 0,
        blueIconId: Int = 0
    ) {
        if (whiteIconId != 0) {
            linearLayout.background =
                requireActivity().getColorFromResource(R.color.accent).toDrawable()
            textView.setTextColor(requireActivity().getColorFromResource(R.color.white))
            imageView.setImageDrawable(requireActivity().getImageDrawable(whiteIconId))
        } else {
            linearLayout.background =
                requireActivity().getColorFromResource(R.color.whiteEnabled).toDrawable()
            textView.setTextColor(requireActivity().getColorFromResource(R.color.accent))
            imageView.setImageDrawable(requireActivity().getImageDrawable(blueIconId))
        }

    }

    fun onClickMenuItem(tag: String) {

        (requireParentFragment() as BottomNavigationFragment).displayFragment(tag)
    }

    fun handleAccount(accountEntity: AccountEntity?) {
        tv_user_name.text = String.format(
            getString(R.string.first_name_and_last_name),
            accountEntity!!.firstName, accountEntity.lastName
        )
        PicassoHelper.loadImageFile(requireContext(), accountEntity.photoPath, iv_user_image)
        updateRefresh(false)
    }

    override fun updateRefresh(status: Boolean?) {
        if (status == true) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    companion object {
        const val TAG = "SettingsFragment"
    }

    private class SingleTapConfirm : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(event: MotionEvent?): Boolean {
            return true
        }
    }
}