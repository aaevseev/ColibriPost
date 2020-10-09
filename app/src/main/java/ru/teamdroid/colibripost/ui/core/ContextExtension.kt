package ru.teamdroid.colibripost.ui.core

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

fun Context.getColorFromResource(idColor: Int): Int {
    return ContextCompat.getColor(this, idColor)
}

fun Context.getColorState(idColor: Int): ColorStateList?{
    return ContextCompat.getColorStateList(this, idColor)
}

fun Context.getImageDrawable(idImage: Int): Drawable? {
    return ContextCompat.getDrawable(this, idImage)
}