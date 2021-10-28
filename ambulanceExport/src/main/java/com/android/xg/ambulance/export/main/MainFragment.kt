package com.android.xg.ambulance.export.main

import android.view.View
import androidx.fragment.app.Fragment
import com.android.xg.ambulance.export.R
import com.android.xg.ambulance.export.databinding.FragmentMainBinding
import com.android.xg.ambulance.export.image.DataBean
import com.android.xg.ambulance.export.image.ImageNetAdapter
import com.top.arch.base.BaseXFragment
import com.top.arch.base.getChildFragment
import com.youth.banner.indicator.CircleIndicator
import java.util.*

class MainFragment : BaseXFragment<FragmentMainBinding>() {

    private lateinit var bannerAdapter: ImageNetAdapter
    private val mFragments: ArrayList<Fragment> = ArrayList()

    private var mAdapter: MainPagerAdapter? = null


    private val mTitles = arrayOf(
        "会议安排", "历史会议"
    )

    override fun getLayout(): Int {
        return R.layout.fragment_main
    }

    override fun init(view: View?) {
        initBanner()
        initFragments()
    }

    private fun initBanner() {
        var data: MutableList<DataBean> = mutableListOf(
            DataBean(
                0,
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpx.thea.cn%2FPublic%2FUpload%2FUploadfiles%2Fimage%2F20191027%2F20191027170651_10259.jpg&refer=http%3A%2F%2Fpx.thea.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1636786714&t=05c54fe6586894c61e9befb5b25dd716",
                "1",
                0
            ), DataBean(
                0,
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fdingyue.ws.126.net%2F2019%2F1227%2Ff59cd348j00q35z8c001ec200u000dqg00w000en.jpg&refer=http%3A%2F%2Fdingyue.ws.126.net&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1636786714&t=38d530a24292463dd64c39cbc960d35a",
                "2",
                0
            ), DataBean(
                0,
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Finews.gtimg.com%2Fnewsapp_match%2F0%2F11488649821%2F0.jpg&refer=http%3A%2F%2Finews.gtimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1636786714&t=2a0c1f745d06afa27f6bb923dd74d2af",
                "2",
                0
            )
        )

        bannerAdapter = ImageNetAdapter()
        bannerAdapter.addDates(data)
        bannerAdapter.setHasStableIds(false)
        mDataBinding.bannerMain.adapter = bannerAdapter
        mDataBinding.bannerMain.indicator = CircleIndicator(requireContext())
        mDataBinding.bannerMain.start()
    }

    private fun initFragments() {
        mFragments.clear()
        mFragments.add(MeetFragment())
        mFragments.add(MeetHistoryFragment())
        mAdapter = MainPagerAdapter(childFragmentManager, mFragments, mTitles)
        mDataBinding.viewpager.adapter = mAdapter
        mDataBinding.tabLayout.setViewPager(mDataBinding.viewpager)
    }

    fun meetFragment(): MeetFragment? {
        return getChildFragment(MeetFragment::class.java)
    }
}