package ru.teamdroid.colibripost.ui.main

import android.graphics.drawable.Drawable
import android.net.Uri
import com.pchmn.materialchips.model.ChipInterface

class ChannelChip(
        private val id:String,
        private val avatarUri: Uri,
        private val name: String
): ChipInterface {

    override fun getId(): Any {
        return id
    }

    override fun getAvatarUri(): Uri {
        return avatarUri
    }

    override fun getAvatarDrawable(): Drawable? {
        return null
    }

    override fun getLabel(): String {
        return name
    }

    override fun getInfo(): String {
        return ""
    }
}