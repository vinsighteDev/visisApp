package com.example.visis.utils

import android.graphics.Bitmap
import android.os.AsyncTask
import android.widget.Button
import android.widget.TextView
import com.example.visis.domain.ColorRecognitionTask
import com.example.visis.domain.ObjectRecognitionTask
import com.example.visis.domain.SceneRecognitionTask
import com.example.visis.domain.TextRecognitionTask
import com.example.visis.presentation.view.MainActivity
import java.io.ByteArrayOutputStream

class Recognizer(mainActivity: MainActivity?) {
    private val textView: TextView? = null
    private val sendButton: Button? = null

    private var mainActivity: MainActivity? = null


    fun doTextRecognition(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        val visionTask = TextRecognitionTask(mainActivity!!)
        visionTask.execute(outputStream.toByteArray())
    }

    fun doSceneRecognition(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        val visionTask: SceneRecognitionTask<ByteArray, String, String> =
            SceneRecognitionTask(mainActivity!!)

        visionTask.execute(outputStream.toByteArray())
    }

    fun doObjectRecognition(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        val visionTask: ObjectRecognitionTask<ByteArray, String, String> =
            ObjectRecognitionTask(mainActivity!!)

        visionTask.execute(outputStream.toByteArray())
    }

    fun doColorRecognition(bitmap: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        val visionTask: ColorRecognitionTask<ByteArray, String, String> =
            ColorRecognitionTask(mainActivity!!)

        visionTask.execute(outputStream.toByteArray())
    }
}