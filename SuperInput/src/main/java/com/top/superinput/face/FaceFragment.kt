package com.top.superinput.face

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.viewpager2.widget.ViewPager2
import com.top.superinput.R
import com.top.superinput.base.BaseInputFragment
//import com.top.superinput.utils.SoftKeyBoardUtil

class FaceFragment : BaseInputFragment() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var tableLayout: TableLayout

    companion object {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_face, container, false)
        val params = view.layoutParams
       // params.height = SoftKeyBoardUtil.getSoftKeyBoardHeight()
        view.layoutParams = params
        viewPager2 = view.findViewById(R.id.viewpager2)
        tableLayout = view.findViewById(R.id.table_layout)

        return view
    }


}