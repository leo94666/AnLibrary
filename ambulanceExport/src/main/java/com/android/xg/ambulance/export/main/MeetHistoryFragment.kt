package com.android.xg.ambulance.export.main


import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.xg.ambulance.export.R
import com.android.xg.ambulance.export.databinding.FragmentMeetHistoryBinding
import com.android.xg.ambulancelib.AmbulanceViewModel
import com.android.xg.ambulancelib.bean.MeetHistoryResultBean
import com.top.arch.base.BaseXFragment
import javax.inject.Inject

class MeetHistoryFragment : BaseXFragment<FragmentMeetHistoryBinding>() {

    lateinit var mMeetAdapter: MeetAdapter

    @Inject
    var loginViewModel: AmbulanceViewModel? = null
    override fun init(root: View?) {
        loginViewModel = ViewModelProvider(this).get(AmbulanceViewModel::class.java)

        mMeetAdapter = MeetAdapter(requireContext())
        mDataBinding.rvMain.layoutManager = LinearLayoutManager(requireContext())
        mDataBinding.rvMain.adapter = mMeetAdapter


        loginViewModel?.historyMeet(object:AmbulanceViewModel.ActionResult{
            override fun onError() {

            }

            override fun onSuccess(any: Any?) {
                if (any is ArrayList<*>) {
                    if (any.size == 0) {
                        Toast.makeText(context, "数据为空！", Toast.LENGTH_SHORT).show()
                        mDataBinding.tvEmpty.visibility = View.VISIBLE
                        mDataBinding.rvMain.visibility = View.GONE
                    } else {
                        mDataBinding.rvMain.visibility = View.VISIBLE
                        mDataBinding.tvEmpty.visibility = View.GONE
                        mMeetAdapter.addData(any as MutableList<MeetHistoryResultBean.MeetHistoryBean>)
                    }
                }
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.fragment_meet_history
    }

}