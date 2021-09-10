package com.android.xg.ambulance

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import com.android.xg.ambulance.databinding.FragmentListOfExportBinding
import com.top.arch.base.BaseActivity
import com.top.arch.base.BaseDialogFragment
import com.top.arch.utils.BaseUtils

class ListOfExportsActivity : BaseActivity<FragmentListOfExportBinding>() {

    val mTitles_2 = arrayOf("全部", "呼吸科", "神经内科", "外科")




    override fun getLayout(): Int {
        setFullScreen()
        return R.layout.fragment_list_of_export
    }

    override fun init(view: View?) {
        mDataBinding.tabType.setTabData(mTitles_2)
        mDataBinding.tvCancel.setOnClickListener { finish() }
        mDataBinding.tvSure.setOnClickListener {
            FirstAidActivity.startFirstAidActivity(this)
        }

        mDataBinding.rvList.layoutManager = GridLayoutManager(this, 3)
        mDataBinding.rvList.adapter = ExportsAdapter(
            this, mutableListOf(
                Exports(
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
            )
        )
    }

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