package com.example.healthconnect.codelab.ui.personaInformation

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.FragmentPersonalInformationDialogBinding
import com.example.healthconnect.codelab.utils.ViewUtils
import com.example.healthconnect.codelab.utils.onError
import com.example.healthconnect.codelab.utils.resetColors
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDate

@AndroidEntryPoint
class PersonalInformationDialogFragment : DialogFragment() {

    private var _binding: FragmentPersonalInformationDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PersonalInformationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalInformationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initViewModel()
    }

    private fun initViews() = binding.apply {
        val width = resources.displayMetrics.widthPixels*0.9
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        requireDialog().window?.setLayout(width.toInt(), height)

        etHeight.filters = arrayOf(IntegerMinMaxFilter(0, 250))
        etWeight.filters = arrayOf(DecimalMinMaxFilter(0.0, 300.0))

        val genders = arrayOf(getString(R.string.male), getString(R.string.female), getString(R.string.non_binary),
            getString(R.string.gender_fluid), getString(R.string.dont_know))
        autoCompleteTextView.setAdapter(ArrayAdapter(requireContext(), R.layout.dropdown_item, genders))

        btnBirthdate.setOnClickListener {
            val date = viewModel.birthdate ?: LocalDate.now()
            DatePickerDialog(
                requireContext(),
                {_,year,month,day ->
                    viewModel.birthdate = LocalDate.of(year, month+1, day)
                    btnBirthdate.text = viewModel.birthdate.toString()
                },
                date.year,
                date.monthValue-1,
                date.dayOfMonth
            ).let {
                it.datePicker.minDate = Instant.now().minusSeconds((60).toLong()*60*24*365*100).toEpochMilli()
                it.datePicker.maxDate = Instant.now().toEpochMilli()
                it.show()
            }
        }

        btnRunningDate.setOnClickListener {
            val date = viewModel.runningDate ?: LocalDate.now()
            DatePickerDialog(
                requireContext(),
                {_,year,month,day ->
                    viewModel.runningDate = LocalDate.of(year, month+1, day)
                    btnRunningDate.text = viewModel.runningDate.toString()
                },
                date.year,
                date.monthValue-1,
                date.dayOfMonth
            ).let {
                it.datePicker.minDate = Instant.now().minusSeconds((60).toLong()*60*24*365*100).toEpochMilli()
                it.datePicker.maxDate = Instant.now().toEpochMilli()
                it.show()
            }
        }

        btnSave.setOnClickListener {
            binding.apply {
                var completed = true
                listOf(etHeight, etWeight).forEach {
                    if (it.text.isEmpty()) {
                        completed = false
                        it.onError()
                    }
                }
                if (viewModel.birthdate == null) {
                    completed = false
                    ViewUtils.showToast(requireContext(), R.string.select_date)
                } else if (viewModel.runningDate == null) {
                    completed = false
                    ViewUtils.showToast(requireContext(), R.string.select_date)
                }

                if (completed) {
                    viewModel.apply {
                        val attributes = personalInformation.value!!.let {
                            it.gender = autoCompleteTextView.text.toString()
                            it.weight = etWeight.text.toString().toDouble()
                            it.height = etHeight.text.toString().toInt()
                            it.birthdate = birthdate!!
                            it.runningDate = runningDate!!
                            it
                        }
                        updatePersonalInformation(googleId.value!!, attributes)
                    }
                }
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        listOf(etHeight, etWeight).forEach {
            it.addTextChangedListener { text ->
                if (text.toString().isNotEmpty()) it.resetColors()
            }
        }
    }

    private fun initViewModel() = viewModel.apply {
        personalInformation.observe(viewLifecycleOwner) {
            if (it != null) binding.apply {
                etHeight.setText("${it.height}")
                etWeight.setText("${it.weight}")

                viewModel.birthdate = it.birthdate
                btnBirthdate.text = it.birthdate.toString()

                viewModel.runningDate = it.runningDate
                btnRunningDate.text = it.runningDate.toString()

                autoCompleteTextView.setText(it.gender, false)
            }
            dismissLoader()
        }

        updateSuccess.observe(viewLifecycleOwner) {
            if (it) {
                ViewUtils.showToast(requireContext(), R.string.put_personal_information_success)
                updateSuccess.postValue(false)
                dismiss()
            }
        }

        error.observe(viewLifecycleOwner) {
            ViewUtils.showToast(requireContext(), ViewUtils.getErrorStringId(it))
            dismissLoader()
        }

        init()
    }

    private fun dismissLoader() = binding.apply {
        clPersonalInformation.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private inner class IntegerMinMaxFilter(
        private var intMin: Int = 0,
        private var intMax: Int = 0
    ) : InputFilter {

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dStart: Int,
            dEnd: Int
        ): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(intMin, intMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }

        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }

    private inner class DecimalMinMaxFilter(
        private var min: Double = 0.0,
        private var max: Double = 0.0
    ) : InputFilter {

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dStart: Int,
            dEnd: Int
        ): CharSequence? {
            try {
                val input = (dest.toString() + source.toString()).toDouble()
                if (isInRange(min, max, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }

        private fun isInRange(a: Double, b: Double, c: Double): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }
}