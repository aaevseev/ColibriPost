package ru.teamdroid.colibripost.ui.core

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<Item : Any, VH : BaseAdapter.BaseViewHolder>(
    diff: DiffUtil.ItemCallback<Item>
) : ListAdapter<Item, VH>(diff) {


    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(getItem(position))
    }

    abstract class BaseViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        abstract fun onBind(item: Any)
    }

    fun clear() {
        submitList(listOf())
        notifyDataSetChanged()
    }

}