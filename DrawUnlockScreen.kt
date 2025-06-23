package com.example.drawunlock.ui

import android.graphics.Bitmap
import android.graphics.Canvas as GCanvas
import android.graphics.Color as GColor
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.drawunlock.viewmodel.UnlockViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DrawUnlockScreen(viewModel: UnlockViewModel = viewModel()) {
    var paths by remember { mutableStateOf(listOf<List<Offset>>()) }
    var currentPath by remember { mutableStateOf(mutableListOf<Offset>()) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {

        Canvas(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> currentPath = mutableListOf(offset) },
                        onDragEnd = {
                            paths = paths + listOf(currentPath)
                            currentPath = mutableListOf()
                        },
                        onDrag = { _, offset ->
                            currentPath.add(offset)
                        }
                    )
                }
        ) {
            val stroke = Stroke(width = 6f)
            paths.forEach { line ->
                val path = Path().apply {
                    if (line.isNotEmpty()) moveTo(line.first().x, line.first().y)
                    line.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(path, Color.White, style = stroke)
            }

            val tempPath = Path().apply {
                if (currentPath.isNotEmpty()) moveTo(currentPath.first().x, currentPath.first().y)
                currentPath.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(tempPath, Color.Gray, style = stroke)
        }

        Button(
            onClick = {
                val bitmap = pathsToBitmap(paths + listOf(currentPath))
                viewModel.verifyDrawing(bitmap)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text("Verify")
        }
    }
}

// Converts the drawn paths to a Bitmap
fun pathsToBitmap(paths: List<List<Offset>>, size: Int = 224): Bitmap {
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = GCanvas(bitmap)
    val paint = Paint().apply {
        color = GColor.WHITE
        strokeWidth = 12f
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    paths.forEach { pathList ->
        for (i in 0 until pathList.size - 1) {
            val p1 = pathList[i]
            val p2 = pathList[i + 1]
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint)
        }
    }

    return Bitmap.createScaledBitmap(bitmap, 28, 28, true)
}
