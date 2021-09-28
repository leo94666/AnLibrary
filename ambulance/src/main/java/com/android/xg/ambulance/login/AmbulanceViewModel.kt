package com.android.xg.ambulance.login

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.android.xg.ambulance.LiveDataEvent
import com.android.xg.ambulance.MainActivity
import com.android.xg.ambulance.http.*
import com.android.xg.ambulance.personal.AmbulanceProfileManager
import com.elab.libarch.http.MyHeadMapUtil
import com.elab.libarch.utils.MD5Util
import com.elab.libarch.utils.ToastUtils
import com.top.arch.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class AmbulanceViewModel @Inject constructor(application: Application) :
    BaseViewModel<LiveDataEvent>(application) {
    private val context: Context


    companion object {
        private const val TAG = "LoginViewModel"
    }

    init {
        context = application
    }

    fun login(carNumber: String, account: String, password: String, type: Boolean) {
        val mDataMap: MutableMap<String, Any> = HashMap()
        mDataMap["elabUser"] = 0  //导管室
        mDataMap["platform"] = "android"
        mDataMap["userName"] = account
        mDataMap["version"] = "1"

        val observable: Observable<*>

        if (type) {
            mDataMap["pwd"] = MD5Util.getMD5String(Objects.requireNonNull(password))
            observable = AmbulanceRestClient.getApiUrl()
                .loginByAccount(
                    MyHeadMapUtil.getInstance()
                        .DataToHeader(mDataMap) as MutableMap<String, String>?, mDataMap
                )
        } else {
            mDataMap["smsCode"] = password
            observable = AmbulanceRestClient.getApiUrl()
                .loginBySMS(
                    MyHeadMapUtil.getInstance()
                        .DataToHeader(mDataMap) as MutableMap<String, String>?, mDataMap
                )
        }

        observable.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<LoginResultBean?> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(loginResultBean: LoginResultBean) {
                    val statusCode: String? = loginResultBean.statusCode
                    val statusMessage: String? = loginResultBean.statusMessage
                    if (statusCode.equals(LiveDataEvent.SUCCESS)) {
                        Toast.makeText(context, "登录成功!", Toast.LENGTH_SHORT).show()
                        AmbulanceProfileManager.getInstance()
                            .login(carNumber, account, loginResultBean.data)
                        mEventHub.value = LiveDataEvent(LiveDataEvent.LOGIN_SUCCESS, null)
                    } else {
                        Toast.makeText(context, statusMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "server error!", Toast.LENGTH_SHORT).show()
                }

                override fun onComplete() {}
            })

    }

    enum class Capctcha {
        REGISTER, LOGIN, FORGET_PASSWORD
    }

    fun getCaptcha(userPhone: String?, smsCode: Int) {
        val datamap: MutableMap<String?, Any?> = HashMap()
        datamap["smsType"] = smsCode
        datamap["userPhone"] = userPhone
        AmbulanceRestClient.getApiUrl()
            .getCaptchaCode(
                MyHeadMapUtil.getInstance().DataToHeader(datamap) as MutableMap<String, String>?,
                datamap
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<CaptchaResultBean?> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(captchaResultBean: CaptchaResultBean) {
                    val statusCode: String? = captchaResultBean.statusCode
                    val statusMessage: String? = captchaResultBean.statusMessage
                    if (statusCode == LiveDataEvent.SUCCESS) {
                        Toast.makeText(context, statusMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, statusMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "server error!", Toast.LENGTH_SHORT).show();
                    //ToastUtils.showToast(context, "获取验证码失败! server error")
                }

                override fun onComplete() {}
            })
    }

    fun rank() {
        val mDataMap: MutableMap<String, String> = HashMap()
        mDataMap["Authorization"] =
            "Bearer " + AmbulanceProfileManager.getInstance().secretBean.accessToken

        AmbulanceRestClient.getApiUrl().rank(mDataMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<RankResultBean> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: RankResultBean) {
                    if (t.statusCode == LiveDataEvent.SUCCESS) {
                        AmbulanceProfileManager.getInstance().setRank(t.data)

                    } else {
                        Toast.makeText(context, t.statusMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "server error!", Toast.LENGTH_SHORT).show();
                }

                override fun onComplete() {

                }
            })
    }


    fun docker() {
        val mDataMap: MutableMap<String, String> = HashMap()
        mDataMap["Authorization"] =
            "Bearer " + AmbulanceProfileManager.getInstance().secretBean.accessToken
        AmbulanceRestClient.getApiUrl().doctor(mDataMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<DoctorResultBean> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: DoctorResultBean) {
                    if (t.statusCode == LiveDataEvent.SUCCESS) {
                        //AmbulanceProfileManager.getInstance().setDoc(t.data)
                        mEventHub.value = LiveDataEvent(LiveDataEvent.DOCTORS, t.data)

                    } else {
                        Toast.makeText(context, t.statusMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "server error!", Toast.LENGTH_SHORT).show();

                }

                override fun onComplete() {

                }
            })
    }
}