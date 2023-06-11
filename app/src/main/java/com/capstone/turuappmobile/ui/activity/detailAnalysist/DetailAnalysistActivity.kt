package com.capstone.turuappmobile.ui.activity.detailAnalysist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactory
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.ActivityDetailAnalysistBinding
import com.capstone.turuappmobile.ui.activity.trackSleep.SleepViewModel
import com.capstone.turuappmobile.ui.fragment.historyAnalysis.HistorySleepAnalysistViewModel
import com.capstone.turuappmobile.utils.convertEpochToJustDateTime
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.Instant

class DetailAnalysistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailAnalysistBinding

    private var sleepQuality = mutableListOf<Float>()

    private var userUID = ""

    private var result = 0F

    private var confidenceQuality = mutableListOf<Entry>()

    private val sleepViewModel by viewModels<SleepViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val historySleepAnalysistViewModel by viewModels<HistorySleepAnalysistViewModel> {
        ViewModelFactoryUser.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAnalysistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historySleepAnalysistViewModel.getUserSession.observe(this) { User ->
            userUID = User.UID
            sleepViewModel.allSleepQuality(User.UID)
                .observe(this) { qualityEntities ->
                    if (qualityEntities.isNotEmpty()) {
                        showEmptyDataLayout(false)
                        qualityEntities.forEach {

                            sleepQuality.add(it.sleepQuality)
                        }
                        val instant = Instant.now()
                        binding.lastCheckedTxt.text = this.resources.getString(
                            R.string.last_checked,
                            convertEpochToJustDateTime(instant.epochSecond.toInt())
                        )
                        binding.sleepQualityValueTxt.text =
                            this.resources.getString(
                                R.string.result_quality,
                                sleepQuality.last().toInt()
                            )
                        binding.sleepQualityStatusTxt.text = qualityCondition(result)


                        binding.averageSleepQualityTxt.text =
                            this.resources.getString(
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
                                getColor(R.color.white_100)
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
                                getDrawable(
                                    R.drawable.background_gradient_chart
                                )
                            arrayHistoryDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                            arrayHistoryDataSet.cubicIntensity =
                                0.2f

                            val lineData = LineData(arrayHistoryDataSet)
                            lineData.setDrawValues(true)
                            lineData.setValueTextColor(getColor(R.color.white_100))
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
}