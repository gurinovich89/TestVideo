@file:Suppress("unused")

package com.example.view

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.testvideo.R
import java.util.*

class StoriesProgressView : LinearLayout {

    private var spaceWidth = 5f
    private val progressBarLayoutParam =
        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
    private val spaceLayoutParam =
        LinearLayout.LayoutParams(spaceWidth.toInt(), LinearLayout.LayoutParams.MATCH_PARENT)
    private val defaultColor = ContextCompat.getColor(context, R.color.colorAccent)
    private val defaultBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimary)

    private var progressColor = defaultColor
    private var progressBackgroundColor = defaultBackgroundColor

    private val progressBars = ArrayList<PausableProgressBar>()

    private var storiesCount = -1
    /**
     * pointer of running animation
     */
    private var current = -1
    private var storiesListener: StoriesListener? = null
    internal var isComplete: Boolean = false

    interface StoriesListener {
        fun onNext()

        fun onPrev()

        fun onComplete()
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        orientation = LinearLayout.HORIZONTAL
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StoriesProgressView)
        progressColor =
            typedArray.getColor(R.styleable.StoriesProgressView_progressColor, defaultColor)
        progressBackgroundColor = typedArray.getColor(
            R.styleable.StoriesProgressView_progressBackgroundColor,
            defaultBackgroundColor
        )
        spaceWidth = typedArray.getDimension(
            R.styleable.StoriesProgressView_progressSpace,
            spaceWidth
        )
        typedArray.recycle()
        spaceLayoutParam.width = spaceWidth.toInt()
        bindViews()
    }

    private fun bindViews() {
        progressBars.clear()
        removeAllViews()

        for (i in 0 until storiesCount) {
            val p = createProgressBar()
            progressBars.add(p)
            addView(p)
            if (i + 1 < storiesCount) {
                addView(createSpace())
            }
        }
    }

    private fun createProgressBar(): PausableProgressBar {
        val p = PausableProgressBar(context)
        p.progressColor = progressColor
        p.progressBackgroundColor = progressBackgroundColor
        p.layoutParams = progressBarLayoutParam
        return p
    }

    private fun createSpace(): View {
        val v = View(context)
        v.layoutParams = spaceLayoutParam
        return v
    }

    /**
     * Set story count and create views
     *
     * @param storiesCount story count
     */
    private fun setStoriesCount(storiesCount: Int) {
        this.storiesCount = storiesCount
        bindViews()
    }

    /**
     * Set storiesListener
     *
     * @param storiesListener StoriesListener
     */
    fun setStoriesListener(storiesListener: StoriesListener) {
        this.storiesListener = storiesListener
    }

    /**
     * Set a story's duration
     *
     * @param duration millisecond
     */
    fun setStoryDuration(storiesCount: Int, duration: Long) {
        setStoriesCount(storiesCount)
        for (i in progressBars.indices) {
            progressBars[i].setDuration(duration)
            progressBars[i].setCallback(callback(i))
        }
    }

    fun updateDuration(storiesIndex: Int, duration: Long, initDuration: Long? = null) {
        val progressBar = progressBars.getOrNull(storiesIndex)
        progressBar?.let {
            progressBar.setDuration(duration)
            if (current == storiesIndex) {
                progressBar.startProgress(initDuration ?: 0)
            }
        }
    }

    /**
     * Set stories count and each story duration
     *
     * @param durations milli
     */
    fun setStoriesCountWithDurations(durations: LongArray) {
        setStoriesCount(durations.size)
        for (i in progressBars.indices) {
            progressBars[i].setDuration(durations[i])
            progressBars[i].setCallback(callback(i))
        }
    }

    private fun callback(index: Int): PausableProgressBar.Callback {
        return object : PausableProgressBar.Callback {

            override fun onStartProgress() {
                current = index
            }

            override fun onFinishProgress() {
                val next = current + 1
                if (next <= progressBars.size - 1) {
                    storiesListener?.onNext()
                } else {
                    isComplete = true
                    storiesListener?.onComplete()
                }
            }
        }
    }

    fun startNextStory() {
        val next = current + 1
        if (next <= progressBars.size - 1) {
            current = next
            startStories(next)
        }
    }

    fun startPrevStory() {
        val prev = current - 1
        if (prev >= 0 && prev < progressBars.size) {
            current = prev
            startStories(prev)
        }
    }

    fun startStories(from: Int = 0, initDuration: Long = 0) {
        if (progressBars.size == 0) return
        for (i in progressBars.indices) {
            if (i < from) {
                progressBars[i].setMaxWithoutCallback()
            } else {
                progressBars[i].setMinWithoutCallback()
            }
        }
        progressBars[from].startProgress(initDuration)
    }

    fun clear() {
        progressBars.clear()
        storiesCount = -1
        current = -1
        storiesListener = null
        isComplete = false
    }

    /**
     * Need to call when Activity or Fragment destroy
     */
    fun destroy() {
        clear()
        for (p in progressBars) {
            p.clear()
        }
    }

    /**
     * Pause story
     */
    fun pause(): Long {
        if (current < 0) return 0
        return progressBars.getOrNull(current)?.pauseProgress() ?: 0
    }

    /**
     * Resume story
     */
    fun resume() {
        if (current < 0) return
        progressBars.getOrNull(current)?.resumeProgress()
    }
}
