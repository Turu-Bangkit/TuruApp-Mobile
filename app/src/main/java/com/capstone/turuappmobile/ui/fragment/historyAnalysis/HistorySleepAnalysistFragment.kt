package com.capstone.turuappmobile.ui.fragment.historyAnalysis

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.db.SleepTimeEntity
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactory
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.FragmentHistorySleepAnalysistBinding
import com.capstone.turuappmobile.ui.activity.trackSleep.SleepViewModel
import com.capstone.turuappmobile.ui.fragment.historyList.HistorySleepListViewModel
import com.capstone.turuappmobile.utils.calculateCoefficientOfVariation
import com.capstone.turuappmobile.utils.calculateStandardDeviation
import com.capstone.turuappmobile.utils.convertEpochToHour
import com.capstone.turuappmobile.utils.convertTimeStringToSeconds
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


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

        historySleepAnalysistViewModel.getUserSession.observe(viewLifecycleOwner) { User ->
            sleepViewModel.allSleepHistoryByUser(User.UID)
                .observe(viewLifecycleOwner) { sleepHistory ->
                    historySleep = sleepHistory
                    if (historySleep.isNotEmpty()) {

                        startTimeList =
                            historySleep.map { convertTimeStringToSeconds(convertEpochToHour(it.startTime)).toFloat() }

                        endTimeList =
                            historySleep.map { convertTimeStringToSeconds(convertEpochToHour(it.endTime!!)).toFloat() }

                        startSleep = startTimeList[0]
                        endSleep = endTimeList[0]
                        timeAsleep = endSleep.minus(startSleep)

                        if (startTimeList.size > 1) {

                            startTimeMean = startTimeList.average().toFloat()

                            endTimeMean = endTimeList.average().toFloat()

                            cvStartTime =
                                calculateCoefficientOfVariation(startTimeList, startTimeMean)
                            cvEndTime = calculateCoefficientOfVariation(endTimeList, endTimeMean)

                            startTimeRegularity = 1.minus(cvStartTime)
                            endTimeRegularity = 1.minus(cvEndTime)
                            regularity = (startTimeRegularity.plus(endTimeRegularity)) / 2

                        }else{
                            regularity = 80F
                        }

                    } else {
                        // TODO: Show message that there is no history
                    }
                }
        }


        val assetManager: AssetManager = requireContext().assets
        val interpreter: Interpreter = loadModel(assetManager)
        // Dummy input, not normalized yet. Click button to see the prediction
        val features = featureNormalization(startSleep, endSleep, regularity, timeAsleep, timeBeforeSleep) // Quality = 77
        val result = targetNormalizationInverse(performInference(interpreter, features))
        binding.btnAnalysis.setOnClickListener {
            binding.textView3.text = result.toString()
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


}