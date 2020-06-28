package ru.teamdroid.colibripost.presentation.ui.settings

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.common.OnItemClickListener
import ru.teamdroid.colibripost.common.addOnItemClickListener
import ru.teamdroid.colibripost.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding!!

    private val userName = "Chuck Norris"
    private val userSubscription = 31

    private lateinit var settingsAdapter: SettingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        settingsAdapter = SettingsAdapter()
        settingsAdapter.addItem(requireActivity().resources.getStringArray(R.array.settings_menu))
        with(binding.rvSettingsMenu) {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = settingsAdapter
            addOnItemClickListener(object : OnItemClickListener {
                override fun onItemClicked(position: Int) {
                    Toast.makeText(activity, "Pressed menu $position", Toast.LENGTH_SHORT).show()
                }
            })
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this)
            .load(R.drawable.chuck_norris)
            .placeholder(R.drawable.user_image_placeholder)
            .fitCenter()
            .circleCrop()
            .into(binding.ivUserImage)

        binding.tvUserName.text = userName
        binding.tvUserSubscription.text = "$userSubscription день"
        binding.tvUserSubscription.background = userSubscriptionStatusBackground(userSubscription)
    }

    private fun userSubscriptionStatusBackground(userSubscription: Int): Drawable =
        when {
            userSubscription > 15 -> resources.getDrawable(
                R.drawable.user_subscription_ok_background,
                null
            )
            userSubscription in 8..15 -> resources.getDrawable(
                R.drawable.user_subscription_attention_background,
                null
            )
            else -> resources.getDrawable(R.drawable.user_subscription_none_background, null)
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val TAG = "SettingsFragment"
    }
}