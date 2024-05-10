package com.example.healthconnect.codelab.ui.sessions.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.healthconnect.codelab.databinding.FragmentSessionsCalendarBinding
import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.ui.sessions.SessionsFragmentDirections
import com.example.healthconnect.codelab.ui.sessions.SessionsViewModel
import com.example.healthconnect.codelab.ui.sessions.info.SessionInfoViewEntity
import java.time.LocalDate
import java.time.ZoneOffset

class SessionsCalendarFragment : Fragment() {

    private var _binding: FragmentSessionsCalendarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SessionsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionsCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initViewModel()
    }

    private fun initViews() = binding.apply {
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val date = LocalDate.of(year, month+1, dayOfMonth)
            updateCard(date)
            viewModel.calendarDate = date
        }
        calendarView.date = viewModel.calendarDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    private fun initViewModel() = viewModel.apply {
        sessions.observe(viewLifecycleOwner) {
            refreshCalendar()
        }

        suggestedSessions.observe(viewLifecycleOwner) {
            refreshCalendar()
        }
    }

    private fun refreshCalendar() {
        binding.calendarView.date = viewModel.calendarDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        updateCard(viewModel.calendarDate)
    }

    private fun updateCard(date: LocalDate) {
        val session = viewModel.sessions.value?.find {
            it.attributes.date.toLocalDate().equals(date)
        }

        binding.apply {
            if (session == null) {
                tvSessionsCalendarEmptyState.visibility = View.VISIBLE
                calendarSessionCard.root.visibility = View.GONE
            } else {
                tvSessionsCalendarEmptyState.visibility = View.GONE
                calendarSessionCard.root.visibility = View.VISIBLE
                bindCard(session)
            }
        }
    }

    private fun bindCard(session: DittoCurrentState.Thing) = binding.calendarSessionCard.apply {
        tvNextSession.text = session.attributes.date.toString()

        root.setOnClickListener {
            val action = SessionsFragmentDirections.actionSessionsFragmentToSessionsInfoFragment(
                SessionInfoViewEntity.Session(session.features.trainingSession, session.attributes.date)
            )
            findNavController().navigate(action)
        }
    }
}