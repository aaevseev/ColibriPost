package ru.teamdroid.colibripost.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.channel_dropdown_item.view.*
import kotlinx.android.synthetic.main.item_av_channel.view.*
import kotlinx.android.synthetic.main.item_av_channel.view.imgCheck
import kotlinx.android.synthetic.main.item_channel.view.*
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.domain.channels.ChannelEntity
import ru.teamdroid.colibripost.domain.post.PostEntity
import ru.teamdroid.colibripost.ui.core.PicassoHelper
import ru.teamdroid.colibripost.ui.core.getColorFromResource

class ChannelChipsAdapter(context: Context,
                          val viewResource: Int,
                          val posts: List<ChannelEntity>,
                          val checked:BooleanArray,
                          private val onItemSelectedListener: (position:Int) -> Unit,
                          private val onItemUnselectedListener: (channelEntity: ChannelEntity) -> Unit):
        ArrayAdapter<ChannelEntity>(context, viewResource) {


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getCount(): Int {
        return posts.size
    }

    override fun getItem(position: Int): ChannelEntity? {
        return posts.get(position)
    }

    fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View{

        val view = LayoutInflater.from(context).inflate(R.layout.channel_dropdown_item, parent,
                false)

        val tv = view.findViewById<TextView>(R.id.title)
        val iv = view.findViewById<CircleImageView>(R.id.icon)
        val lr = view.findViewById<RelativeLayout>(R.id.lrDropdownItem)

        setCheckedOrNotBackground(lr as RelativeLayout, checked[position])

        lr.setOnClickListener {
            checked[position] = !checked[position]
            setCheckedOrNotBackground(it as RelativeLayout, checked[position])
            if(checked[position]){
                onItemSelectedListener(position)
            }else{
                onItemUnselectedListener(posts[position])
            }

        }

        tv.text = posts.get(position).title
        PicassoHelper.loadImageFile(context, posts.get(position).photoPath, iv)

        return view
    }

    fun setCheckedOrNotBackground(root:RelativeLayout , isChecked:Boolean){
        root.apply {
            if (isChecked) {
                this.imgCheck.visibility = View.VISIBLE
                this.title.setTextColor(root.context.getColorFromResource(R.color.white))
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.accent)
            } else{
                this.imgCheck.visibility = View.GONE
                this.title.setTextColor(root.context.getColorFromResource(R.color.text))
                backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
            }
        }
    }
}