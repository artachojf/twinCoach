package com.example.healthconnect.codelab.ui.sessions.info.suggested

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.FragmentSuggestedSessionInfoBinding
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.ui.MainActivity
import com.example.healthconnect.codelab.utils.toDistanceString
import com.example.healthconnect.codelab.utils.toHeartRateString
import com.example.healthconnect.codelab.utils.toSpeedString
import com.example.healthconnect.codelab.utils.toTimeString

class SuggestedSessionInfoFragment : Fragment() {

    private var _binding: FragmentSuggestedSessionInfoBinding? = null
    private val binding get() = _binding!!

    private val args: SuggestedSessionInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuggestedSessionInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).apply {
            showNavbar(false)
            showToolbar(R.string.title_suggested_session, true)
        }

        initViews()
    }

    private fun initViews() {
        binding.tvSessionTitle.text = args.session.day.toString()
        bindSuggestionCard(args.session)
        initRecycler(args.session)
    }

    private fun bindSuggestionCard(session: DittoGeneralInfo.TrainingSession) = binding.suggestedSessionCard.apply {
        tvDistance.text =
            if (session.times == 1) "${session.times} X ${session.distance.toDistanceString()}"
            else session.distance.toDistanceString()
        tvTime.text = session.expectedTime.toTimeString()
        tvHeart.text = session.meanHeartRate.toHeartRateString()
        tvRest.text = session.rest.toTimeString()

        tvNextSession.visibility = View.GONE
        ivPace.visibility = View.GONE
        tvPace.visibility = View.GONE
        ivRest.visibility = View.VISIBLE
        tvRest.visibility = View.VISIBLE
    }

    private fun initRecycler(session: DittoGeneralInfo.TrainingSession) {
        binding.rvTimeline.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvTimeline.adapter = TimelineListAdapter(timelineList(session))
    }

    private fun timelineList(session: DittoGeneralInfo.TrainingSession): List<TimelineViewEntity> {
        val list = mutableListOf<TimelineViewEntity>()

        if (session.times > 1) {
            list.add(TimelineViewEntity(getString(R.string.warmup_title), getString(R.string.warmup_description)))
            list.add(TimelineViewEntity(getString(R.string.stretching_title), getString(R.string.stretching_before_description)))

            for (x in 1..session.times) {
                list.add(
                    TimelineViewEntity(
                        "${getString(R.string.run_title)} ${session.distance.toDistanceString()}",
                        "${getString(R.string.run_description_1)} ${session.distance.toDistanceString()} ${getString(R.string.run_description_2)}" +
                                " ${session.expectedTime.toTimeString()} ${getString(R.string.run_description_3)}"
                    )
                )
                if (x != session.times)
                    list.add(
                        TimelineViewEntity(
                            getString(R.string.rest_title),
                            "${getString(R.string.rest_description_1)} ${session.rest.toTimeString()} ${getString(R.string.rest_description_2)}"
                        )
                    )
            }

            list.add(TimelineViewEntity(getString(R.string.cooldown_title), getString(R.string.cooldown_description)))
        } else {
            list.add(TimelineViewEntity(getString(R.string.stretching_title), getString(R.string.stretching_before_description)))
            list.add(TimelineViewEntity("Run ${session.distance.toDistanceString()}", ""))
        }

        list.add(TimelineViewEntity(getString(R.string.stretching_title), getString(R.string.stretching_after_description)))

        return list
    }
}