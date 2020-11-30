package ru.teamdroid.colibripost.ui.main

import android.accounts.AccountManager.get
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.synthetic.main.post_preview_item.view.*
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.ui.core.PicassoHelper
import java.util.*

class PostAdapter(

): ru.teamdroid.colibripost.ui.core.BaseAdapter<PostEntity,
        PostAdapter.PostViewHolder>(PostDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.post_preview_item, parent, false)
        return PostViewHolder(view)
    }

    class PostViewHolder(val root: View):
            ru.teamdroid.colibripost.ui.core.BaseAdapter.BaseViewHolder(root){

        @SuppressLint("SetTextI18n")
        override fun onBind(item: Any) {
            (item as? PostEntity)?.let {
                root.tvPostTitle.text = item.text?.text ?: "null"
                PicassoHelper.loadImageFile(root.context, item.channelPhotoPath, root.ivChannelPhoto)

                val value = item.scheduleDate
                val year = value / 10000
                val month = value % 10000 / 100
                val day = value % 100
                val date: Date = Date(value * 1000L)

                val calendar = Calendar.getInstance()
                calendar.time = date
                root.tvPostDate.text = calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar.get(Calendar.MINUTE).toString()
            }
        }

    }

    class PostDiffCallback : DiffUtil.ItemCallback<PostEntity>() {
        override fun areItemsTheSame(oldItem: PostEntity, newItem: PostEntity): Boolean {
            return oldItem.chatId == newItem.chatId
        }

        override fun areContentsTheSame(oldItem: PostEntity, newItem: PostEntity): Boolean {
            return oldItem == newItem
        }

    }

}