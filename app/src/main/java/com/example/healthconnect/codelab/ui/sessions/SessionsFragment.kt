package com.example.healthconnect.codelab.ui.sessions

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.FragmentSessionsBinding
import com.example.healthconnect.codelab.ui.MainActivity
import com.example.healthconnect.codelab.ui.sessions.adapter.SessionsAdapter
import com.example.healthconnect.codelab.utils.ViewUtils
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionsFragment : Fragment() {

    private var _binding: FragmentSessionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SessionsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).apply {
            showNavbar(true)
            showToolbar(R.string.title_sessions, false)
        }

        initViews()
        initViewModel()
    }

    private fun initViews() = binding.apply {
        swipeRefreshLayout.isRefreshing = true
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.init()
        }

        initTabs()
    }

    private fun initTabs() {
        binding.sessionsViewPager.adapter = SessionsAdapter(this)
        TabLayoutMediator(binding.sessionsTabLayout, binding.sessionsViewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.list_tab)
                }
                else -> {
                    tab.text = getString(R.string.calendar_tab)
                }
            }
        }.attach()
    }

    private fun initViewModel() = viewModel.apply {
            sessions.observe(viewLifecycleOwner) {
                binding.apply {
                    tvSessionsEmptyState.visibility = View.GONE
                    sessionsViewPager.visibility = View.VISIBLE
                }
                dismissLoader()
            }

            error.observe(viewLifecycleOwner) {
                binding.apply {
                    sessionsViewPager.visibility = View.GONE

                    tvSessionsEmptyState.text = getString(ViewUtils.getErrorStringId(it.failure))
                    tvSessionsEmptyState.visibility = View.VISIBLE
                }
                dismissLoader()
            }
        init()
    }

    private fun dismissLoader() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.swipeRefreshLayout.isRefreshing = false
        }, 1000)
    }
}