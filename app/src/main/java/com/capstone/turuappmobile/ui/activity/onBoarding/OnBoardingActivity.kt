package com.capstone.turuappmobile.ui.activity.onBoarding

import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.turuappmobile.data.db.SleepQualityEntity
import com.capstone.turuappmobile.data.db.SleepTimeEntity
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactory
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.ActivityOnBoardingBinding
import com.capstone.turuappmobile.ui.activity.home.HomeActivity
import com.capstone.turuappmobile.ui.activity.trackSleep.SleepViewModel
import com.capstone.turuappmobile.ui.fragment.historyAnalysis.HistorySleepAnalysistViewModel
import com.capstone.turuappmobile.utils.calculateCoefficientOfVariation
import com.capstone.turuappmobile.utils.convertEpochToHour
import com.capstone.turuappmobile.utils.convertTimeStringToSeconds
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class OnBoardingActivity : AppCompatActivity() {


    private lateinit var binding: ActivityOnBoardingBinding

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

    private var result = 0F

    private val sleepViewModel by viewModels<SleepViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val historySleepAnalysistViewModel by viewModels<HistorySleepAnalysistViewModel> {
        ViewModelFactoryUser.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historySleepAnalysistViewModel.getUserSession.observe(this) { User ->
            userUID = User.UID
            sleepViewModel.allSleepHistoryByUser(userUID, "")
                .observe(this) { sleepHistory ->
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

                        val assetManager: AssetManager = this.assets
                        val interpreter: Interpreter = loadModel(assetManager)
                        // Dummy input, not normalized yet. Click button to see the prediction
                        val features = featureNormalization(
                            startSleep,
                            endSleep,
                            regularity,
                            timeAsleep,
                            timeBeforeSleep
                        ) // Quality = 77
                        result =
                            targetNormalizationInverse(performInference(interpreter, features))


                    }
                    sleepViewModel.allSleepQuality(User.UID)
                        .observe(this) { qualityEntities ->
                            if (qualityEntities.isNotEmpty()) {
                                qualityEntities.forEach {

                                    sleepQuality.add(it.sleepQuality)
                                }

                                if (qualityEntities.last().sleepQuality != result) {
                                    sleepViewModel.insertSleepQuality(
                                        SleepQualityEntity(
                                            null,
                                            result,
                                            userUID
                                        )
                                    )
                                    sleepQuality.add(result)
                                }

                            } else {
                                if (historySleep.size == 1) {
                                    sleepViewModel.insertSleepQuality(
                                        SleepQualityEntity(
                                            null,
                                            result,
                                            userUID
                                        )
                                    )
                                }
                            }

                            updateUi()

                        }

                }


        }


    }

    private fun updateUi() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun loadModel(assetManager: AssetManager): Interpreter {
        val modelFilename = "model_augmented.tflite"
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
        val startNorm = (start - 60) / (86340 - 60)
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