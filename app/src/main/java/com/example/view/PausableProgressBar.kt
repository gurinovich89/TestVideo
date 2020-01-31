package com.example.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.Transformation
import android.widget.FrameLayout
import com.example.testvideo.R

@SuppressLint("ViewConstructor")
class PausableProgressBar : FrameLayout {

    private val frontProgressView: View?
    private val backProgressView: View?

    private var animation: PausableScaleAnimation? = null
    private var duration = DEFAULT_PROGRESS_DURATION.toLong()
    private var callback: Callback? = null

    var progressBackgroundColor: Int = Color.GRAY
        set(value) {
            backProgressView?.setBackgroundColor(value)
        }

    var progressColor: Int = Color.RED
        set(value) {
            frontProgressView?.setBackgroundColor(value)
        }


    interface Callback {
        fun onStartProgress()
        fun onFinishProgress()
    }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.pausable_progress, this)
        frontProgressView = findViewById(R.id.front_progress)
        backProgressView = findViewById(R.id.back_progress)
        backProgressView?.setBackgroundColor(progressBackgroundColor)
        frontProgressView?.setBackgroundColor(progressColor)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
    }

    fun setDuration(duration: Long) {
        this.duration = duration
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setMax() {
        finishProgress()
        frontProgressView?.clearAnimation()
        frontProgressView?.visibility = View.VISIBLE
    }

    fun setMin() {
        finishProgress()
    }

    fun setMinWithoutCallback() {
        animation?.setAnimationListener(null)
        animation?.cancel()
        frontProgressView?.visibility = View.INVISIBLE
    }

    fun setMaxWithoutCallback() {
        animation?.setAnimationListener(null)
        animation?.cancel()
        frontProgressView?.clearAnimation()
        frontProgressView?.visibility = View.VISIBLE
    }

    private fun finishProgress() {
        animation?.setAnimationListener(null)
        animation?.cancel()
        callback?.onFinishProgress()
    }

    fun startProgress(initDuration: Long = 0) {
        if (initDuration < 0 || initDuration > duration) {
            throw IllegalArgumentException("initDuration should be in interval 0..$duration")
        }
        val fromX = initDuration.toFloat() / duration
        animation = PausableScaleAnimation(
            fromX, 1f, 1f, 1f, Animation.ABSOLUTE, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        animation?.duration = duration - initDuration
        animation?.interpolator = LinearInterpolator()
        animation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                frontProgressView?.visibility = View.VISIBLE
                callback?.onStartProgress()
            }

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                callback?.onFinishProgress()
            }
        })
        animation?.fillAfter = true
        frontProgressView?.startAnimation(animation)
    }

    fun pauseProgress(): Long {
        val fromDuration = animation?.let { this.duration - it.duration } ?: 0
        val deltaAnimation = animation?.pause() ?: 0
        return fromDuration + deltaAnimation
    }

    fun resumeProgress() {
        animation?.resume()
    }

    fun clear() {
        animation?.setAnimationListener(null)
        animation?.cancel()
        animation = null
    }

    private class PausableScaleAnimation internal constructor(
        fromX: Float,
        toX: Float,
        fromY: Float,
        toY: Float,
        pivotXType: Int,
        pivotXValue: Float,
        pivotYType: Int,
        pivotYValue: Float
    ) : ScaleAnimation(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue) {

        private var mElapsedAtPause: Long = 0
        private var mPaused = false
        private var mPausedDuration: Long = 0

        override fun getTransformation(
            currentTime: Long,
            outTransformation: Transformation,
            scale: Float
        ): Boolean {
            if (!mPaused) {
                mPausedDuration = (currentTime - startTime)
            }
            if (mPaused && mElapsedAtPause == 0L) {
                mElapsedAtPause = currentTime - startTime
            }
            if (mPaused) {
                startTime = currentTime - mElapsedAtPause
            }
            return super.getTransformation(currentTime, outTransformation, scale)
        }

        /***
         * pause animation
         */
        internal fun pause(): Long {
            if (mPaused) return mPausedDuration
            mElapsedAtPause = 0
            mPaused = true
            return mPausedDuration
        }

        /***
         * resume animation
         */
        internal fun resume() {
            mPaused = false
        }
    }

    companion object {
        private const val DEFAULT_PROGRESS_DURATION = 2000
    }
}
