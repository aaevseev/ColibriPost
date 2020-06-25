package ru.teamdroid.colibripost.presentation.ui.newpost

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.teamdroid.colibripost.R

class NewPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_post, container, false)
    }

    companion object {

        const val TAG = "NewPostFragment"
    }
}