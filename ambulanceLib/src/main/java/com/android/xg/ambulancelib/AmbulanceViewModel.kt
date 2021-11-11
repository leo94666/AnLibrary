package com.android.xg.ambulancelib

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.android.xg.ambulancelib.bean.*
import com.android.xg.ambulancelib.client.AmbulanceRestClient
import com.android.xg.ambulancelib.personal.AmbulanceProfileManager
import com.elab.libarch.http.MyHeadMapUtil
import com.elab.libarch.utils.MD5Util
import com.google.gson.Gson
import com.top.arch.base.BaseViewModel
import com.top.arch.util.DeviceUtils.getUniqueDeviceId
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class AmbulanceViewModel @Inject constructor(application: Application) :
    BaseViewModel<LiveDataEvent>(application) {
    private val context: Context


    companion object {
        private const val TAG = "LoginViewModel"
    }

    init {
        context = application
    }

    enum class ROLE(i: Int) {
        INTERNATIONAL(0), EXPORTS(1), BROADCAST(2), CUSTOMER_SERVICE(3)
    }

    fun login(carNumber: String, account: String, password: String, type: Boolean, role: ROLE) {
        val mDataMap: MutableMap<String, Any> = HashMap()
        mDataMap["elabUser"] = role.ordinal  //0 导管室 ,1 专家端	2 转播 3 自动客服
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
            .subscribe(object : io.reactivex.Observer<LoginResultBean?> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(loginResultBean: LoginResultBean) {
                    val statusCode: String? = loginResultBean.statusCode
                    val statusMessage: String? = loginResultBean.statusMessage
                    if (statusCode.equals(LiveDataEvent.SUCCESS)) {
                        Toast.makeText(context, "登录成功!", Toast.LENGTH_SHORT).show()
                        AmbulanceProfileManager.getInstance()
                            .login(account, loginResultBean.data)
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

    fun register(userPhone: String, pwd: String, smsCode: String) {
        val mDataMap: MutableMap<String, Any> = HashMap()
        mDataMap["userPhone"] = userPhone
        mDataMap["pwd"] = MD5Util.getMD5String(Objects.requireNonNull(pwd))
        mDataMap["smsCode"] = smsCode

        AmbulanceRestClient.getApiUrl().register(mDataMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : io.reactivex.Observer<ResultBean> {
                override fun onSubscribe(d: Disposable) {


                }

                override fun onNext(t: ResultBean) {
                    val statusCode: String? = t.statusCode
                    val statusMessage: String? = t.statusMessage
                    if (statusCode == LiveDataEvent.SUCCESS) {
                        Toast.makeText(context, statusMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, statusMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "server error!", Toast.LENGTH_SHORT).show();

                }

                override fun onComplete() {

                }

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
            .subscribe(object : io.reactivex.Observer<CaptchaResultBean?> {
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
            .subscribe(object : io.reactivex.Observer<RankResultBean> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: RankResultBean) {
                    if (t.statusCode == LiveDataEvent.SUCCESS) {
                        AmbulanceProfileManager.getInstance().rank = t.data

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
            .subscribe(object : io.reactivex.Observer<DoctorResultBean> {
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


    fun hospital(actionResult: ActionResult?) {
        val mDataMap: MutableMap<String, String> = HashMap()
        mDataMap["Authorization"] =
            "Bearer " + AmbulanceProfileManager.getInstance().secretBean.accessToken
        AmbulanceRestClient.getApiUrl().getUserInfo(mDataMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : io.reactivex.Observer<UserResultBean> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: UserResultBean) {
                    if (t.statusCode == LiveDataEvent.SUCCESS) {
                        AmbulanceProfileManager.getInstance().hospital = t.data?.hospital
                        AmbulanceProfileManager.getInstance().carNumber = t.data?.licenseNumber
                        AmbulanceProfileManager.getInstance().userInfo = t.data
                        actionResult?.onSuccess(null)
                    } else {
                        Toast.makeText(context, t.statusMessage, Toast.LENGTH_SHORT).show()
                        actionResult?.onError()
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "server error!", Toast.LENGTH_SHORT).show()
                    actionResult?.onError()
                }

                override fun onComplete() {

                }
            })
    }


    ////////////////////////////


    interface ActionResult {
        fun onError()
        fun onSuccess(any: Any?)
    }

    fun createMeet(actionResult: ActionResult) {
        val mHeaderDataMap: MutableMap<String, String> = HashMap()
        mHeaderDataMap["Authorization"] =
            "Bearer " + AmbulanceProfileManager.getInstance().secretBean.accessToken

        val mBodyDataMap: MutableMap<String, Any> = HashMap()
        val currentTimeMillis = System.currentTimeMillis()
        mBodyDataMap["title"] = "救护车项目会议$currentTimeMillis"
        mBodyDataMap["password"] = "bjxg"
        mBodyDataMap["source"] = "android"
        mBodyDataMap["startTime"] = currentTimeMillis
        mBodyDataMap["endTime"] = currentTimeMillis


        AmbulanceRestClient.getApiUrl().createMeeting(mHeaderDataMap, mBodyDataMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : io.reactivex.Observer<CreateMeetingResultBean> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: CreateMeetingResultBean) {
                    if (t.statusCode == LiveDataEvent.SUCCESS) {
                        //AmbulanceProfileManager.getInstance().setDoc(t.data)
                        actionResult.onSuccess(t.data)
                    } else {
                        Toast.makeText(context, t.statusMessage, Toast.LENGTH_SHORT).show()
                        actionResult.onError()
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "server error!", Toast.LENGTH_SHORT).show()
                    actionResult.onError()
                }

                override fun onComplete() {

                }

            })
    }

    fun endMeet(meeting: CreateMeetingResultBean.CreateMeetingBean, actionResult: ActionResult) {
        val mHeaderDataMap: MutableMap<String, String> = HashMap()
        mHeaderDataMap["Authorization"] =
            "Bearer " + AmbulanceProfileManager.getInstance().secretBean.accessToken

        val mBodyDataMap: MutableMap<String, Any> = HashMap()
        //val currentTimeMillis = System.currentTimeMillis()
        mBodyDataMap["meetingId"] = meeting.meetingId!!
        mBodyDataMap["title"] = meeting.title!!
        mBodyDataMap["password"] = meeting.password!!
        mBodyDataMap["status"] = 2  //status(会议状态)：0未开始，1进行中，2已结束
        mBodyDataMap["startTime"] = meeting.startTime!!
        mBodyDataMap["endTime"] = meeting.endTime!!


        AmbulanceRestClient.getApiUrl().modifyMeeting(mHeaderDataMap, mBodyDataMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : io.reactivex.Observer<CreateMeetingResultBean> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: CreateMeetingResultBean) {
                    if (t.statusCode == LiveDataEvent.SUCCESS) {
                        //AmbulanceProfileManager.getInstance().setDoc(t.data)
                        actionResult.onSuccess(null)
                        //Toast.makeText(context, "88888888888888888", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, t.statusMessage, Toast.LENGTH_SHORT).show()
                        actionResult.onError()
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "server error!", Toast.LENGTH_SHORT).show();
                    actionResult.onError()

                }

                override fun onComplete() {

                }

            })
    }


    fun aliveMeet(actionResult: ActionResult) {
        val mHeaderDataMap: MutableMap<String, String> = HashMap()
        mHeaderDataMap["Authorization"] =
            "Bearer " + AmbulanceProfileManager.getInstance().secretBean.accessToken

        AmbulanceRestClient.getApiUrl().aliveMeeting(mHeaderDataMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : io.reactivex.Observer<MeetHistoryResultBean> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: MeetHistoryResultBean) {
                    if (t.statusCode == LiveDataEvent.SUCCESS) {
                        //AmbulanceProfileManager.getInstance().setDoc(t.data)
                        actionResult.onSuccess(t.data)
                    } else {
                        Toast.makeText(context, t.statusMessage, Toast.LENGTH_SHORT).show()
                        actionResult.onError()
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "server error!", Toast.LENGTH_SHORT).show();
                    actionResult.onError()
                }

                override fun onComplete() {

                }

            })

    }

    fun historyMeet(actionResult: ActionResult) {
        val mHeaderDataMap: MutableMap<String, String> = HashMap()
        mHeaderDataMap["Authorization"] =
            "Bearer " + AmbulanceProfileManager.getInstance().secretBean.accessToken

        AmbulanceRestClient.getApiUrl().historyMeeting(mHeaderDataMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : io.reactivex.Observer<MeetHistoryResultBean> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: MeetHistoryResultBean) {
                    if (t.statusCode == LiveDataEvent.SUCCESS) {
                        //AmbulanceProfileManager.getInstance().setDoc(t.data)
                        actionResult.onSuccess(t.data)
                    } else {
                        Toast.makeText(context, t.statusMessage, Toast.LENGTH_SHORT).show()
                        actionResult.onError()
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "server error!", Toast.LENGTH_SHORT).show();
                    actionResult.onError()

                }

                override fun onComplete() {

                }

            })
    }


    fun invite(meetingId: String?, userIds: Array<String>, actionResult: ActionResult?) {
        val mHeaderDataMap: MutableMap<String, String> = HashMap()
        mHeaderDataMap["Authorization"] =
            "Bearer " + AmbulanceProfileManager.getInstance().secretBean.accessToken

        val mBodyDataMap: MutableMap<String, Any> = HashMap()
        mBodyDataMap["meetingId"] = meetingId!!
        val toJson = Gson().toJson(userIds)
        mBodyDataMap["userIds"] = userIds



        try {
            AmbulanceRestClient.getApiUrl().invite(mHeaderDataMap, mBodyDataMap)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : io.reactivex.Observer<ResultBean> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: ResultBean) {
                        if (t.statusCode == LiveDataEvent.SUCCESS) {
                            //AmbulanceProfileManager.getInstance().setDoc(t.data)
                            actionResult?.onSuccess(null)
                        } else {
                            Toast.makeText(context, t.statusMessage, Toast.LENGTH_SHORT).show()
                            actionResult?.onError()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context, "server error!", Toast.LENGTH_SHORT).show();
                        actionResult?.onError()

                    }

                    override fun onComplete() {

                    }

                })
        } catch (e: Exception) {
            Toast.makeText(context, "server error!", Toast.LENGTH_SHORT).show();

        }

    }

    fun meetInfo(meetingId: String?, actionResult: ActionResult) {
        val mHeaderDataMap: MutableMap<String, String> = HashMap()
        mHeaderDataMap["Authorization"] =
            "Bearer " + AmbulanceProfileManager.getInstance().secretBean.accessToken

        AmbulanceRestClient.getApiUrl().meetInfo(mHeaderDataMap, meetingId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : io.reactivex.Observer<MeetInfoResultBean> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: MeetInfoResultBean) {
                    if (t.statusCode == LiveDataEvent.SUCCESS) {
                        //AmbulanceProfileManager.getInstance().setDoc(t.data)
                        actionResult.onSuccess(t.data)
                    } else {
                        Toast.makeText(context, t.statusMessage, Toast.LENGTH_SHORT).show()
                        actionResult.onError()
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "server error!", Toast.LENGTH_SHORT).show();
                    actionResult.onError()
                }

                override fun onComplete() {

                }
            })
    }


    fun removeId(actionResult: ActionResult?) {
        val mHeaderDataMap: MutableMap<String, String> = HashMap()
        mHeaderDataMap["Authorization"] =
            "Bearer " + AmbulanceProfileManager.getInstance().secretBean.accessToken

        AmbulanceRestClient.getApiUrl().removeID(mHeaderDataMap, getUniqueDeviceId())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : io.reactivex.Observer<ResultBean> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: ResultBean) {
                    actionResult?.onSuccess(null)
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "server error!", Toast.LENGTH_SHORT).show();
                    actionResult?.onError()
                }

                override fun onComplete() {

                }
            })
    }


    fun sectionsDoctors(hospitalId: Int, actionResult: ActionResult?) {
        val mHeaderDataMap: MutableMap<String, String> = HashMap()
        mHeaderDataMap["Authorization"] =
            "Bearer " + AmbulanceProfileManager.getInstance().secretBean.accessToken

        AmbulanceRestClient.getApiUrl().sectionsDoctors(mHeaderDataMap, hospitalId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : io.reactivex.Observer<SectionsDoctorsResultBean> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: SectionsDoctorsResultBean) {
                    if (t.statusCode == LiveDataEvent.SUCCESS) {
                        actionResult?.onSuccess(t.data)
                    } else {
                        Toast.makeText(context, t.statusMessage, Toast.LENGTH_SHORT).show();
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