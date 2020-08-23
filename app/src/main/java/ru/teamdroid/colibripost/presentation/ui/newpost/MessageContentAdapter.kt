package ru.teamdroid.colibripost.presentation.ui.newpost

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.teamdroid.colibripost.R

import ru.teamdroid.colibripost.databinding.ItemMessageContentBinding
import java.io.File

//не занимался украшательтвами адапетра, сделал просто чтобы он показывал выбранные фото
class MessageContentAdapter() :
    RecyclerView.Adapter<MessageContentAdapter.MessageContentViewHolder>() {
    private val items: MutableList<Uri> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageContentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MessageContentViewHolder(
            ItemMessageContentBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MessageContentViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
    fun setItems(paths:List<Uri>){
        items.clear()
        items+=paths
        notifyDataSetChanged()//пока просто "жёстко" обновляю данные
    }


    class MessageContentViewHolder(private val binding: ItemMessageContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Uri) {
            Glide.with(binding.root).load(item).into(binding.ivContent)
        }
    }
}
