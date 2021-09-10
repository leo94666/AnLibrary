package com.android.xg.ambulance

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import com.android.xg.ambulance.databinding.FragmentListOfExportBinding
import com.top.arch.base.BaseDialogFragment

class ListOfExportsFragment : BaseDialogFragment<FragmentListOfExportBinding>() {

    val mTitles_2 = arrayOf("全部", "呼吸科", "神经内科", "外科")


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.attributes?.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
    }

    override fun getLayout(): Int {
        return R.layout.fragment_list_of_export
    }

    override fun init(view: View?) {
        mDataBinding.tabType.setTabData(mTitles_2)
        mDataBinding.tvCancel.setOnClickListener { dismiss() }
        mDataBinding.tvSure.setOnClickListener {
            dismiss()
            FirstAidActivity.startFirstAidActivity(requireContext())
        }

        mDataBinding.rvList.layoutManager = GridLayoutManager(activity, 3)
        mDataBinding.rvList.adapter = ExportsAdapter(
            requireContext(), mutableListOf(
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
                    true,
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

}