package com.example.drawunlock.utils

fun argmax(array: FloatArray): Pair<Int, Float> {
    var maxIndex = 0
    var maxValue = array[0]

    for (i in 1 until array.size) {
        if (array[i] > maxValue) {
            maxValue = array[i]
            maxIndex = i
        }
    }

    return Pair(maxIndex, maxValue)
}
