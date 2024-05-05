package com.example.healthconnect.codelab.ui.home

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.FragmentHomeBinding
import com.example.healthconnect.codelab.services.PeriodicDittoService
import com.example.healthconnect.codelab.ui.MainActivity
import com.example.healthconnect.codelab.ui.home.adapter.HomeAdapter
import com.example.healthconnect.codelab.utils.ViewUtils
import com.example.healthconnect.codelab.utils.healthConnect.HealthConnectManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private val adapter: HomeAdapter = HomeAdapter()
    @Inject
    lateinit var hcManager: HealthConnectManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).apply {
            showNavbar(true)
            showToolbar(R.string.title_home, false)
        }

        binding.apply {
            refreshLayout.setOnRefreshListener {
                Handler(Looper.getMainLooper()).postDelayed({
                    refreshLayout.isRefreshing = false
                }, 2000)
            }

            rvHome.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvHome.adapter = adapter
            adapter.submitList(getHomeOptions())
        }

        viewModel.apply {
            currentState.observe(viewLifecycleOwner) {
                adapter.submitList(getHomeOptions())
            }

            generalInfo.observe(viewLifecycleOwner) {
                adapter.submitList(getHomeOptions())
            }

            error.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), "Error ocurred", Toast.LENGTH_LONG).show()
            }

            init()
        }

        //requestPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getHomeOptions(): List<HomeViewEntity> =
        listOf(
            HomeViewEntity.GoalViewEntity(viewModel.generalInfo.value?.features?.goal) { moveToGoal() },
            HomeViewEntity.SuggestionsViewEntity(viewModel.generalInfo.value?.features?.suggestions) { moveToProgression() },
            HomeViewEntity.NextSessionEntity(
                viewModel.generalInfo.value?.features?.trainingPlan?.sessions?.get(
                    0
                )
            ) { moveToNextSession() },
            HomeViewEntity.GeneralInformationEntity(viewModel.generalInfo.value?.attributes) { moveToGeneralInfo() },
            HomeViewEntity.FatigueViewEntity(3) {}
        )

    private fun moveToGoal() {
        //val action = HomeFragmentDirections
        //findNavController().navigate(action)
    }

    private fun moveToProgression() {
        //val action = HomeFragmentDirections
        //findNavController().navigate(action)
    }

    private fun moveToNextSession() {
        //val action = HomeFragmentDirections
        //findNavController().navigate(action)
    }

    private fun moveToGeneralInfo() {
        //val action = HomeFragmentDirections
        //findNavController().navigate(action)
    }

    private fun requestPermission() {
        lifecycleScope.launch {
            if (!hcManager.hasAllPermissions()) {
                val requestPermissions =
                    registerForActivityResult(hcManager.requestPermissionActivityContract()) {
                        if (it.containsAll(hcManager.permissions)) launchService()
                        else {
                            ViewUtils.showWarningDialog(
                                requireContext(),
                                R.string.permissions_alert_dialog,
                                {},
                                { activity?.finish() })
                        }
                    }
                requestPermissions.launch(hcManager.permissions)
            } else {
                launchService()
            }
        }
    }

    private fun launchService() {
        val jobScheduler = requireContext().getSystemService(AppCompatActivity.JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobBuilder = JobInfo.Builder(1, ComponentName(requireContext(), PeriodicDittoService::class.java))
            .setPeriodic(1000*60*60).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        jobScheduler.schedule(jobBuilder.build())
    }
}