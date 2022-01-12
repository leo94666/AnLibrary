package com.android.xg.ambulance.export.main


import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.xg.ambulance.export.R
import com.android.xg.ambulance.export.databinding.FragmentMeetBinding
import com.android.xg.ambulance.export.meet.MeetActivity
import com.android.xg.ambulancelib.AmbulanceViewModel
import com.android.xg.ambulancelib.bean.MeetHistoryResultBean
import com.android.xg.ambulancelib.bean.MeetInfoResultBean
import com.android.xg.ambulancelib.websocket.ActionInviteBean
import com.top.arch.base.BaseXFragment
import javax.inject.Inject

class MeetFragment : BaseXFragment<FragmentMeetBinding>() {

    lateinit var mMeetAdapter: MeetAdapter

    @Inject
    var loginViewModel: AmbulanceViewModel? = null

    override fun init(root: View?) {
        loginViewModel = ViewModelProvider(this).get(AmbulanceViewModel::class.java)

        mMeetAdapter = MeetAdapter(requireContext())
        mDataBinding.rvMain.layoutManager = LinearLayoutManager(requireContext())
        mDataBinding.rvMain.adapter = mMeetAdapter

        mMeetAdapter.setOnSelectedListener(object : MeetAdapter.OnSelectedListener {
            override fun onSelectedItem(position: MeetHistoryResultBean.MeetHistoryBean) {
                var actionInviteBean =
                    ActionInviteBean(position.roomNo!!, position.screenNum!!, position.source!!,position.meetingId!!)

                loginViewModel?.meetInfo(position.meetingId,
                    object : AmbulanceViewModel.ActionResult {
                        override fun onError() {

                        }

                        override fun onSuccess(any: Any?) {
                            if (any is MeetInfoResultBean.MeetInfoBean?) {
                                MeetActivity.startMeetActivity(
                                    requireContext(),
                                    any?.initiator?.userName!!,
                                    actionInviteBean
                                )
                            }
                        }
                    })
            }
        })

        update()
        initRefresh()
    }

    private fun initRefresh() {
        mDataBinding.smartRefresh.setEnableAutoLoadMore(false)
        mDataBinding.smartRefresh.setOnRefreshListener {
            update()
        }

    }

    override fun getLayout(): Int {
        return R.layout.fragment_meet
    }

    fun update() {
        loginViewModel?.aliveMeet(object : AmbulanceViewModel.ActionResult {
            override fun onError() {
                mDataBinding.smartRefresh.finishRefresh(false)

            }

            override fun onSuccess(any: Any?) {
                if (any is ArrayList<*>) {
                    if (any.size == 0) {
                        Toast.makeText(context, "数据为空！", Toast.LENGTH_SHORT).show()
                        mDataBinding.tvEmpty.visibility = View.VISIBLE
                        mDataBinding.rvMain.visibility = View.GONE
                        mDataBinding.smartRefresh.finishRefresh(false)

                    } else {
                        mDataBinding.rvMain.visibility = View.VISIBLE
                        mDataBinding.tvEmpty.visibility = View.GONE
                        mMeetAdapter.updateData(any as MutableList<MeetHistoryResultBean.MeetHistoryBean>)
                        mDataBinding.smartRefresh.finishRefresh(true)
                    }
                }
            }

        })
    }

}