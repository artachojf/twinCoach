package com.example.healthconnect.codelab.ui.goal

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.FragmentGoalDialogBinding
import com.example.healthconnect.codelab.utils.ViewUtils
import com.example.healthconnect.codelab.utils.onError
import com.example.healthconnect.codelab.utils.resetColors
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDate

@AndroidEntryPoint
class GoalDialogFragment : DialogFragment() {

    private var _binding: FragmentGoalDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalDialogBinding.inflate(inflater, container, false)
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

        //TODO: Posibilidad de incluir un loader
        etMinutes.filters = arrayOf(MinMaxFilter(0, 99))
        etMinutes.filters = arrayOf(MinMaxFilter(0, 59))
        etSeconds.filters = arrayOf(MinMaxFilter(0, 59))

        btnDate.setOnClickListener {
            val today = LocalDate.now()
            DatePickerDialog(
                requireContext(),
                {_,year,month,day ->
                    viewModel.date = LocalDate.of(year, month+1, day)
                    btnDate.text = viewModel.date.toString()
                },
                today.year,
                today.monthValue-1,
                today.dayOfMonth
            ).let {
                it.datePicker.minDate = Instant.now().toEpochMilli()
                it.show()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnSave.setOnClickListener {
            var completed = true
            listOf(etHours, etMinutes, etSeconds, etLength).forEach {
                if (it.text.isEmpty()) {
                    completed = false
                    it.onError()
                }
            }
            if (viewModel.date == null) {
                completed = false
                ViewUtils.showToast(requireContext(), R.string.select_date)
            }


            if (completed) {
                val seconds = etHours.text.toString().toInt() * 3600 + etMinutes.text.toString()
                    .toInt() * 60 + etSeconds.text.toString().toInt()
                val distance = etLength.text.toString().toInt()
                viewModel.apply {
                    val goal = goal.value!!.let {
                        it.seconds = seconds
                        it.distance = distance
                        it.date = this.date!!
                        it
                    }
                    updateGoal(googleId.value!!, goal)
                }
            } else
                ViewUtils.showToast(requireContext(), R.string.form_not_completed)
        }

        listOf(etHours, etMinutes, etSeconds, etLength).forEach {
            it.addTextChangedListener {text ->
                if (text.toString().isNotEmpty()) it.resetColors()
            }
        }
    }

    private fun initViewModel() = viewModel.apply {
        goal.observe(viewLifecycleOwner) {
            if (it != null) binding.apply {
                etHours.setText(((it.seconds) / 3600).toString())
                etMinutes.setText((((it.seconds) % 3600) / 60).toString())
                etSeconds.setText((it.seconds % 60).toString())

                etLength.setText(it.distance.toString())

                viewModel.date = it.date
                btnDate.text = viewModel.date.toString()
            }
        }

        updateGoalSuccess.observe(viewLifecycleOwner) {
            if (it) {
                ViewUtils.showToast(requireContext(), R.string.put_goal_success)
                updateGoalSuccess.postValue(false)
                dismiss()
            }
        }

        error.observe(viewLifecycleOwner) {
            ViewUtils.showToast(requireContext(), ViewUtils.getErrorStringId(it))
        }

        init()
    }

    private inner class MinMaxFilter(
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
}