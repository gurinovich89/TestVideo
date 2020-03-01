package com.example.customviewpager

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.customviewpager.dummy.DummyContent
import com.example.customviewpager.dummy.DummyContent.DummyItem
import com.example.testvideo.R

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [StringFragment.OnListFragmentInteractionListener] interface.
 */
class StringFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1
    private var testBackgroundColor = Color.TRANSPARENT

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT, columnCount)
            testBackgroundColor = it.getInt(ARG_COLOR, testBackgroundColor)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_string_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyStringRecyclerViewAdapter(DummyContent.ITEMS, listener)
            }
        }
        view.setBackgroundColor(testBackgroundColor)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: DummyItem?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_COLOR = "ARG_COLOR"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(color: Int) =
            StringFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLOR, color)
                }
            }
    }
}
