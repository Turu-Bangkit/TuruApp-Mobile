package com.capstone.turuappmobile.ui.activity.detailHistorySleep

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.db.SleepClassifyEventEntity
import com.capstone.turuappmobile.data.db.SleepSumEntity
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactory
import com.capstone.turuappmobile.databinding.ActivityDetailHistoryBinding
import com.capstone.turuappmobile.ui.activity.trackSleep.SleepViewModel
import com.capstone.turuappmobile.utils.convertEpochToDateTime
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class DetailHistoryActivity : AppCompatActivity(), OnChartValueSelectedListener {

    private lateinit var binding: ActivityDetailHistoryBinding
    private val sleepViewModel by viewModels<SleepViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val confidenceEntries = ArrayList<BarEntry>()
    private val lightEntries = ArrayList<BarEntry>()
    private val motionEntries = ArrayList<BarEntry>()

    private var sumEntity = listOf<SleepSumEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startTime = intent.getIntExtra(START_TIME, 0)
        val endTime = intent.getIntExtra(END_TIME, 0)

        binding.myToolbarDetailHistory.subtitle = resources.getString(R.string.range_date, convertEpochToDateTime(startTime), convertEpochToDateTime(endTime))
        binding.myToolbarDetailHistory.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        sleepViewModel.allByEpoch(startTime, endTime).observe(this) { sleepClassifyEventEntity ->

            sumEntity = getSumFromList(sleepClassifyEventEntity)

            for ((index, element) in sumEntity.withIndex()) {

                confidenceEntries.add(
                    BarEntry(
                        index.toFloat(),
                        element.confidence.toFloat()
                    )
                )
                lightEntries.add(
                    BarEntry(
                        index.toFloat(),
                        element.light.toFloat()
                    )
                )
                motionEntries.add(
                    BarEntry(
                        index.toFloat(),
                        element.motion.toFloat()
                    )
                )
            }

            Log.d("DetailHistoryActivity", "sumEntity: ${motionEntries.size}")

            val confidenceDataSet = BarDataSet(confidenceEntries, "Confidence")
            val lightDataSet = BarDataSet(lightEntries, "Light")
            val motionDataSet = BarDataSet(motionEntries, "Motion")

            confidenceDataSet.color = getColor(R.color.blue_300)
            lightDataSet.color = getColor(R.color.blue_400)
            motionDataSet.color = getColor(R.color.blue_600)

            val barData = BarData(confidenceDataSet, lightDataSet, motionDataSet)
            barData.barWidth = 0.5f
            barData.setDrawValues(false)


            val xAxis: XAxis = binding.chart.xAxis
            xAxis.granularity = 1f


            binding.chart.setOnChartValueSelectedListener(this)
            binding.chart.apply {
                data = barData
                legend.isEnabled = false
                description.isEnabled = false
                isDoubleTapToZoomEnabled = false
                invalidate()

            }


        }

    }

    private fun getSumFromList(list: List<SleepClassifyEventEntity>): List<SleepSumEntity> {
        val sumEntityList = mutableListOf<SleepSumEntity>()
        var totalItem = 0
        var totalConfident = 0
        var totalLight = 0
        var totalMotion = 0
        var startTimeSum = list.first().timestampSeconds

        for (event in list) {
            totalItem++
            totalConfident += event.confidence
            totalLight += event.light
            totalMotion += event.motion

            val elapsedTimeMinutes = (event.timestampSeconds - startTimeSum) / 60
            if (elapsedTimeMinutes >= 60) {
                val averageConfident = totalConfident / totalItem
                val averageMotion = totalMotion / totalItem
                val averageLight = totalLight / totalItem

                sumEntityList.add(
                    SleepSumEntity(
                        startTimeSum,
                        event.timestampSeconds,
                        averageConfident,
                        averageMotion,
                        averageLight
                    )
                )

                startTimeSum = event.timestampSeconds
                totalItem = 0
                totalConfident = 0
                totalLight = 0
                totalMotion = 0
            }
        }

        if (totalItem > 0) {
            val averageConfident = totalConfident / totalItem
            val averageMotion = totalMotion / totalItem
            val averageLight = totalLight / totalItem

            sumEntityList.add(
                SleepSumEntity(
                    startTimeSum,
                    list.last().timestampSeconds,
                    averageConfident,
                    averageMotion,
                    averageLight
                )
            )


        }




        return sumEntityList
    }

    companion object {
        const val START_TIME = "start_time"
        const val END_TIME = "end_time"
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {

        if (e != null) {
            val sleepSumEntity = sumEntity[e.x.toInt()]
            binding.awakeSum.text = resources.getString(R.string.awake_sum, sleepSumEntity.confidence.toString())
            binding.motionSum.text = resources.getString(R.string.motion_sum,sleepSumEntity.motion.toString())
            binding.lightSum.text = resources.getString(R.string.light_sum,sleepSumEntity.light.toString())
        }
    }

    override fun onNothingSelected() {
        Log.d("onNothinf", "nothing")
    }

}