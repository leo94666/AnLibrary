package com.android.xg.ambulance.export.main

import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.android.xg.ambulance.export.App
import com.android.xg.ambulance.export.R
import com.android.xg.ambulance.export.databinding.FragmentMeBinding
import com.android.xg.ambulance.export.login.LoginActivity
import com.android.xg.ambulancelib.AmbulanceViewModel
import com.android.xg.ambulancelib.personal.AmbulanceProfileManager
import com.top.arch.app.AppManager
import com.top.arch.base.BaseXFragment
import javax.inject.Inject

class MeFragment:BaseXFragment<FragmentMeBinding>() {

    @Inject
    var loginViewModel: AmbulanceViewModel? = null
    override fun getLayout(): Int {
        return R.layout.fragment_me
    }

    override fun init(view: View?) {
        loginViewModel = ViewModelProvider(this).get(AmbulanceViewModel::class.java)

        mDataBinding.stvPersonalPhone.setRightString(AmbulanceProfileManager.getInstance().userId)
//        AmbulanceProfileManager.getInstance().userInfo.avatar;
//        Glide.with(requireContext())
//            .load("")
//            .into(mDataBinding.civAvatar)

//        loginViewModel?.hospital(object:AmbulanceViewModel.ActionResult{
//            override fun onError() {
//
//            }
//
//            override fun onSuccess(any: Any?) {
//                AmbulanceProfileManager.getInstance()
//                Glide.with(requireContext()).load(AmbulanceProfileManager.getInstance().userInfo.avatar).into(mDataBinding.civAvatar)
//                mDataBinding.stvPersonalHospital.setRightString(AmbulanceProfileManager.getInstance().userInfo.hospital?.name)
//                mDataBinding.stvPersonalNickname.setRightString(AmbulanceProfileManager.getInstance().userInfo.name)
//
//            }
//
//        })

        mDataBinding.logout.setOnClickListener {
            App.getApplication()?.stopRoomService()
            loginViewModel?.removeId(object :AmbulanceViewModel.ActionResult{
                override fun onError() {

                }

                override fun onSuccess(any: Any?) {
                    AmbulanceProfileManager.getInstance().logout()
                    AppManager.getInstance().finishAllActivity()
                    LoginActivity.startLoginActivity(requireContext())
                }

            })
        }
    }
}