package ru.teamdroid.colibripost.ui.settings.channels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import kotlinx.android.synthetic.main.channels_bottom_sheet.view.*
import kotlinx.android.synthetic.main.item_av_channel.view.*
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.ui.core.PicassoHelper
import ru.teamdroid.colibripost.ui.core.getColorFromResource
import ru.teamdroid.colibripost.ui.core.getColorState

class AvailableChannelsAdapter() :
    ru.teamdroid.colibripost.ui.core.BaseAdapter<ChannelEntity,
            AvailableChannelsAdapter.AvChannelViewHolder>(ChannelsAdapter.ChannelDiffCallback()) {

    lateinit var checked: BooleanArray


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvChannelViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_av_channel, parent, false)
        return AvChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvChannelViewHolder, position: Int) {
        print(currentList.size)
        bind(holder, getItem(position), position, checked)
    }

    override fun submitList(list: List<ChannelEntity>?) {
        checked = BooleanArray(list!!.size)
        super.submitList(null)
        super.submitList(list)
        notifyDataSetChanged()
    }

    fun bind(
        holder: AvChannelViewHolder,
        item: ChannelEntity,
        position: Int,
        checked: BooleanArray
    ) {

        setCheckedOrNotBackground(holder.root.rlMain, position, checked)

        print(currentList.size)
        holder.root.rlMain.setOnClickListener {
            checked[position] = !checked[position]

            holder.root.apply {
                setCheckedOrNotBackground(it, position, checked)

                val tempArray = checked.filter { it == true }

                if (tempArray.size == 0) {
                    rootView.btn_add_channels.isEnabled = false
                    rootView.btn_add_channels.backgroundTintList =
                        context.getColorState(R.color.accentEnabled)
                } else {
                    rootView.btn_add_channels.isEnabled = true
                    rootView.btn_add_channels.backgroundTintList =
                        context.getColorState(R.color.accent)
                }
            }
        }
        holder.onBind(item)
    }

    fun setCheckedOrNotBackground(root: View, position: Int, checked: BooleanArray) {
        root.apply {
            if (checked[position]) {
                this.background = if (position == 0) ContextCompat.getDrawable(
                    context,
                    R.drawable.bottom_accent_sheet_background
                )
                else context.getColorFromResource(R.color.accent).toDrawable()
                this.imgCheck.visibility = View.VISIBLE
                this.tvChannelName.setTextColor(root.context.getColorFromResource(R.color.white))
            } else {
                this.background = if (position == 0) ContextCompat.getDrawable(
                    context,
                    R.drawable.bottom_white_sheet_background
                )
                else context.getColorFromResource(R.color.white).toDrawable()
                this.imgCheck.visibility = View.GONE
                this.tvChannelName.setTextColor(root.context.getColorFromResource(R.color.text))
            }
        }
    }


    fun getCheckedChannels(): List<ChannelEntity> {

        val channelsPosition = mutableListOf<Int>()
        for (index in checked.indices) {
            if (checked.get(index)) channelsPosition.add(index)
        }

        val channels: MutableList<ChannelEntity> = mutableListOf()
        for (position in channelsPosition) channels.add(getItem(position))

        return channels
    }

    @JvmName("getChecked1")
    fun getChecked() = checked

    class AvChannelViewHolder(val root: View) :
        ru.teamdroid.colibripost.ui.core.BaseAdapter.BaseViewHolder(root) {

        override fun onBind(item: Any) {
            (item as? ChannelEntity)?.let {
                root.tvChannelName.text = item.title
                PicassoHelper.loadImageFile(root.context, item.photoPath, root.imgPhoto)
            }
        }

    }

}