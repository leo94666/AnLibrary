package com.android.xg.ambulance

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.android.xg.ambulance.databinding.FragmentListOfExportBinding
import com.android.xg.ambulance.http.DoctorResultBean
import com.android.xg.ambulance.login.AmbulanceViewModel

import com.top.arch.base.BaseActivity
import com.top.arch.utils.BaseUtils
import javax.inject.Inject

class ListOfExportsActivity : BaseActivity<FragmentListOfExportBinding>() {

    private var TAG = "ListOfExportsActivity"


    @Inject
    var ambulanceViewModel: AmbulanceViewModel? = null

    val mTitles = arrayOf("全部", "呼吸科", "神经内科", "外科")

    var exportsAdapter: ExportsAdapter? = null

    var mSelectedExport = mutableListOf<Exports>()


    override fun getLayout(): Int {
        setFullScreen()
        hideBottomUIMenu()
        return R.layout.fragment_list_of_export
    }

    override fun init(view: View?) {
        ambulanceViewModel = ViewModelProvider(this).get(AmbulanceViewModel::class.java)

        mDataBinding.tabType.visibility = View.GONE
        mDataBinding.tabType.setTabData(mTitles)
        mDataBinding.tvCancel.setOnClickListener { finish() }
        mDataBinding.tvSure.setOnClickListener {

            //Toast.makeText(this@ListOfExportsActivity,""+mSelectedExport.size,Toast.LENGTH_SHORT).show()
            if (mSelectedExport.size == 0) {
                Toast.makeText(this@ListOfExportsActivity, "请先选择专家!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            FirstAidActivity.startFirstAidActivity(this, mSelectedExport as ArrayList<Exports>)
        }

        mDataBinding.rvList.layoutManager = GridLayoutManager(this, 3)
        exportsAdapter = ExportsAdapter(this)
        mDataBinding.rvList.adapter = exportsAdapter

        mSelectedExport.clear()
        exportsAdapter?.setOnSelectedListener(object : ExportsAdapter.OnSelectedListener {

            override fun onSelectedItem(position: Exports, isChecked: Boolean): Boolean {
                if (isChecked) {
                    mSelectedExport.add(position)
                } else {
                    mSelectedExport.remove(position)
                }
                return false
            }
        })

        ambulanceViewModel?.docker()
        ambulanceViewModel?.eventHub?.observe(this, {
            if (it.action == LiveDataEvent.DOCTORS) {
                val doctors = it.`object` as ArrayList<DoctorResultBean.Doctor>
                val exports = buildExports(doctors)
                exportsAdapter?.addData(exports)
            }
        })
    }

    private fun buildExports(doctors: ArrayList<DoctorResultBean.Doctor>): MutableList<Exports> {
        var exports = mutableListOf<Exports>()
        doctors.forEach {
            exports.add(Exports(it.userName!!, it.info?.nickName!!, it.info?.avatar!!, false, 0))
        }
        return exports
    }

    /**
     * Exports(
    "李医生",
    "",
    true,
    50
    ),
    Exports(
    "鲁医生",
    "",
    true,
    20
    ),
    Exports(
    "徐医生",
    "",
    false,
    30
    ),
    Exports(
    "胡医生",
    "",
    true,
    80
    ),
    Exports(
    "管医生",
    "",
    true,
    10
    ),
    Exports(
    "郝医生",
    "",
    true,
    75
    )
     */


    companion object {
        /**
         * @param context
         */
        fun startListOfExportsActivity(context: Context) {
            if (BaseUtils.isFastDoubleClick()) return
            val intent = Intent(context, ListOfExportsActivity::class.java)
            context.startActivity(intent)
        }
    }

}