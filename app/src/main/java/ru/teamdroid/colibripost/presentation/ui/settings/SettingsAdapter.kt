package ru.teamdroid.colibripost.presentation.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_setting_menu.view.*
import ru.teamdroid.colibripost.R

class SettingsAdapter : RecyclerView.Adapter<SettingsAdapter.SettingsMenuViewHolder>() {

    private val items = arrayListOf<String>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsMenuViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_setting_menu, parent, false)
        return SettingsMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettingsMenuViewHolder, position: Int) {
        holder.itemView.tv_item_settings_menu_name.text = items[position]
    }

    fun addItem(menu: Array<String>) {
        if (items.isNotEmpty()) items.clear()
        items.addAll(menu)
    }

    class SettingsMenuViewHolder(view: View) : RecyclerView.ViewHolder(view)
}

