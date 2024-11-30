package com.example.healthconnect.codelab.ui.firstSteps.third

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.FragmentThirdStepBinding
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.ui.MainActivity
import com.example.healthconnect.codelab.ui.firstSteps.FirstStepsViewModel
import com.example.healthconnect.codelab.utils.ViewUtils
import com.example.healthconnect.codelab.utils.onError
import com.example.healthconnect.codelab.utils.resetColors
import java.lang.Exception
import java.time.Instant
import java.time.LocalDate

class ThirdStepFragment : Fragment() {

    private var _binding: FragmentThirdStepBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FirstStepsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThirdStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).apply {
            hideToolbar()
            showNavbar(false)
        }

        initViews()
        initViewModel()
    }

    private fun initViews() = binding.apply {
        initPersonalInformation()
        initGoal()

        listOf(etHeight, etWeight, etHours, etMinutes, etSeconds, etLength).forEach {
            it.addTextChangedListener { text ->
                if (text.toString().isNotEmpty()) it.resetColors()
            }
        }

        btnBack.setOnClickListener { findNavController().popBackStack() }

        btnFinish.setOnClickListener {
            binding.apply {
                if (finishCheck()) {
                    val attributes = DittoGeneralInfo.Attributes(
                        autoCompleteTextView.text.toString(),
                        etHeight.text.toString().toInt(),
                        etWeight.text.toString().toDouble(),
                        LocalDate.parse(btnBirthdate.text.toString()),
                        LocalDate.parse(btnRunningDate.text.toString())
                    )

                    val seconds = etHours.text.toString().toInt() * 3600 + etMinutes.text.toString()
                        .toInt() * 60 + etSeconds.text.toString().toInt()
                    val goal = DittoGeneralInfo.Goal(
                        etLength.text.toString().toInt(),
                        seconds,
                        emptyList(),
                        LocalDate.parse(btnDate.text.toString())
                    )

                    val checkboxes = listOf(dayMonday, dayTuesday, dayWednesday, dayThursday, dayFriday, daySaturday, daySunday)
                    val trainingDays = mutableListOf<Int>()
                    checkboxes.indices.forEach {
                        if (checkboxes[it].isChecked) trainingDays.add(it)
                    }
                    val preferences = DittoGeneralInfo.Preferences(
                        trainingDays
                    )

                    val features = DittoGeneralInfo.Features(
                        goal,
                        DittoGeneralInfo.TrainingPlan(emptyList()),
                        DittoGeneralInfo.Suggestions(emptyList()),
                        preferences
                    )

                    val thing = DittoGeneralInfo.Thing(
                        DittoGeneralInfo.thingId(viewModel.googleId.value!!),
                        null,
                        attributes,
                        features
                    )

                    viewModel.putGeneralInfo(thing)
                } else {
                    ViewUtils.showToast(requireContext(), R.string.form_not_completed)
                }
            }
        }
    }

    private fun finishCheck(): Boolean {
        binding.apply {
            var completed = true
            listOf(etHeight, etWeight, etHours, etMinutes, etSeconds, etLength).forEach {
                if (it.text.isEmpty()) {
                    completed = false
                    it.onError()
                }
            }

            listOf(btnBirthdate, btnRunningDate, btnDate).forEach {
                if (getDate(it.text.toString()) == null) {
                    completed = false
                    ViewUtils.showToast(requireContext(), R.string.select_date)
                }
            }

            val checkboxes = listOf(dayMonday, dayTuesday, dayWednesday, dayThursday, dayFriday, daySaturday, daySunday)
            val trainingDays = mutableListOf<Int>()
            checkboxes.indices.forEach {
                if (checkboxes[it].isChecked) trainingDays.add(it)
            }
            if (trainingDays.isEmpty()) ViewUtils.showToast(requireContext(), R.string.no_training_days_selected_error)

            return completed
        }
    }

    private fun getDate(text: String): LocalDate? {
        return try {
            LocalDate.parse(text)
        } catch (_: Exception) {
            null
        }
    }

    private fun initViewModel() = viewModel.apply {
        updateSuccess.observe(viewLifecycleOwner) {
            val action = ThirdStepFragmentDirections.actionThirdStepFragmentToHomeFragment()
            findNavController().navigate(action)
        }

        error.observe(viewLifecycleOwner) {
            ViewUtils.showToast(requireContext(), ViewUtils.getErrorStringId(it))
        }

        initThird()
    }

    private fun initPersonalInformation() = binding.apply {
        etHeight.filters = arrayOf(IntegerMinMaxFilter(0, 250))
        etWeight.filters = arrayOf(DecimalMinMaxFilter(0.0, 300.0))

        val genders = arrayOf(getString(R.string.male), getString(R.string.female), getString(R.string.non_binary),
            getString(R.string.gender_fluid), getString(R.string.dont_know))
        autoCompleteTextView.setAdapter(ArrayAdapter(requireContext(), R.layout.dropdown_item, genders))

        btnBirthdate.setOnClickListener {
            val date = try {
                LocalDate.parse(btnBirthdate.text.toString())
            } catch (_: Exception) {
                LocalDate.now()
            }
            DatePickerDialog(
                requireContext(),
                {_,year,month,day ->
                    btnBirthdate.text = LocalDate.of(year, month+1, day).toString()
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
            val date = try {
                LocalDate.parse(btnRunningDate.text.toString())
            } catch (_: Exception) {
                LocalDate.now()
            }
            DatePickerDialog(
                requireContext(),
                {_,year,month,day ->
                    btnRunningDate.text = LocalDate.of(year, month+1, day).toString()
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
    }

    private fun initGoal() = binding.apply {
        etMinutes.filters = arrayOf(IntegerMinMaxFilter(0, 99))
        etMinutes.filters = arrayOf(IntegerMinMaxFilter(0, 59))
        etSeconds.filters = arrayOf(IntegerMinMaxFilter(0, 59))

        btnDate.setOnClickListener {
            val date = try {
                LocalDate.parse(btnDate.text.toString())
            } catch (_: Exception) {
                LocalDate.now()
            }
            DatePickerDialog(
                requireContext(),
                {_,year,month,day ->
                    btnDate.text = LocalDate.of(year, month+1, day).toString()
                },
                date.year,
                date.monthValue-1,
                date.dayOfMonth
            ).let {
                it.datePicker.minDate = Instant.now().toEpochMilli()
                it.show()
            }
        }
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