package com.hommlie.user.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan

class CustomBulletSpan(private val gapWidth: Int, private val bulletColor: Int) :
    LeadingMarginSpan {
    private val bulletRadius = 7 // Adjust size of the bullet dot

    override fun getLeadingMargin(first: Boolean): Int {
        return 2 * bulletRadius + gapWidth
    }

    override fun drawLeadingMargin(
        c: Canvas, p: Paint, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int, text: CharSequence?, start: Int, end: Int, first: Boolean, layout: Layout?
    ) {
        if (first) {
            val oldStyle = p.style
            val oldColor = p.color

            p.style = Paint.Style.FILL
            p.color = bulletColor // Set custom color

            c.drawCircle(x + dir * bulletRadius.toFloat(), (top + bottom) / 2f, bulletRadius.toFloat(), p)

            p.style = oldStyle
            p.color = oldColor
        }
    }
}