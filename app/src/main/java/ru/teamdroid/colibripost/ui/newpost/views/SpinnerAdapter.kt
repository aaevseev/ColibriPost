package com.srgpanov.telegrammsmm.ui.screen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.SimpleSpinnerItemBinding
import ru.teamdroid.colibripost.databinding.SimpleSpinnerTitleItemBinding


class SpinnerAdapter(
    context: Context,
    resource: Int,
    private val list: MutableList<SpinnerItem>
) : ArrayAdapter<SpinnerItem>(context, resource, list) {

    var defaultItem = list.size - 1

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getDropView(position, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getTitleVIew(position, parent)
    }

    private fun getTitleVIew(position: Int, parent: ViewGroup): View {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = SimpleSpinnerTitleItemBinding.inflate(inflater, parent, false)
        binding.tvSpinnerItem.text = list[position].text
        binding.ivSpinnerCircle.setImageDrawable(list[position].color)
        if (position == defaultItem) {
            binding.tvSpinnerItem.setTextColor(context.resources.getColor(R.color.hint))
        }

        return binding.root
    }


    fun getDropView(
        position: Int,
        parent: ViewGroup?
    ): View {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = SimpleSpinnerItemBinding.inflate(inflater, parent, false)
        binding.tvSpinnerItem.text = list[position].text
        binding.ivSpinnerCircle.setImageDrawable(list[position].color)

        return binding.root
    }

    override fun getCount(): Int {
        return list.size - 1
    }
}