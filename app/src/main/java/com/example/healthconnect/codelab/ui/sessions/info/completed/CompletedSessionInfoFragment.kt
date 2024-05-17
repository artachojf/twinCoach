package com.example.healthconnect.codelab.ui.sessions.info.completed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.FragmentCompletedSessionInfoBinding
import com.example.healthconnect.codelab.databinding.ItemHrZoneComponentBinding
import com.example.healthconnect.codelab.domain.model.ditto.DittoCurrentState
import com.example.healthconnect.codelab.ui.MainActivity
import com.example.healthconnect.codelab.utils.toDistanceString
import com.example.healthconnect.codelab.utils.toSpeedString
import com.example.healthconnect.codelab.utils.toTimeString

class CompletedSessionInfoFragment : Fragment() {

    private var _binding: FragmentCompletedSessionInfoBinding? = null
    private val binding get() = _binding!!

    private val args: CompletedSessionInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletedSessionInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).apply {
            showNavbar(true)
            showToolbar(R.string.title_completed_session, true)
        }

        initViews()
    }

    private fun initViews() {
        binding.tvSessionTitle.text = args.session.attributes.date.toString()
        bindCompletedCard(args.session.features.trainingSession)
        initRecycler()
        bindZones(args.session.features.trainingSession)
    }

    private fun bindCompletedCard(session: DittoCurrentState.TrainingSession) = binding.completedSessionCard.apply {
        val distance = session.getTotalDistance()
        val time = session.getTotalTime()

        tvDistance.text = distance.toInt().toDistanceString()
        tvTime.text = time.toInt().toTimeString()
        tvHeart.text
        tvPace.text = ((time/60) / (distance / 1000)).toSpeedString()

        tvNextSession.visibility = View.GONE
        ivPace.visibility = View.VISIBLE
        tvPace.visibility = View.VISIBLE
        ivRest.visibility = View.GONE
        tvRest.visibility = View.GONE
    }

    private fun initRecycler() {
        binding.rvLaps.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvLaps.adapter = LapsListAdapter(lapsList(args.session.features.trainingSession))
    }

    private fun bindZones(session: DittoCurrentState.TrainingSession) = binding.apply {
        val totalTime = session.getTotalTime()

        bindZone(getString(R.string.zone1), (session.zone1.time / totalTime) * 100, vZone1)
        bindZone(getString(R.string.zone2), (session.zone2.time / totalTime) * 100, vZone2)
        bindZone(getString(R.string.zone3), (session.zone3.time / totalTime) * 100, vZone3)
        bindZone(getString(R.string.zoneRest), (session.rest.time / totalTime) * 100, vRest)
    }

    private fun bindZone(name: String, progress: Double, binding: ItemHrZoneComponentBinding) =
        binding.apply {
            tvZone.text = name
            progressBar.progress = progress.toInt()
            tvProgress.text = "$progress %"
        }

    private fun lapsList(session: DittoCurrentState.TrainingSession): List<LapsViewEntity> {
        val list = mutableListOf<LapsViewEntity>()

        list.add(
            LapsViewEntity.Header(
                getString(R.string.column1_header),
                getString(R.string.column2_header),
                getString(R.string.column3_header)
            )
        )
        session.laps.forEach {
            list.add(
                LapsViewEntity.Item(
                    it.distance.toInt(),
                    it.time.toInt(),
                    ((it.time/60) / (it.distance / 1000))
                )
            )
        }

        return list
    }
}