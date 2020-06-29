package ru.teamdroid.colibripost.presentation.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.teamdroid.colibripost.databinding.ItemSettingMenuBinding

class SettingsAdapter(var onItemClickListener: (position: Int) -> Unit) :
    RecyclerView.Adapter<SettingsAdapter.SettingsMenuViewHolder>() {

    private var _binding: ItemSettingMenuBinding? = null
    private val binding: ItemSettingMenuBinding
        get() = _binding!!

    private val items = arrayListOf<String>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsMenuViewHolder {
        _binding =
            ItemSettingMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettingsMenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SettingsMenuViewHolder, position: Int) {
        binding.root.setOnClickListener { onItemClickListener.invoke(position) }
        binding.tvItemSettingsMenuName.text = items[position]
    }

    fun addItem(menu: Array<String>) {
        if (items.isNotEmpty()) items.clear()
        items.addAll(menu)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        _binding = null
    }

    inner class SettingsMenuViewHolder(binding: ItemSettingMenuBinding) :
        RecyclerView.ViewHolder(binding.root)
}

