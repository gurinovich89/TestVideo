package com.example.customviewpager

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class CustomViewPager2 : FrameLayout {

    private var mLastMotionY: Int = 0
    private var mLastMotionX: Int = 0

    private var mTouchSlop: Int = 0

    lateinit var viewPager: ViewPager2
    lateinit var fakeView: View

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        mTouchSlop = ViewConfiguration.get(getContext()).scaledPagingTouchSlop
        Log.i("west", "mTouchSlop=$mTouchSlop")
        viewPager = ViewPager2(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        viewPager.isNestedScrollingEnabled = true
        addView(viewPager)

        /*fakeView = View(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        fakeView.elevation = 100f
        fakeView.setBackgroundColor(resources.getColor(R.color.colorFake))
        addView(fakeView)*/
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        Log.i("west", "onTouchEvent event=$ev")
        return super.onTouchEvent(ev)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.i("west", "dispatchTouchEvent event=$ev")
        var isVerticalScroling = false
        var isHorizontalScrolling = false
        val action = ev.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_MOVE -> {
                val y = ev.y.toInt()
                val x = ev.x.toInt()
                val yDiff = Math.abs(y - mLastMotionY)
                val xDiff = Math.abs(x - mLastMotionX)
                if (yDiff > mTouchSlop) {
                    mLastMotionY = y
                    mLastMotionX = x
                    if (yDiff > xDiff) {
                        isVerticalScroling = true
                    }
                }
                if (!isVerticalScroling) {
                    if (xDiff > mTouchSlop && xDiff > yDiff * 2) {
                        isHorizontalScrolling = true
                    }
                }
            }

            MotionEvent.ACTION_DOWN -> {
                mLastMotionY = ev.y.toInt()
                mLastMotionX = ev.x.toInt()
            }
        }

        if (ev.action == MotionEvent.ACTION_MOVE) {
            if (isVerticalScroling) {
                val newEvent = MotionEvent.obtain(ev)
                newEvent.setLocation(mLastMotionX.toFloat(), ev.y)
                val rvPager = viewPager.getChildAt(0) as? RecyclerView
                val itemView = rvPager?.layoutManager?.findViewByPosition(viewPager.currentItem)
                itemView?.dispatchTouchEvent(newEvent)
                //viewPager.dispatchTouchEvent(newEvent)
            } else if (isHorizontalScrolling) {
                val newEvent = MotionEvent.obtain(ev)
                newEvent.setLocation(ev.x, ev.y)
                viewPager.dispatchTouchEvent(newEvent)
            }
            return true
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.i("west", "onInterceptTouchEvent event=$ev")
        return super.onInterceptTouchEvent(ev)
    }
}
