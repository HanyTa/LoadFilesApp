package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates.observable

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private lateinit var canvas: Canvas
    private var widthSize = 0
    private var heightSize = 0
    private var buttonColor: Int = R.attr.buttonBackgroundColor
    private var text: String = resources.getString(R.string.button_name)
    private var progressValue: Float = 0f
    private val rectF = RectF()
    val rectPaint = Paint().apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.colorPrimary)
    }

    val textPaint = Paint().apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.white)
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
    }

    val circlePaint = Paint().apply {
        color = resources.getColor(R.color.colorAccent)
    }

    val colorPaint = Paint().apply {
        color = resources.getColor(R.color.colorPrimaryDark)
    }
    private var valueAnimator = ValueAnimator()

    private var rotationCircleAngle: Int = 0

    private var buttonState: ButtonState by observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                setButtonText(resources.getString(R.string.button_loading))
                setButtonBackground(resources.getColor(R.color.colorPrimaryDark))
                valueAnimator = ValueAnimator.ofFloat(0f, 1f)
                valueAnimator.addUpdateListener {
                    progressValue = it.animatedValue as Float
                    invalidate()
                }
                valueAnimator.repeatCount = ValueAnimator.INFINITE
                valueAnimator.repeatMode = ValueAnimator.RESTART
                valueAnimator.duration = 2500
                valueAnimator.start()
                isEnabled = false
            }
            ButtonState.Completed -> {
                setButtonText(resources.getString(R.string.button_name))
                setButtonBackground(resources.getColor(R.color.colorPrimary))
                valueAnimator.cancel()
                progressValue = 0f
                isEnabled = true
            }

            else -> {}
        }
        invalidate()
    }

    private var rect = Rect()

    init {
        isClickable = true
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0).apply {

            try {
                text = getString(R.styleable.LoadingButton_buttonText).toString()
                buttonColor = resources.getColor(R.color.colorPrimary)
            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0, 0, measuredWidth, measuredHeight)
//        rectF.set(
//            measuredWidth.toFloat()/2 + text.length + paddingStart, 10f,
//            30f, measuredHeight.toFloat() - 10f
//        )
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(buttonColor)
        canvas.drawRect(rect, rectPaint)
        if (buttonState == ButtonState.Loading) {
            var progress: Float = progressValue * measuredWidth.toFloat()
            canvas.drawRect(0f, 0f, progress, measuredHeight.toFloat(), colorPaint)
            progress = progressValue * 360f
            canvas.drawArc(
                measuredWidth.toFloat() -200f,
                30f,
                measuredWidth.toFloat() - 200f + 30f,
                measuredHeight.toFloat() - 30f,
                0f,
                progress,
                true,
                circlePaint
            )
        }
        canvas.drawText(text, measuredWidth.toFloat()/2,measuredHeight.toFloat()/2, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }


    fun setState(state: ButtonState) {
        buttonState = state
    }

    private fun setButtonText(text: String) {
        this.text = text
        invalidate()
    }

    private fun setButtonBackground(color: Int) {
        buttonColor = color
        invalidate()
    }
}


