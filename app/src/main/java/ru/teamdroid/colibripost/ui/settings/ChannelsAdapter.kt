package ru.teamdroid.colibripost.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.synthetic.main.item_channel.view.*
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.ui.core.BaseAdapter
import ru.teamdroid.colibripost.ui.core.PicassoHelper

class ChannelsAdapter: BaseAdapter<ChannelEntity, ChannelsAdapter.ChannelViewHolder>(
    ChannelDiffCallback()
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.item_channel, parent, false)
        return ChannelViewHolder(view)
    }


    class ChannelViewHolder(val root: View): BaseAdapter.BaseViewHolder(root){

        override fun onBind(item: Any) {
            (item as? ChannelEntity)?.let {
                root.tvChannelName.text = item.title
                root.tvCount.text = item.memberCount.toString()

                PicassoHelper.loadImageFile(root.context, item.photoPath, root.imgPhoto)
            }
        }
    }

    class ChannelDiffCallback: DiffUtil.ItemCallback<ChannelEntity>(){
        override fun areItemsTheSame(oldItem: ChannelEntity, newItem: ChannelEntity): Boolean {
            return oldItem.chatId == newItem.chatId
        }

        override fun areContentsTheSame(oldItem: ChannelEntity, newItem: ChannelEntity): Boolean {
            return oldItem == newItem
        }
    }




}