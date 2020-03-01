package com.example.customviewpager

import android.graphics.Color
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyPagerAdapter(
    @NonNull fragmentManager: FragmentManager,
    @NonNull lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        val color = when (position) {
            0 -> Color.RED
            1 -> Color.YELLOW
            2 -> Color.GREEN
            else -> Color.WHITE
        }
        return StringFragment.newInstance(color)
    }

}