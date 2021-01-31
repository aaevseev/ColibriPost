package ru.teamdroid.colibripost.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.post_item.view.*
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.ui.core.BaseAdapter
import ru.teamdroid.colibripost.ui.core.PicassoHelper
import ru.teamdroid.colibripost.ui.core.getImageDrawable
import java.util.*

class PostAdapter(
    val deleteSchedulePost:(post:PostEntity) -> Unit,
    val duplicateSchedulePost:(post:PostEntity) -> Unit,
): ru.teamdroid.colibripost.ui.core.BaseAdapter<PostEntity,
        BaseAdapter.BaseViewHolder>(PostDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if(viewType == 1) PostViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.post_item, parent, false), this)
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

    fun bind(holder: BaseViewHolder, position: Int){
        setTimeLineEdges(holder, position)
        holder.onBind(currentList[position])
    }

    fun setTimeLineEdges(holder: BaseViewHolder, position: Int){
        if(currentList.size > 2) {
            when(position){
                0 -> {
                    holder.view.igTimeLineUp.visibility = View.INVISIBLE
                    holder.view.igTimeLineDown.visibility = View.VISIBLE
                    holder.view.background = holder.view.context.getImageDrawable(R.drawable.settings_menu_background)
                }
                currentList.size - 2 -> {
                    holder.view.igTimeLineDown.visibility = View.INVISIBLE
                }
                else -> {
                    if (position != currentList.size - 1){
                        holder.view.igTimeLineUp.visibility = View.VISIBLE
                        holder.view.igTimeLineDown.visibility = View.VISIBLE
                    }
                }
            }
        } else if(position != currentList.size - 1){
            holder.view.igTimeLineUp.visibility = View.INVISIBLE
            holder.view.igTimeLineDown.visibility = View.INVISIBLE
        }
    }

    class PostViewHolder(val root: View, val adapter: PostAdapter):BaseAdapter.BaseViewHolder(root){

        @SuppressLint("SetTextI18n")
        override fun onBind(item: Any) {
            (item as? PostEntity)?.let {
                root.tvPostTitle.text = item.text
                PicassoHelper.loadImageFile(root.context, item.channelPhotoPath, root.ivChannelPhoto)

                val value = item.scheduleDate
                val date = Date(value * 1000L)

                val calendar = Calendar.getInstance()
                calendar.time = date
                val minute = calendar.get(Calendar.MINUTE)
                root.tvPostDate.text = calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" + if (minute < 10) "0".plus(minute.toString()) else minute
                root.ivMenuButton.setOnClickListener {
                    val popupwindow = popupDisplay(root.context, item)
                    popupwindow.showAsDropDown(root, convertDipToPixels(167F), convertDipToPixels(-73F))
                }
            }
        }

        fun popupDisplay(context: Context, post: PostEntity): PopupWindow { // disply designing your popoup window
            val popupWindow = PopupWindow(context) // inflet your layout or diynamic add view
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.post_popup_menu, null)
            (view.findViewById<View>(R.id.btnDeletePost) as MaterialButton).setOnClickListener {
                popupWindow.dismiss()
                adapter.deleteSchedulePost(post)
            }
            (view.findViewById<View>(R.id.btnClonePost) as MaterialButton).setOnClickListener {
                popupWindow.dismiss()
                adapter.duplicateSchedulePost(post)
            }
            popupWindow.setFocusable(true)
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.post_menu_background))
            popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT)
            popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT)
            popupWindow.setContentView(view)
            return popupWindow
        }

        fun convertDipToPixels(dips: Float): Int {
            return (dips * root.getResources().getDisplayMetrics().density + 0.5f).toInt()
        }
    }

    class PostBottomViewHolder(val root: View):BaseAdapter.BaseViewHolder(root){


        override fun onBind(item: Any) {
            //Nont needed
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            currentList.size - 1 -> 2
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