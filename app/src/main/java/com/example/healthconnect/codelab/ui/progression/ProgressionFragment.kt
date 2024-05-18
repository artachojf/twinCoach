package com.example.healthconnect.codelab.ui.progression

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthconnect.codelab.R
import com.example.healthconnect.codelab.databinding.FragmentProgressionBinding
import com.example.healthconnect.codelab.domain.model.ditto.DittoGeneralInfo
import com.example.healthconnect.codelab.ui.MainActivity
import com.example.healthconnect.codelab.ui.progression.model.SuggestionViewEntity
import com.example.healthconnect.codelab.ui.progression.model.toPresentation
import com.example.healthconnect.codelab.utils.ViewUtils
import com.example.healthconnect.codelab.utils.toDistanceString
import com.example.healthconnect.codelab.utils.toSpeedString
import com.example.healthconnect.codelab.utils.toTimeString
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import kotlin.math.absoluteValue

@AndroidEntryPoint
class ProgressionFragment : Fragment() {

    private var _binding: FragmentProgressionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProgressionViewModel by viewModels()

    private val lineSeries = LineGraphSeries<DataPoint>(arrayOf())
    private val adapter = SuggestionsAdapter(::onSuggestionClick)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).apply {
            showNavbar(true)
            showToolbar(R.string.title_progression, false)
        }

        initViews()
        initViewModel()
    }

    private fun initViews() = binding.apply {
        swipeRefreshLayout.isRefreshing = true
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.init()
        }

        graph.addSeries(lineSeries)

        initSuggestionsRecycler()
    }

    private fun initViewModel() = viewModel.apply {
        features.observe(viewLifecycleOwner) {
            binding.apply {
                if (it == null || it.goal.estimations.isEmpty()) {
                    tvEmptyState.text = getString(R.string.progression_empty_state)

                    tvEmptyState.visibility = View.VISIBLE
                    scrollView.visibility = View.GONE
                } else {
                    bindGraph(it.goal.estimations)
                    bindCurrentEstimation(it.goal)

                    if (it.suggestions.suggestions.isEmpty()) {
                        tvSuggestions.visibility = View.GONE
                        rvSuggestions.visibility = View.GONE
                    } else {
                        updateSuggestions(it.suggestions.suggestions)
                        tvSuggestions.visibility = View.VISIBLE
                        rvSuggestions.visibility = View.VISIBLE
                    }

                    tvEmptyState.visibility = View.GONE
                    scrollView.visibility = View.VISIBLE
                }
            }
            dismissLoader()
        }

        error.observe(viewLifecycleOwner) {
            binding.apply {
                tvEmptyState.text = getString(ViewUtils.getErrorStringId(it.failure))

                tvEmptyState.visibility = View.VISIBLE
                scrollView.visibility = View.GONE
            }
            dismissLoader()
        }

        putFeaturesSuccess.observe(viewLifecycleOwner) {
            if (it) {
                ViewUtils.showToast(requireContext(), R.string.put_features_success)
                putFeaturesSuccess.postValue(false)
            }
        }

        putFeaturesError.observe(viewLifecycleOwner) {
            if (it != null) {
                ViewUtils.showToast(requireContext(), ViewUtils.getErrorStringId(it.failure))
                putFeaturesError.postValue(null)
            }
        }

        init()
    }

    private fun dismissLoader() {
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun bindGraph(estimations: List<DittoGeneralInfo.Estimation>) = binding.apply {
        lineSeries.resetData(
            estimations.map {
                DataPoint(it.date.toEpochDay().toDouble(), (-1*it.seconds).toDouble())
            }.toTypedArray()
        )
        graph.gridLabelRenderer.labelFormatter = LabelFormatter()
        graph.gridLabelRenderer.numHorizontalLabels = 3
        graph.gridLabelRenderer.setHumanRounding(false)

        graph.viewport.isScrollable = true
        graph.viewport.isXAxisBoundsManual = true
        graph.viewport.setMaxX(estimations.last().date.plusDays(1).toEpochDay().toDouble())
        graph.viewport.setMinX(estimations.first().date.minusDays(1).toEpochDay().toDouble())

        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.setMaxY((estimations.minOf { it.seconds }*-1).toDouble())
        graph.viewport.setMinY((estimations.maxOf { it.seconds }*-1).toDouble())
    }

    private fun bindCurrentEstimation(goal: DittoGeneralInfo.Goal) = binding.apply {
        val estimation = goal.estimations.last()
        tvEstimation.text = "${estimation.seconds.toTimeString()} ${getString(R.string.estimation_part_1)}" +
                " ${goal.distance.toDistanceString()} (${((estimation.seconds.toDouble()/60) / (goal.distance.toDouble()/1000)).toSpeedString()})"

        tvGoalDate.text = "${getString(R.string.goal_date_part_1)} ${goal.estimations.last().goalReachDate}"
    }

    private fun initSuggestionsRecycler() = binding.apply {
        rvSuggestions.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvSuggestions.adapter = adapter
    }

    private fun updateSuggestions(suggestions: List<DittoGeneralInfo.Suggestion>) {
        adapter.submitList(
            suggestions.map {
                it.toPresentation(requireContext())
            },
        )
    }

    private fun onSuggestionClick(item: SuggestionViewEntity) {
        when (item.suggestion) {
            is DittoGeneralInfo.Suggestion.SmallerGoal -> {
                ViewUtils.showDialog(requireContext(), item.description, { changeGoal(item.suggestion.id, item.suggestion.distance, item.suggestion.seconds, item.suggestion.date) }) {}
            }

            is DittoGeneralInfo.Suggestion.BiggerGoal -> {
                ViewUtils.showDialog(requireContext(), item.description, { changeGoal(item.suggestion.id, item.suggestion.distance, item.suggestion.seconds, item.suggestion.date) }) {}
            }

            is DittoGeneralInfo.Suggestion.LessTrainingDays -> {
                ViewUtils.showDialog(requireContext(), item.description, { changeTrainingDays(item.suggestion.id, item.suggestion.trainingDays) }) {}
            }

            is DittoGeneralInfo.Suggestion.MoreTrainingDays -> {
                ViewUtils.showDialog(requireContext(), item.description, { changeTrainingDays(item.suggestion.id,   item.suggestion.trainingDays) }) {}
            }
        }
    }

    private fun changeGoal(id: Int, distance: Int, seconds: Int, date: LocalDate) {
        val modifiedFeatures = viewModel.features.value?.let {
            it.goal.distance = distance
            it.goal.seconds = seconds
            it.goal.date = date
            it.suggestions.suggestions = it.suggestions.suggestions.filter { it.id != id }
            it
        }
        viewModel.putGeneralInfoFeatures(
            viewModel.googleId.value!!,
            modifiedFeatures!!
        )
    }

    private fun changeTrainingDays(id: Int, days: List<Int>) {
        val modifiedFeatures = viewModel.features.value?.let {
            it.preferences.trainingDays = days
            it.suggestions.suggestions = it.suggestions.suggestions.filter { it.id != id }
            it
        }
        viewModel.putGeneralInfoFeatures(
            viewModel.googleId.value!!,
            modifiedFeatures!!
        )
    }

    private class LabelFormatter : DefaultLabelFormatter() {
        override fun formatLabel(value: Double, isValueX: Boolean): String {
            return if (isValueX) {
                LocalDate.ofEpochDay(value.toLong()).toString()
            } else {
                value.absoluteValue.toInt().toTimeString()
            }
        }
    }
}