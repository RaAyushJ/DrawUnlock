package com.example.drawunlock.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.drawunlock.utils.argmax
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class UnlockViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    // Load model from assets
    private val tfliteModel: Interpreter by lazy {
        val assetFileDescriptor = context.assets.openFd("model.tflite")
        val fileInputStream = assetFileDescriptor.createInputStream()
        val modelBytes = ByteArray(assetFileDescriptor.length.toInt())
        fileInputStream.read(modelBytes)
        fileInputStream.close()

        val byteBuffer = ByteBuffer.allocateDirect(modelBytes.size)
        byteBuffer.order(ByteOrder.nativeOrder())
        byteBuffer.put(modelBytes)
        byteBuffer.rewind()

        Interpreter(byteBuffer)
    }

    private val labels = listOf(  // Replace with your actual label list
        "apple", "cat", "tree", "house", "car", "book", "sun", "cloud", "fish", "star"
    )

    private val currentKey = "apple"  // Simulate stored keyword

    fun verifyDrawing(bitmap: Bitmap) {
        val input = preprocess(bitmap)
        val output = Array(1) { FloatArray(labels.size) }

        tfliteModel.run(input, output)

        val (predictedIndex, confidence) = argmax(output[0])
        val predictedLabel = labels[predictedIndex]

        Log.d("DrawUnlock", "Prediction: $predictedLabel ($confidence)")

        if (predictedLabel == currentKey && confidence > 0.85f) {
            Toast.makeText(context, "Unlocked!", Toast.LENGTH_SHORT).show()
            // TODO: Implement unlock logic
        } else {
            Toast.makeText(context, "Try again!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun preprocess(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val resized = Bitmap.createScaledBitmap(bitmap, 28, 28, true)
        val input = Array(1) { Array(28) { Array(28) { FloatArray(1) } } }

        for (y in 0 until 28) {
            for (x in 0 until 28) {
                val pixel = resized.getPixel(x, y)
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                val gray = (r + g + b) / 3.0f / 255.0f
                input[0][y][x][0] = gray
            }
        }

        return input
    }
}
