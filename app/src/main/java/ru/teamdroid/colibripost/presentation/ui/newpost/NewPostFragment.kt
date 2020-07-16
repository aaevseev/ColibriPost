package ru.teamdroid.colibripost.presentation.ui.newpost

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.srgpanov.telegrammsmm.ui.screen.SpinnerAdapter
import com.srgpanov.telegrammsmm.ui.screen.SpinnerItem
import ru.teamdroid.colibripost.App
import ru.teamdroid.colibripost.R
import ru.teamdroid.colibripost.databinding.FragmentNewPostBinding
import ru.teamdroid.colibripost.presentation.ui.newpost.CalendarDialogFragment.Companion.KEY_DAY
import ru.teamdroid.colibripost.presentation.ui.newpost.CalendarDialogFragment.Companion.KEY_MONTH
import ru.teamdroid.colibripost.presentation.ui.newpost.CalendarDialogFragment.Companion.KEY_YEAR
import ru.teamdroid.colibripost.presentation.ui.newpost.CalendarDialogFragment.Companion.REQUEST_DATE
import ru.teamdroid.colibripost.presentation.ui.newpost.TimeDialogFragment.Companion.KEY_HOUR
import ru.teamdroid.colibripost.presentation.ui.newpost.TimeDialogFragment.Companion.KEY_MUNUTE
import ru.teamdroid.colibripost.presentation.ui.newpost.TimeDialogFragment.Companion.REQUEST_TIME
import javax.inject.Inject


class NewPostFragment : Fragment(), FragmentResultListener {
    private var _binding: FragmentNewPostBinding? = null
    private val binding: FragmentNewPostBinding
        get() = _binding!!

    private lateinit var takePicture: ActivityResultLauncher<Intent>

    private val adapter:MessageContentAdapter by lazy { MessageContentAdapter() }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel: NewPostViewModel by viewModels { viewModelFactory }
    val publishAdapter by lazy {
        ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_spinner_item
        )
    }

    companion object {
        const val TAG = "NewPostFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectNewPostFragment(this)
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
        setupViews()
        observeViewModel()
        childFragmentManager.setFragmentResultListener(REQUEST_DATE, viewLifecycleOwner, this)
        childFragmentManager.setFragmentResultListener(REQUEST_TIME, viewLifecycleOwner, this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        when (requestKey) {
            REQUEST_DATE -> {
                val year = result.getInt(KEY_YEAR)
                val month = result.getInt(KEY_MONTH)
                val dayOfMonth = result.getInt(KEY_DAY)
                val day = "${formatTime(dayOfMonth)}.${formatTime(month+1)}.$year"
                binding.tvDate.text = day
                viewModel.setPublishDay(day)
            }
            REQUEST_TIME -> {
                val hour = formatTime(result.getInt(KEY_HOUR))
                val minute = formatTime(result.getInt(KEY_MUNUTE))
                val time = "$hour:$minute"
                binding.tvTime.text = time
                viewModel.setPublishTime(time)
            }
        }
    }

    private fun setupViews() {
        setupSpinners()
        setupListeners()
        setupRv()

        binding.tvDate.text=viewModel.publishDay.value
        binding.tvTime.text=viewModel.publishTime.value

    }

    private fun setupRv() {
        binding.rvContent.adapter=adapter
        binding.rvContent.setHasFixedSize(true)
    }

    private fun observeViewModel() {
        viewModel.chatList.observe(viewLifecycleOwner) { list ->
            val publishList = mutableListOf<String>()
            list.forEach { chat ->
                publishList.add(chat.title)
            }
            publishAdapter.addAll(publishList)
        }
        viewModel.takeBitmap.observe(viewLifecycleOwner){bitmap->
            if (bitmap != null) {
                Log.d("NewPostFragment", "observeViewModel: ${bitmap.width} ${bitmap.height}")

            }else{
                Log.e(TAG, "observeViewModel: take bitmap null" )
            }
        }
        viewModel.inputFiles.observe(viewLifecycleOwner){pathList->
            Log.d("NewPostFragment", "observeViewModel: $pathList")
            setupRvVisibility(pathList)
            adapter.setItems(pathList)
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupRvVisibility(pathList: List<Uri>) {
        if (pathList.isEmpty()) {
            binding.rvContent.visibility = View.GONE
        } else {
            binding.rvContent.visibility = View.VISIBLE
        }
    }

    private fun setupListeners() {
        binding.etPost.doAfterTextChanged { editable ->
            viewModel.setPostText(editable.toString())
        }
        binding.btnSendPost.setOnClickListener {
            viewModel.sendPost()
        }
        binding.tvDate.setOnClickListener {
            val dialogFragment = CalendarDialogFragment()
            dialogFragment.show(childFragmentManager, CalendarDialogFragment.TAG)
        }
        binding.tvTime.setOnClickListener {
            val dialogFragment = TimeDialogFragment()
            dialogFragment.show(childFragmentManager, TimeDialogFragment.TAG)
        }
        takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val selectedImage = it.data?.getData()

            Log.d("NewPostFragment", "setupListeners: $selectedImage")
            if (selectedImage != null) {
                viewModel.onFileChosen(selectedImage)
            }else {
                Log.e(TAG, "setupListeners: selectedImage null" )
            }

        }

        binding.btnClip.setOnClickListener {
            val takePictureIntent: Intent = Intent(Intent.ACTION_PICK)
            takePictureIntent.type = "image/*"
            takePicture.launch(takePictureIntent)
        }
    }

    private fun formatTime(time: Int): String = if (time < 10) "0$time" else time.toString()

    private fun setupSpinners() {
        val spinnerHeight = resources.getDimension(R.dimen.spinner_item_height).toInt()
        setupCategorySpinner(spinnerHeight)
        setupPublishSpinner(spinnerHeight)
    }


    //спиннер нужно переделать
    private fun setupPublishSpinner(spinnerHeight: Int) {
        binding.spinnerPublish.dropDownVerticalOffset = spinnerHeight
        binding.spinnerPublish.adapter = publishAdapter
        binding.spinnerPublish.setSelection(1)
        binding.spinnerPublish.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val item = viewModel.chatList.value?.get(position)
                    item?.let { viewModel.setPublishChat(it) }
                }
            }
    }

    //спиннер нужно переделать
    private fun setupCategorySpinner(spinnerHeight: Int) {
        val categoryList = getSpinnerCategoryList()
        val categoryAdapter = SpinnerAdapter(
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
