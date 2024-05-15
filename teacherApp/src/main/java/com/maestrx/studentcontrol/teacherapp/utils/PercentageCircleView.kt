package com.maestrx.studentcontrol.teacherapp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.maestrx.studentcontrol.teacherapp.R

class PercentageCircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var percentage: Float = 0f
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 30f
    }
    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 30f
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.BW, null)
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }
    private val percentagePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.BW, null)
        textSize = 128f
        textAlign = Paint.Align.CENTER
    }

    fun setPercentage(value: Float) {
        percentage = value
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = width.coerceAtMost(height) / 2 - circlePaint.strokeWidth
        val cx = width / 2
        val cy = height / 2

        canvas.drawCircle(cx, cy, radius, circlePaint)

        val sweepAngle = 360 * (percentage / 100)
        canvas.drawArc(
            cx - radius, cy - radius, cx + radius, cy + radius,
            -90f, sweepAngle, false, arcPaint
        )

        val percentageText = "${percentage.toInt()}%"
        val percentageTextY = cy - (percentagePaint.descent() + percentagePaint.ascent()) / 2 + 20
        canvas.drawText(percentageText, cx, percentageTextY, percentagePaint)

        val labelText = resources.getText(R.string.attendance).toString()
        val labelTextY = percentageTextY - percentagePaint.textSize
        canvas.drawText(labelText, cx, labelTextY, labelPaint)
    }
}