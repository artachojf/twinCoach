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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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

        initViews()
        initViewModel()
        //requestPermission()
    }

    private fun initViews() = binding.apply {
        refreshLayout.isRefreshing = true
        refreshLayout.setOnRefreshListener {
            viewModel.init()
        }

        rvHome.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvHome.adapter = adapter
        adapter.submitList(getHomeOptions())
    }

    private fun initViewModel() = viewModel.apply {
        generalInfo.observe(viewLifecycleOwner) {
            adapter.submitList(getHomeOptions())

            binding.apply {
                rvHome.visibility = View.VISIBLE
                tvHomeEmptyState.visibility = View.GONE
            }
            dismissLoader()

            Handler(Looper.getMainLooper()).postDelayed({
                if (it == null) moveToFirstSteps()
            }, 1000)
        }

        error.observe(viewLifecycleOwner) {
            binding.apply {
                tvHomeEmptyState.text = getString(ViewUtils.getErrorStringId(it.failure))

                rvHome.visibility = View.GONE
                tvHomeEmptyState.visibility = View.VISIBLE
            }
            dismissLoader()
        }

        init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dismissLoader() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.refreshLayout.isRefreshing = false
        }, 1000)
    }

    private fun getHomeOptions(): List<HomeViewEntity> {
        val list = mutableListOf<HomeViewEntity>()

        list.add(HomeViewEntity.GoalViewEntity(viewModel.generalInfo.value?.features?.goal) { moveToGoal() })
        if (viewModel.generalInfo.value?.features?.suggestions != null
            && viewModel.generalInfo.value?.features?.suggestions!!.suggestions.isNotEmpty()
        )
            list.add(HomeViewEntity.SuggestionsViewEntity(viewModel.generalInfo.value?.features?.suggestions) { moveToProgression() })
        if (viewModel.generalInfo.value?.features?.trainingPlan?.sessions?.isNotEmpty() == true)
            HomeViewEntity.NextSessionEntity(viewModel.generalInfo.value?.features?.trainingPlan?.sessions?.get(0)) { moveToNextSession() }
        list.add(HomeViewEntity.GeneralInformationEntity(viewModel.generalInfo.value?.attributes) { moveToGeneralInfo() })
        list.add(HomeViewEntity.FatigueViewEntity(3) {})

        return list
    }

    private fun moveToGoal() {
        val action = HomeFragmentDirections.actionHomeFragmentToGoalDialogFragment()
        findNavController().navigate(action)
    }

    private fun moveToProgression() {
        val action = HomeFragmentDirections.actionHomeFragmentToProgressionFragment()
        findNavController().navigate(action)
    }

    private fun moveToNextSession() {
        viewModel.generalInfo.value?.features?.trainingPlan?.sessions?.get(0)?.let {
            val action = HomeFragmentDirections.actionHomeFragmentToSuggestedSessionInfoFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun moveToGeneralInfo() {
        val action = HomeFragmentDirections.actionHomeFragmentToPersonalInformationDialogFragment()
        findNavController().navigate(action)
    }

    private fun moveToFirstSteps() {
        val action = HomeFragmentDirections.actionHomeFragmentToFirstStepFragment()
        findNavController().navigate(action)
    }

    //Periodic Service

    private fun requestPermission() {
        lifecycleScope.launch {
            if (!hcManager.hasAllPermissions()) {
                val requestPermissions =
                    registerForActivityResult(hcManager.requestPermissionActivityContract()) {
                        if (it.containsAll(hcManager.permissions)) launchService()
                        else {
                            ViewUtils.showDialog(
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
        val jobScheduler =
            requireContext().getSystemService(AppCompatActivity.JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobBuilder =
            JobInfo.Builder(1, ComponentName(requireContext(), PeriodicDittoService::class.java))
                .setPeriodic(1000 * 60 * 60).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        jobScheduler.schedule(jobBuilder.build())
    }
}