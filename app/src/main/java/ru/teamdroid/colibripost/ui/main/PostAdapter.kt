package ru.teamdroid.colibripost.ui.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.synthetic.main.post_item.view.*
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.ui.core.BaseAdapter
import ru.teamdroid.colibripost.ui.core.PicassoHelper
import ru.teamdroid.colibripost.ui.core.getColorState
import ru.teamdroid.colibripost.ui.core.getImageDrawable
import java.util.*

class PostAdapter(

): ru.teamdroid.colibripost.ui.core.BaseAdapter<PostEntity,
        BaseAdapter.BaseViewHolder>(PostDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if(viewType == 1) PostViewHolder(LayoutInflater.from(parent.context)
                            .inflate(R.layout.post_item, parent, false))
                else PostBottomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.post_bottom_item, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bind(holder, position)
    }

    override fun submitList(list: List<PostEntity>?) {
        super.submitList(null)
        super.submitList(list)
        notifyDataSetChanged()
    }

    fun bind(holder: BaseViewHolder, position:Int){
        if(currentList.size > 2) {
            when(position){
                0 -> {
                    holder.view.igTimeLineUp.visibility = View.INVISIBLE
                    holder.view.background = holder.view.context.getImageDrawable(R.drawable.settings_menu_background)
                }
                currentList.size - 2 -> {
                    holder.view.igTimeLineDown.visibility = View.INVISIBLE
                }
            }
        } else {
            holder.view.igTimeLineUp.visibility = View.INVISIBLE
            holder.view.igTimeLineDown.visibility = View.INVISIBLE
        }
        holder.onBind(currentList[position])
    }

    class PostViewHolder(val root: View):BaseAdapter.BaseViewHolder(root){

        @SuppressLint("SetTextI18n")
        override fun onBind(item: Any) {
            (item as? PostEntity)?.let {
                root.tvPostTitle.text = item.text?.text ?: "null"
                PicassoHelper.loadImageFile(root.context, item.channelPhotoPath, root.ivChannelPhoto)

                val value = item.scheduleDate
                val date = Date(value * 1000L)

                val calendar = Calendar.getInstance()
                calendar.time = date
                root.tvPostDate.text = calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar.get(Calendar.MINUTE).toString()
            }
        }

    }

    class PostBottomViewHolder(val root: View):BaseAdapter.BaseViewHolder(root){


        override fun onBind(item: Any) {
            //Nont needed
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            currentList.size-1 -> 2
            else -> 1
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<PostEntity>() {
        override fun areItemsTheSame(oldItem: PostEntity, newItem: PostEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PostEntity, newItem: PostEntity): Boolean {
            return oldItem == newItem
        }

    }

}