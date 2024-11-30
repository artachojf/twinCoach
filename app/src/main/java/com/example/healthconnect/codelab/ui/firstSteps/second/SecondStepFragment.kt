package com.example.healthconnect.codelab.ui.firstSteps.second

import android.content.Intent
import android.health.connect.HealthConnectManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.health.connect.client.HealthConnectClient
import androidx.navigation.fragment.findNavController
import com.example.healthconnect.codelab.BuildConfig
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.FragmentSecondStepBinding
import com.example.healthconnect.codelab.ui.MainActivity
import com.example.healthconnect.codelab.ui.firstSteps.FirstStepsViewModel
import com.example.healthconnect.codelab.utils.ViewUtils

class SecondStepFragment : Fragment() {

    private var _binding: FragmentSecondStepBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FirstStepsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        viewModel.initSecond()
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
        btnBack.setOnClickListener { findNavController().popBackStack() }

        btnNext.setOnClickListener {
            if (viewModel.hasPermission.value!!) {
                val action = SecondStepFragmentDirections.actionSecondStepFragmentToThirdStepFragment()
                findNavController().navigate(action)
            } else ViewUtils.showToast(requireContext(), R.string.grant_permission)
        }

        btnPermissions.setOnClickListener {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                Intent(HealthConnectManager.ACTION_MANAGE_HEALTH_PERMISSIONS)
                    .putExtra(Intent.EXTRA_PACKAGE_NAME, BuildConfig.APPLICATION_ID)
            } else {
                Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS)
            }
            startActivity(intent)
        }
    }

    private fun initViewModel() = viewModel.apply {
        hasPermission.observe(viewLifecycleOwner) {
            binding.btnNext.isEnabled = it
        }
    }
}