package com.capstone.turuappmobile.ui.fragment.historyAnalysis

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.db.SleepQualityEntity
import com.capstone.turuappmobile.data.db.SleepTimeEntity
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactory
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.FragmentHistorySleepAnalysistBinding
import com.capstone.turuappmobile.ui.activity.trackSleep.SleepViewModel
import com.capstone.turuappmobile.ui.fragment.historyList.HistorySleepListViewModel
import com.capstone.turuappmobile.utils.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.time.Instant


class HistorySleepAnalysistFragment : Fragment() {

    private var _binding: FragmentHistorySleepAnalysistBinding? = null
    private val binding get() = _binding!!
    private var historySleep = listOf<SleepTimeEntity>()
    private var startTimeList = listOf<Float>()
    private var endTimeList = listOf<Float>()

    private var startSleep: Float = 0F
    private var endSleep: Float = 0F
    private var regularity: Float = 0F
    private var timeAsleep: Float = 0F
    private var timeBeforeSleep: Float = 0F
    private var startTimeMean = 0F
    private var endTimeMean = 0F

    private var cvStartTime = 0F
    private var cvEndTime = 0F
    private var startTimeRegularity = 0F
    private var endTimeRegularity = 0F

    private var sleepQuality = mutableListOf<Float>()

    private var userUID = ""

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val instant = Instant.now()

        binding.lastCheckedTxt.text = requireActivity().resources.getString(
            R.string.last_checked,
            convertEpochToJustDateTime(instant.epochSecond.toInt())
        )

        historySleepAnalysistViewModel.getUserSession.observe(viewLifecycleOwner) { User ->
            userUID = User.UID
            sleepViewModel.allSleepHistoryByUser(userUID, "")
                .observe(viewLifecycleOwner) { sleepHistory ->
                    historySleep = sleepHistory.filter {
                        it.endTime != null && it.endTime - it.startTime > 4000 && it.realStartTime != null
                    }
                    if (historySleep.isNotEmpty()) {

                        timeAsleep =
                            ((historySleep[0].endTime!!).minus(historySleep[0].startTime)).toFloat()
                        timeBeforeSleep =
                            (historySleep[0].realStartTime!!.minus((historySleep[0].startTime))).toFloat()

                        startTimeList =
                            historySleep.map { convertTimeStringToSeconds(convertEpochToHour(it.startTime)).toFloat() }

                        endTimeList =
                            historySleep.map { convertTimeStringToSeconds(convertEpochToHour(it.endTime!!)).toFloat() }

                        startSleep = startTimeList[0]
                        endSleep = endTimeList[0]


                        if (startTimeList.size > 1) {

                            startTimeMean = startTimeList.average().toFloat()

                            endTimeMean = endTimeList.average().toFloat()

                            cvStartTime =
                                calculateCoefficientOfVariation(startTimeList, startTimeMean)
                            cvEndTime = calculateCoefficientOfVariation(endTimeList, endTimeMean)

                            startTimeRegularity = 1.minus(cvStartTime)
                            endTimeRegularity = 1.minus(cvEndTime)
                            regularity = (startTimeRegularity.plus(endTimeRegularity)) / 2

                        } else {
                            regularity = 80F
                        }

                        val assetManager: AssetManager = requireContext().assets
                        val interpreter: Interpreter = loadModel(assetManager)
                        // Dummy input, not normalized yet. Click button to see the prediction
                        val features = featureNormalization(
                            startSleep,
                            endSleep,
                            regularity,
                            timeAsleep,
                            timeBeforeSleep
                        ) // Quality = 77
                        val result =
                            targetNormalizationInverse(performInference(interpreter, features))
                        val instant = Instant.now()

                        binding.lastCheckedTxt.text = requireActivity().resources.getString(
                            R.string.last_checked,
                            convertEpochToJustDateTime(instant.epochSecond.toInt())
                        )
                        binding.sleepQualityValueTxt.text =
                            requireActivity().resources.getString(R.string.result_quality, result.toString())
                        binding.sleepQualityStatusTxt.text = qualityCondition(result)
                        sleepViewModel.insertSleepQuality(SleepQualityEntity(1, result, userUID))

                    } else {
                        // TODO: Show message that there is no history
                    }
                }

            sleepViewModel.allSleepQuality(User.UID)
                .observe(viewLifecycleOwner) { qualityEntities ->
                    qualityEntities.forEach {
                        sleepQuality.add(it.sleepQuality)
                    }

                    binding.averageSleepQualityTxt.text = requireActivity().resources.getString(
                        R.string.result_quality,
                        sleepQuality.average().toInt().toString()
                    )
                }
        }

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
            axisLeft.textColor = requireActivity().getColor(R.color.white_100)
            axisRight.isEnabled = false

            description.isEnabled = false
            legend.isEnabled = false

            val confidenceEntries = ArrayList<Entry>()
            sleepQuality.forEach {
                confidenceEntries.add(Entry(sleepQuality.indexOf(it).toFloat(), it))
            }

            val arrayHistoryDataSet = LineDataSet(confidenceEntries, "History")
            arrayHistoryDataSet.setDrawFilled(true)
            arrayHistoryDataSet.fillDrawable =
                ContextCompat.getDrawable(requireActivity(), R.drawable.background_gradient_chart)
            arrayHistoryDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            arrayHistoryDataSet.cubicIntensity =
                0.2f

            val lineData = LineData(arrayHistoryDataSet)
            lineData.setDrawValues(true)
            lineData.setValueTextColor(requireActivity().getColor(R.color.white_100))
            data = lineData
        }


    }

    private fun loadModel(assetManager: AssetManager): Interpreter {
        val modelFilename = "model.tflite"
        val modelFileDescriptor: AssetFileDescriptor = assetManager.openFd(modelFilename)
        val inputStream = FileInputStream(modelFileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset: Long = modelFileDescriptor.startOffset
        val declaredLength: Long = modelFileDescriptor.declaredLength
        val modelByteBuffer: MappedByteBuffer =
            fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        return Interpreter(modelByteBuffer)
    }

    // Perform inference using the TensorFlow Lite model
    private fun performInference(interpreter: Interpreter, features: FloatArray): Float {
        val inputShape = interpreter.getInputTensor(0).shape()
        val outputShape = interpreter.getOutputTensor(0).shape()

        val inputBuffer = ByteBuffer.allocateDirect(inputShape[0] * inputShape[1] * 4)
        inputBuffer.order(ByteOrder.nativeOrder())

        for (feature in features) {
            inputBuffer.putFloat(feature)
        }
        val outputBuffer = ByteBuffer.allocateDirect(outputShape[0] * 4)
        outputBuffer.order(ByteOrder.nativeOrder())

        interpreter.run(inputBuffer, outputBuffer)
        return outputBuffer.getFloat(0)
    }

    // normalization func
    private fun featureNormalization(
        start: Float,
        end: Float,
        regularity: Float,
        timeAsleep: Float,
        timeBeforeSleep: Float
    ): FloatArray {
        val startNorm = (start - 21780) / (94380 - 21780)
        val endNorm = (end - 3900) / (84540 - 3900)
        val regularityNorm = (regularity + 1) / (100 + 1)
        val timeAsleepNorm = (timeAsleep / 45769.4)
        val timeBeforeSleepNorm = (timeBeforeSleep - 560) / (5677.7 - 560)

        return floatArrayOf(
            startNorm,
            endNorm,
            regularityNorm,
            timeAsleepNorm.toFloat(),
            timeBeforeSleepNorm.toFloat()
        )
    }

    private fun targetNormalizationInverse(quality: Float): Float {
        return quality * (100 - 7) + 7
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


}