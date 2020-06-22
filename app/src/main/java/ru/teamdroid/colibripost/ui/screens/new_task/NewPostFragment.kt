package com.srgpanov.telegrammsmm.ui.screen

import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.FragmentNewPostBinding


class NewPostFragment : Fragment() {
    private var _binding: FragmentNewPostBinding? = null
    private val binding: FragmentNewPostBinding
        get() = _binding!!
    private lateinit var viewModel: NewPostViewModel

    companion object {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[NewPostViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupView() {
        val spinnerHeight = resources.getDimension(R.dimen.spinner_item_height).toInt()
        setupCategorySpinner(spinnerHeight)
        setupPublishSpinner(spinnerHeight)
        setupDateSpinner(spinnerHeight)
    }

    private fun setupDateSpinner(spinnerHeight: Int) {
        binding.spnDate.dropDownVerticalOffset = spinnerHeight
        val timeList = getSpinnerTimeList()
        val timeAdapter: ArrayAdapter<*> = SpinnerAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            timeList
        )
        binding.spnDate.adapter = timeAdapter
    }

    private fun setupPublishSpinner(spinnerHeight: Int) {
        binding.spnPublish.dropDownVerticalOffset = spinnerHeight
        val publishList = getSpinnerPublishList()
        val publishAdapter: ArrayAdapter<*> = SpinnerAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            publishList
        )
        binding.spnPublish.adapter = publishAdapter
        binding.spnPublish.setSelection(1)
    }

    private fun setupCategorySpinner(spinnerHeight: Int) {
        val categoryList = getSpinnerCategoryList()
        val categoryAdapter: ArrayAdapter<*> = SpinnerAdapter(
            requireActivity(),
            R.layout.simple_spinner_item,
            categoryList
        )
        categoryAdapter.setDropDownViewResource(R.layout.simple_spinner_item)
        binding.spnCategory.dropDownVerticalOffset = spinnerHeight
        binding.spnCategory.adapter = categoryAdapter
        binding.spnCategory.setSelection(4)
    }

    private fun getSpinnerPublishList(): MutableList<SpinnerItem> {
        val list = mutableListOf<SpinnerItem>()
        val blue = ShapeDrawable()
        blue.shape = OvalShape()
        blue.setTint(Color.BLUE)
        list.add(SpinnerItem(blue, "Опубликовать в..."))
        list.add(SpinnerItem(blue, "Опубликовать в..."))
        return list
    }


    private fun getSpinnerTimeList(): MutableList<SpinnerItem> {
        val list = mutableListOf<SpinnerItem>()
        val blue = ShapeDrawable()
        blue.shape = OvalShape()
        blue.setTint(Color.BLUE)
        list.add(SpinnerItem(blue, "17.06.2020"))
        list.add(SpinnerItem(blue, "17.06.2020"))
        return list
    }

    private fun getSpinnerCategoryList(): MutableList<SpinnerItem> {
        val list = mutableListOf<SpinnerItem>()
        val spinnerCircleDrawable: LayerDrawable = requireActivity().resources.getDrawable(
            R.drawable.spinner_circle,
            null
        ) as LayerDrawable
        list.add(
            SpinnerItem(
                spinnerCircleDrawable.findDrawableByLayerId(R.id.spinner_green), "News"
            )
        )
        list.add(
            SpinnerItem(
                spinnerCircleDrawable.findDrawableByLayerId(R.id.spinner_blue), "Advertising"
            )
        )
        list.add(
            SpinnerItem(
                spinnerCircleDrawable.findDrawableByLayerId(R.id.spinner_orange), "Entertainment"
            )
        )
        list.add(
            SpinnerItem(
                spinnerCircleDrawable.findDrawableByLayerId(R.id.spinner_purple), "Involvement"
            )
        )
        list.add(
            SpinnerItem(
                spinnerCircleDrawable.findDrawableByLayerId(R.id.spinner_hint), "Choose category"
            )
        )
        return list
    }


}
