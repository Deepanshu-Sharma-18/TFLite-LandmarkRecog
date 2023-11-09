package com.example.landmarkrecognition.domain

import android.content.Context
import android.graphics.Bitmap
import android.view.Surface
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class LandmarkClassifier(
    private val context : Context,
    private val threshold : Float = 0.05f,
    private val maxResult : Int = 1
) {

    private var classifier : ImageClassifier? = null

    init {
        val baseOptions = BaseOptions.builder()
            .setNumThreads(2)
            .build()

        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(maxResult)
            .setScoreThreshold(threshold)
            .build()

        try {
            classifier = ImageClassifier.createFromFileAndOptions(
                context,
                "landmarks.tflite",
                options
            )
        }catch (e : IllegalStateException){
            e.printStackTrace()
        }
    }

    fun classify( bitmap: Bitmap , rotation : Int) : List<Classification>{
        val imageProcessor = org.tensorflow.lite.support.image.ImageProcessor.Builder().build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrientation(rotation))
            .build()

        val results = classifier?.classify(tensorImage , imageProcessingOptions)

        return results?.flatMap { classification ->
            classification.categories.map{
                Classification(
                    name = it.displayName,
                    score = it.score
                )
            }
        }?.distinctBy {
            it.name
        } ?: emptyList()
    }


    private fun getOrientation(rotation: Int) : ImageProcessingOptions.Orientation{
        return when(rotation){
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }

}