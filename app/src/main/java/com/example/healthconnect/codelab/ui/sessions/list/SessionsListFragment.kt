package com.example.healthconnect.codelab.ui.sessions.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthconnect.codelab.databinding.FragmentSessionsListBinding
import com.example.healthconnect.codelab.ui.sessions.SessionsFragmentDirections
import com.example.healthconnect.codelab.ui.sessions.SessionsViewModel
import com.example.healthconnect.codelab.ui.sessions.info.SessionInfoViewEntity
import com.example.healthconnect.codelab.ui.sessions.list.adapter.SessionsListAdapter

class SessionsListFragment : Fragment() {

    private var _binding: FragmentSessionsListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SessionsViewModel by activityViewModels()

    private val adapter = SessionsListAdapter(::onItemClick)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initViewModel()
    }

    private fun initViews() = binding.apply {
        rvSessionsList.layoutManager = LinearLayoutManager(requireContext())
        rvSessionsList.adapter = adapter
    }

    private fun initViewModel() = viewModel.apply {
        sessions.observe(viewLifecycleOwner) {
            updateRecycler()
        }

        suggestedSessions.observe(viewLifecycleOwner) {
            updateRecycler()
        }
    }

    private fun updateRecycler() {
        val list = getRecyclerItems()
        if (list.isEmpty()) {
            binding.apply {
                tvSessionsListEmptyState.visibility = View.VISIBLE
                rvSessionsList.visibility = View.GONE
            }
        } else {
            adapter.submitList(list)

            binding.apply {
                tvSessionsListEmptyState.visibility = View.GONE
                rvSessionsList.visibility = View.VISIBLE
            }
        }
    }

    private fun getRecyclerItems(): List<SessionsListViewEntity> {
        val list = mutableListOf<SessionsListViewEntity>()

        if (viewModel.suggestedSessions.value?.isNotEmpty() == true) {
            list.add(SessionsListViewEntity.Label("Suggested Sessions"))
            viewModel.suggestedSessions.value?.map {
                SessionsListViewEntity.SuggestedSession(it!!)
            }?.let {
                list.addAll(
                    it
                )
            }
        }

        if (viewModel.sessions.value?.isNotEmpty() == true) {
            list.add(SessionsListViewEntity.Label("Completed Sessions"))
            viewModel.sessions.value?.map {
                SessionsListViewEntity.CompletedSession(it.features.trainingSession, it.attributes.date)
            }?.let {
                list.addAll(
                    it
                )
            }
        }

        return list
    }

    private fun onItemClick(session: SessionInfoViewEntity) {
        val action = SessionsFragmentDirections.actionSessionsFragmentToSessionsInfoFragment(session)
        findNavController().navigate(action)
    }
}