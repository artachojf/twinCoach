package com.example.healthconnect.codelab.ui.firstSteps.first

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.FragmentFirstStepBinding
import com.example.healthconnect.codelab.ui.MainActivity
import com.example.healthconnect.codelab.ui.firstSteps.FirstStepsViewModel
import com.example.healthconnect.codelab.utils.ViewUtils

class FirstStepFragment : Fragment() {

    private var _binding: FragmentFirstStepBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FirstStepsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        viewModel.initFirst(requireContext())
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
        btnPlayStore.setOnClickListener {
            viewModel.apply {
                if (!sdkSupported.value!!) ViewUtils.showToast(requireContext(), R.string.sdk_not_supported)
                else if (!installed.value!!) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=com.google.android.apps.healthdata")
                        )
                    )
                } else ViewUtils.showToast(requireContext(), R.string.health_connect_installed)
            }
        }

        btnNext.setOnClickListener {
            val action = FirstStepFragmentDirections.actionFirstStepFragmentToSecondStepFragment()
            findNavController().navigate(action)
        }
    }

    private fun initViewModel() = viewModel.apply {
        installed.observe(viewLifecycleOwner) {
            binding.btnNext.isEnabled = it
        }

        initFirst(requireContext())
    }
}