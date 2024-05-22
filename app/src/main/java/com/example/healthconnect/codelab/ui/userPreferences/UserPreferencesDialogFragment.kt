package com.example.healthconnect.codelab.ui.userPreferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.FragmentUserPreferencesDialogBinding
import com.example.healthconnect.codelab.utils.ViewUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserPreferencesDialogFragment : DialogFragment() {

    private var _binding: FragmentUserPreferencesDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserPreferencesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserPreferencesDialogBinding.inflate(inflater, container, false)
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

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnSave.setOnClickListener {
            val checkboxes = listOf(dayMonday, dayTuesday, dayWednesday, dayThursday, dayFriday, daySaturday, daySunday)
            val trainingDays = mutableListOf<Int>()
            checkboxes.indices.forEach {
                if (checkboxes[it].isChecked) trainingDays.add(it)
            }

            if (trainingDays.isEmpty()) ViewUtils.showToast(requireContext(), R.string.no_training_days_selected_error)
            else {
                val preferences = viewModel.userPreferences.value!!.let {
                    it.trainingDays = trainingDays
                    it
                }
                viewModel.updateUserPreferences(viewModel.googleId.value!!, preferences)
            }
        }
    }

    private fun initViewModel() = viewModel.apply {
        userPreferences.observe(viewLifecycleOwner) {preferences ->
            if (preferences != null) binding.apply {
                val checkboxes = listOf(dayMonday, dayTuesday, dayWednesday, dayThursday, dayFriday, daySaturday, daySunday)
                preferences.trainingDays.forEach { checkboxes[it].isChecked = true }
            }
        }

        updateSuccess.observe(viewLifecycleOwner) {
            if (it) {
                ViewUtils.showToast(requireContext(), R.string.put_preferences_success)
                updateSuccess.postValue(false)
                dismiss()
            }
        }

        error.observe(viewLifecycleOwner) {
            ViewUtils.showToast(requireContext(), ViewUtils.getErrorStringId(it))
        }

        init()
    }
}