package ru.teamdroid.colibripost.ui.core

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Picasso

object PicassoHelper {

    fun loadImageFile(context: Context, path: String, iv: ImageView) {
        Picasso.with(context)
            .load(java.io.File(path))
            .into(iv)
    }

}