package com.android.xg.ambulance.export.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

public class MainPagerAdapter(
    fm: FragmentManager?,
    fragments: ArrayList<Fragment>,
    title: Array<String>
) :
    FragmentPagerAdapter(fm!!) {
    var mFragments: ArrayList<Fragment> = fragments
    var mTitles: Array<String> = title

    override fun getPageTitle(position: Int): CharSequence {
        return mTitles[position]
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mFragments.size
    }
}