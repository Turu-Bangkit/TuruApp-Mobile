package com.capstone.turuappmobile.ui.fragment.historyAnalysis

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactory
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.FragmentHistorySleepAnalysistBinding
import com.capstone.turuappmobile.ui.activity.trackSleep.SleepViewModel
import com.capstone.turuappmobile.utils.convertEpochToJustDateTime
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.Instant


class HistorySleepAnalysistFragment : Fragment() {

    private var _binding: FragmentHistorySleepAnalysistBinding? = null
    private val binding get() = _binding!!

    private var sleepQuality = mutableListOf<Float>()

    private var userUID = ""

    private var result = 0F

    private var confidenceQuality = mutableListOf<Entry>()

    private val sleepViewModel by viewModels<SleepViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val historySleepAnalysistViewModel by viewModels<HistorySleepAnalysistViewModel> {
        ViewModelFactoryUser.getInstance(requireActivity())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentHistorySleepAnalysistBinding.inflate(inflater, container, false)
        Log.d("HistoryAnalysistFragment", "onCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lineChartGradient.invalidate()

        historySleepAnalysistViewModel.getUserSession.observe(viewLifecycleOwner) { User ->
            userUID = User.UID
            sleepViewModel.allSleepQuality(User.UID)
                .observe(viewLifecycleOwner) { qualityEntities ->
                    if (qualityEntities.isNotEmpty()) {
                        showEmptyDataLayout(false)
                        qualityEntities.forEach {

                            sleepQuality.add(it.sleepQuality)
                        }
                        val instant = Instant.now()
                        binding.lastCheckedTxt.text = requireActivity().resources.getString(
                            R.string.last_checked,
                            convertEpochToJustDateTime(instant.epochSecond.toInt())
                        )
                        binding.sleepQualityValueTxt.text =
                            requireActivity().resources.getString(
                                R.string.result_quality,
                                sleepQuality.last().toInt()
                            )
                        binding.sleepQualityStatusTxt.text = qualityCondition(result)


                        binding.averageSleepQualityTxt.text =
                            requireActivity().resources.getString(
                                R.string.result_quality,
                                sleepQuality.average().toInt()
                            )


                        binding.lineChartGradient.apply {

                            xAxis.valueFormatter = IndexAxisValueFormatter()
                            xAxis.position = XAxis.XAxisPosition.BOTTOM

                            axisLeft.isEnabled = true
                            axisLeft.axisMinimum = 0f
                            axisLeft.granularity = 25f
                            axisLeft.axisMinimum = 0f
                            axisLeft.axisMaximum = 100f
                            axisLeft.valueFormatter = object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    return when {
                                        value < 20f -> "Very Bad"
                                        value < 40f -> "Bad"
                                        value < 60f -> "Medium"
                                        value < 80f -> "Good"
                                        else -> "Very Good"
                                    }
                                }
                            }
                            axisLeft.textColor =
                                requireActivity().getColor(R.color.white_100)
                            axisRight.isEnabled = false

                            description.isEnabled = false
                            legend.isEnabled = false


                            sleepQuality.forEach {
                                Log.d("confidenceEntries", "in")
                                confidenceQuality.add(
                                    Entry(
                                        sleepQuality.indexOf(it).toFloat(), it
                                    )
                                )
                            }


                            val arrayHistoryDataSet =
                                LineDataSet(confidenceQuality, "History")
                            arrayHistoryDataSet.setDrawFilled(true)
                            arrayHistoryDataSet.fillDrawable =
                                ContextCompat.getDrawable(
                                    requireActivity(),
                                    R.drawable.background_gradient_chart
                                )
                            arrayHistoryDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                            arrayHistoryDataSet.cubicIntensity =
                                0.2f

                            val lineData = LineData(arrayHistoryDataSet)
                            lineData.setDrawValues(true)
                            lineData.setValueTextColor(requireActivity().getColor(R.color.white_100))
                            data = lineData
                            invalidate()
                        }

                    }
                }

        }


    }


    private fun qualityCondition(quality: Float): String {
        return when {
            quality < 20f -> "Very Bad"
            quality < 40f -> "Bad"
            quality < 60f -> "Medium"
            quality < 80f -> "Good"
            else -> "Very Good"
        }
    }

    private fun showEmptyDataLayout(isLoading: Boolean) {
        binding.layoutLoadingEmpty.layoutEmpty.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.lineChartGradient.invalidate()

    }


}