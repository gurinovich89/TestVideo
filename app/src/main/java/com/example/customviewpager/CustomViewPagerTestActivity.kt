package com.example.customviewpager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.customviewpager.dummy.DummyContent
import com.example.testvideo.R
import kotlinx.android.synthetic.main.activity_custom_view_pager_test.*

class CustomViewPagerTestActivity : AppCompatActivity(),
    StringFragment.OnListFragmentInteractionListener {
    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
        //TODO
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view_pager_test)
    }

    override fun onStart() {
        super.onStart()
        val myAdapter = MyPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.viewPager.adapter = myAdapter
    }
}
