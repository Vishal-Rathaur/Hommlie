package com.hommlie.user.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.hommlie.user.R


class StepProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val circleRadius = 25f
    private val lineWidth = 5f
    private val sideMargin = 30f.dpToPx(context) // Convert 10dp to pixels
    private val steps = 5 // Total steps
    private var currentStep = -1 // Current active step (-1 means no steps yet)
    private var animatedLineProgress = 0f // Progress for the active line animation
    private var stepSpacing = 0f // Dynamic spacing between steps

    private val activeCirclePaint = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.holo_green_dark)
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val inactiveCirclePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.LightGray_Color)
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
    }

    private val activeLinePaint = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.holo_green_dark)
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        isAntiAlias = true
    }

    private val inactiveLinePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.LightGray_Color)
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        isAntiAlias = true
    }

    init {
        setWillNotDraw(false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Calculate dynamic spacing based on the width
        val totalAvailableWidth = w - (2 * sideMargin) - (2 * circleRadius)
        stepSpacing = totalAvailableWidth / (steps - 1)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val startX = sideMargin + circleRadius
        val centerY = height / 2f
        var currentX = startX

        for (i in 0 until steps) {
            // Draw circles
            val paint = if (i <= currentStep) activeCirclePaint else inactiveCirclePaint
            canvas.drawCircle(currentX, centerY, circleRadius, paint)

            // Draw lines between circles
            if (i < steps - 1) {
                val nextX = currentX + stepSpacing
                if (i < currentStep) {
                    // Fully active line (green)
                    canvas.drawLine(currentX + circleRadius, centerY, nextX - circleRadius, centerY, activeLinePaint)
                } else if (i == currentStep) {
                    // Animate the active line
                    canvas.drawLine(
                        currentX + circleRadius, centerY,
                        currentX + circleRadius + animatedLineProgress, centerY,
                        activeLinePaint
                    )
                    // Draw the remaining inactive part of the line in gray
                    canvas.drawLine(
                        currentX + circleRadius + animatedLineProgress, centerY,
                        nextX - circleRadius, centerY,
                        inactiveLinePaint
                    )
                } else {
                    // Fully inactive line (gray)
                    canvas.drawLine(currentX + circleRadius, centerY, nextX - circleRadius, centerY, inactiveLinePaint)
                }
                currentX = nextX
            }
        }
    }

    fun animateToStep(step: Int) {
        if (step < 0 || step >= steps || step <= currentStep) return

        val totalAnimationDuration = 800L // Animation duration for each step
        val lineTargetLength = stepSpacing - 2 * circleRadius

        var stepCounter = currentStep + 1

        fun animateNextStep() {
            if (stepCounter > step) return
            ValueAnimator.ofFloat(0f, lineTargetLength).apply {
                duration = totalAnimationDuration
                addUpdateListener { valueAnimator ->
                    animatedLineProgress = valueAnimator.animatedValue as Float
                    invalidate() // Force redraw during the animation
                }
                doOnEnd {
                    currentStep = stepCounter
                    animatedLineProgress = 0f
                    invalidate()
                    stepCounter++
                    animateNextStep()
                }
                start()
            }
        }

        animateNextStep()
    }

    private fun Float.dpToPx(context: Context): Float {
        return this * context.resources.displayMetrics.density
    }
}
