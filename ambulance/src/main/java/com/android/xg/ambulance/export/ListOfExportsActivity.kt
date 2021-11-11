package com.android.xg.ambulance.export

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.android.xg.ambulance.R
import com.android.xg.ambulance.databinding.FragmentListOfExportBinding
import com.android.xg.ambulance.meet.FirstAidActivity
import com.android.xg.ambulancelib.AmbulanceViewModel
import com.android.xg.ambulancelib.LiveDataEvent
import com.android.xg.ambulancelib.bean.DoctorResultBean
import com.android.xg.ambulancelib.bean.SectionsDoctorsResultBean
import com.android.xg.ambulancelib.personal.AmbulanceProfileManager
import com.top.androidx.tabs.listener.OnTabSelectListener
import com.top.arch.base.BaseActivity
import com.top.arch.utils.BaseUtils
import javax.inject.Inject

class ListOfExportsActivity : BaseActivity<FragmentListOfExportBinding>() {

    private var TAG = "ListOfExportsActivity"


    @Inject
    var ambulanceViewModel: AmbulanceViewModel? = null

    private val mTitles = mutableListOf("全部")
    private val mALLDoctors = mutableListOf<DoctorResultBean.Doctor>()
    private val mSections = mutableListOf<SectionsDoctorsResultBean.SectionsDoctors>()

    var exportsAdapter: ExportsAdapter? = null

    var mSelectedExport = mutableListOf<Exports>()


    override fun getLayout(): Int {
        setFullScreen()
        hideBottomUIMenu()
        keepScreenOn()

        return R.layout.fragment_list_of_export
    }

    override fun init(view: View?) {
        ambulanceViewModel = ViewModelProvider(this).get(AmbulanceViewModel::class.java)

        //mDataBinding.tabType.visibility = View.GONE

        mDataBinding.tvCancel.setOnClickListener { finish() }
        mDataBinding.tvSure.setOnClickListener {
            if (mSelectedExport.size == 0) {
                Toast.makeText(this@ListOfExportsActivity, "请先选择专家!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            FirstAidActivity.startFirstAidActivity(this, mSelectedExport as ArrayList<Exports>)
        }

        ambulanceViewModel?.sectionsDoctors(AmbulanceProfileManager.getInstance().hospital.hospitalId,
            object : AmbulanceViewModel.ActionResult {
                override fun onError() {

                }

                override fun onSuccess(any: Any?) {
                    if (any is ArrayList<*>) {
                        val buildTitle =
                            buildTitle(any as ArrayList<SectionsDoctorsResultBean.SectionsDoctors>)
                        mTitles.addAll(buildTitle)
                        mDataBinding.tabType.setTabData(mTitles.toTypedArray())
                    }
                }
            })

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

        mDataBinding.tabType.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                //Toast.makeText(this@ListOfExportsActivity, "onTabSelect: $position", Toast.LENGTH_SHORT).show()
                if (position == 0) {
                    exportsAdapter?.clear()
                    exportsAdapter?.addData(buildExports(mALLDoctors as ArrayList<DoctorResultBean.Doctor>))
                } else {
                    exportsAdapter?.clear()
                    exportsAdapter?.addData(buildExports(mSections[position - 1].doctors))
                }
            }

            override fun onTabReselect(position: Int) {
                //Toast.makeText(this@ListOfExportsActivity, "onTabReselect: $position", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun buildTitle(data: ArrayList<SectionsDoctorsResultBean.SectionsDoctors>): ArrayList<String> {
        var mData = arrayListOf<String>()
        mALLDoctors.clear()
        mSections.clear()
        data.forEach {
            mData.add(it.name)
            mALLDoctors.addAll(it.doctors)
            mSections.add(it)
        }
        return mData
    }

    private fun buildExports(doctors: ArrayList<DoctorResultBean.Doctor>): MutableList<Exports> {
        var exports = mutableListOf<Exports>()
        doctors.forEach {
            exports.add(
                Exports(
                    it.userName!!,
                    it.userId!!,
                    it.info?.nickName!!,
                    it.info?.avatar!!,
                    false,
                    0
                )
            )
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