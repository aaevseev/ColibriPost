package ru.teamdroid.colibripost.ui.settings

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.synthetic.main.delete_channel_dialog.view.*
import kotlinx.android.synthetic.main.item_channel.view.*
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.ui.core.BaseAdapter
import ru.teamdroid.colibripost.ui.core.PicassoHelper

open class ChannelsAdapter(
    var deleteChannel: (idChannel: Long) -> Unit
): BaseAdapter<ChannelEntity, ChannelsAdapter.ChannelViewHolder>(
    ChannelDiffCallback()
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.item_channel, parent, false)
        return ChannelViewHolder(view, this)
    }


    class ChannelViewHolder(val root: View, val adapter: ChannelsAdapter): BaseAdapter.BaseViewHolder(root){

        override fun onBind(item: Any) {
            (item as? ChannelEntity)?.let {
                root.tvChannelName.text = item.title
                root.tvCount.text = item.memberCount.toString()
                root.btnChannelDelete.setOnClickListener {
                    adapter.showDeleteChannelDialog(root.context, item)
                }
                PicassoHelper.loadImageFile(root.context, item.photoPath, root.imgPhoto)
            }
        }
    }

    fun showDeleteChannelDialog(context: Context, item: ChannelEntity){
        val view = LayoutInflater.from(context).inflate(
            R.layout.delete_channel_dialog, null
        )
        /*val dialog = Dialog(context, R.style.DialogFullScreen)

        dialog.setContentView(view)*/

        val dialog = AlertDialog.Builder(context).setView(view)
            .create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.tvDeleteChannel.setOnClickListener {
            deleteChannel(item.chatId)
            dialog.dismiss()
        }
        view.tvCancelDelete.setOnClickListener { dialog.dismiss() }

        dialog.show()


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