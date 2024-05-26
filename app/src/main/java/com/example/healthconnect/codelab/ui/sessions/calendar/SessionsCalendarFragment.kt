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
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.ui.sessions.SessionsFragmentDirections
import com.example.healthconnect.codelab.ui.sessions.SessionsViewModel
import com.example.healthconnect.codelab.utils.toDistanceString
import com.example.healthconnect.codelab.utils.toFormatString
import com.example.healthconnect.codelab.utils.toHeartRateString
import com.example.healthconnect.codelab.utils.toSpeedString
import com.example.healthconnect.codelab.utils.toTimeString
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
        val completedSession = viewModel.sessions.value?.find {
            it.attributes.date.toLocalDate().equals(date)
        }

        val suggestedSession = viewModel.suggestedSessions.value?.find {
            it?.day?.equals(date) == true
        }

        binding.apply {
            if (completedSession != null) {
                tvSessionsCalendarEmptyState.visibility = View.GONE
                calendarSessionCard.root.visibility = View.VISIBLE
                bindCompletedCard(completedSession)
            } else if (suggestedSession != null) {
                tvSessionsCalendarEmptyState.visibility = View.GONE
                calendarSessionCard.root.visibility = View.VISIBLE
                bindSuggestedCard(suggestedSession)
            } else {
                tvSessionsCalendarEmptyState.visibility = View.VISIBLE
                calendarSessionCard.root.visibility = View.GONE
            }
        }
    }

    private fun bindCompletedCard(session: DittoCurrentState.Thing) = binding.calendarSessionCard.apply {
        val distance = session.features.trainingSession.getTotalDistance()
        val time = session.features.trainingSession.getTotalTime()

        tvNextSession.text = session.attributes.date.toFormatString()
        tvDistance.text = distance.toInt().toDistanceString()
        tvTime.text = time.toInt().toTimeString()
        tvHeart.text
        tvPace.text = ((time/60) / (distance / 1000)).toSpeedString()

        ivPace.visibility = View.VISIBLE
        tvPace.visibility = View.VISIBLE
        ivRest.visibility = View.GONE
        tvRest.visibility = View.GONE

        root.setOnClickListener {
            val action = SessionsFragmentDirections.actionSessionsFragmentToCompletedSessionInfoFragment(session)
            findNavController().navigate(action)
        }
    }

    private fun bindSuggestedCard(session: DittoGeneralInfo.TrainingSession) = binding.calendarSessionCard.apply {
        tvNextSession.text = session.day.toString()
        tvDistance.text =
            if (session.times == 1) "${session.times} X ${session.distance.toDistanceString()}"
            else session.distance.toDistanceString()
        tvTime.text = session.expectedTime.toTimeString()
        tvHeart.text = session.meanHeartRate.toHeartRateString()
        tvRest.text = session.rest.toTimeString()

        ivRest.visibility = View.VISIBLE
        tvRest.visibility = View.VISIBLE
        ivPace.visibility = View.GONE
        tvPace.visibility = View.GONE

        root.setOnClickListener {
            val action = SessionsFragmentDirections.actionSessionsFragmentToSuggestedSessionInfoFragment(session)
            findNavController().navigate(action)
        }
    }
}