package com.jdasense.app.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jdasense.app.R

class WaveformView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = context.getColor(R.color.hospital_blue_primary)
        strokeWidth = 4f
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private var amplitudes = mutableListOf<Float>()
    private val maxAmplitudes = 100

    fun addAmplitude(amplitude: Float) {
        amplitudes.add(amplitude)
        if (amplitudes.size > maxAmplitudes) {
            amplitudes.removeAt(0)
        }
        invalidate()
    }

    fun clear() {
        amplitudes.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (amplitudes.isEmpty()) return

        val midY = height / 2f
        val spacing = width.toFloat() / maxAmplitudes

        for (i in 0 until amplitudes.size - 1) {
            val startX = i * spacing
            val startY = midY - (amplitudes[i] * height / 2f)
            val stopX = (i + 1) * spacing
            val stopY = midY - (amplitudes[i + 1] * height / 2f)
            canvas.drawLine(startX, startY, stopX, stopY, paint)
        }
    }
}
