package ru.teamdroid.colibripost.ui.screens.new_task

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
import com.srgpanov.telegrammsmm.ui.screen.SpinnerAdapter
import com.srgpanov.telegrammsmm.ui.screen.SpinnerItem
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.FragmentNewPostBinding


class NewPostFragment : Fragment() {
    private var _binding: FragmentNewPostBinding? = null
    private val binding: FragmentNewPostBinding
        get() = _binding!!


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

    //спиннер нужно переделать
    private fun setupDateSpinner(spinnerHeight: Int) {
        binding.spinnerDate.dropDownVerticalOffset = spinnerHeight
        val timeList = getSpinnerTimeList()
        val timeAdapter: ArrayAdapter<*> = SpinnerAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            timeList
        )
        binding.spinnerDate.adapter = timeAdapter
    }

    //спиннер нужно переделать
    private fun setupPublishSpinner(spinnerHeight: Int) {
        binding.spinnerPublish.dropDownVerticalOffset = spinnerHeight
        val publishList = getSpinnerPublishList()
        val publishAdapter: ArrayAdapter<*> = SpinnerAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            publishList
        )
        binding.spinnerPublish.adapter = publishAdapter
        binding.spinnerPublish.setSelection(1)
    }

    //спиннер нужно переделать
    private fun setupCategorySpinner(spinnerHeight: Int) {
        val categoryList = getSpinnerCategoryList()
        val categoryAdapter: ArrayAdapter<*> = SpinnerAdapter(
            requireActivity(),
            R.layout.simple_spinner_item,
            categoryList
        )
        categoryAdapter.setDropDownViewResource(R.layout.simple_spinner_item)
        binding.spinnerCategory.dropDownVerticalOffset = spinnerHeight
        binding.spinnerCategory.adapter = categoryAdapter
        binding.spinnerCategory.setSelection(4)
    }

    //Мок данные для спиннера
    private fun getSpinnerPublishList(): MutableList<SpinnerItem> {
        val list = mutableListOf<SpinnerItem>()
        val blue = ShapeDrawable()
        blue.shape = OvalShape()
        blue.setTint(Color.BLUE)
        list.add(SpinnerItem(blue, "Опубликовать в..."))
        list.add(SpinnerItem(blue, "Опубликовать в..."))
        return list
    }

    //Мок данные для спиннера
    private fun getSpinnerTimeList(): MutableList<SpinnerItem> {
        val list = mutableListOf<SpinnerItem>()
        val blue = ShapeDrawable()
        blue.shape = OvalShape()
        blue.setTint(Color.BLUE)
        list.add(SpinnerItem(blue, "17.06.2020"))
        list.add(SpinnerItem(blue, "17.06.2020"))
        return list
    }

    //Мок данные для спиннера
    private fun getSpinnerCategoryList(): MutableList<SpinnerItem> {
        val drawable: LayerDrawable = requireActivity().resources.getDrawable(
            R.drawable.spinner_circle,
            null
        ) as LayerDrawable
        return mutableListOf(
            SpinnerItem(drawable.findDrawableByLayerId(R.id.spinner_green), "News"),
            SpinnerItem(drawable.findDrawableByLayerId(R.id.spinner_blue), "Advertising"),
            SpinnerItem(drawable.findDrawableByLayerId(R.id.spinner_orange), "Entertainment"),
            SpinnerItem(drawable.findDrawableByLayerId(R.id.spinner_purple), "Involvement"),
            SpinnerItem(drawable.findDrawableByLayerId(R.id.spinner_hint), "Choose category")
        )
    }


}
