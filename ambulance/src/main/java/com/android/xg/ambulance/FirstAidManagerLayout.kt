package com.android.xg.ambulance

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.MapView
import com.top.arch.camera.CameraProxy
import com.top.arch.util.ToastUtils
import com.amap.api.maps2d.model.MyLocationStyle
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.LocationSource
import com.amap.api.maps2d.LocationSource.OnLocationChangedListener
import com.android.xg.bean.Device
import com.android.xg.hcnetsdk.BuildConfig
import com.android.xg.hcnetsdk.HikVisionDeviceHelper
import com.android.xg.hikvision.*
import com.android.xg.hkview.DraggingStatus
import com.android.xg.timeaxis.RecordSegment
import com.elab.libarch.config.ConfigManager
import com.elab.libarch.utils.DimensUtils
import com.google.gson.Gson
import com.top.androidx.superview.SuperTextureView
import com.top.arch.util.ClickUtils
import com.top.arch.util.SPUtils


class FirstAidManagerLayout : ConstraintLayout, TextureView.SurfaceTextureListener,
    LifecycleObserver, LocationSource, AMapLocationListener {

    private var TAG = "FirstAidManagerLayout"
    private var mCameraProxy: CameraProxy? = null
    private var mTextureView: SuperTextureView? = null
    private var mMapView: MapView? = null
    private var mHikVisionVideoView1: HikVisionVideoView? = null
    private var mHikVisionVideoView2: HikVisionVideoView? = null
    private var mGuidelineHorizontal: Guideline? = null
    private var mGuidelineVertical: Guideline? = null
    // private var mDrawView:DrawView?=null


    private var aMap: AMap? = null
    private var mListener: OnLocationChangedListener? = null
    private var mLocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null

    private var mScreenWidth = -1
    private var mScreenHeight: Int = -1
    private var mMinScreen = -1
    private val mItemSpace = 8
    private var mItemHeight = -1
    private var mItemWidth = -1
    private var mExtendWidth = -1
    private var mExtendHeight = -1

    private var mHkParentConstraintLayout: ConstraintLayout? = null
    private var mConstraintSet: ConstraintSet? = null

    private var isExtend = false


    constructor(context: Context) : super(context) {
        initView()
        initScreenInfo()
        initMode()
        initListener()
    }


    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initView()
        initScreenInfo()
        initMode()
        initListener()

    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        initView()
        initScreenInfo()
        initMode()
        initListener()
    }


    //1080
    private fun initScreenInfo() {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val widthPixels = displayMetrics.widthPixels //手机宽度像素
        val heightPixels = displayMetrics.heightPixels
        showLogToast("widthPixels：$widthPixels  heightPixels：$heightPixels")
        mScreenHeight = heightPixels
        mScreenWidth = widthPixels
        mMinScreen = if (widthPixels > heightPixels) {
            heightPixels
        } else {
            widthPixels
        }

        mMinScreen -= DimensUtils.dip2px(25f)
        mExtendHeight = mMinScreen

        //mMinScreen -= 6 * mItemSpace
        showLogToast("mMinScreen: $mMinScreen")

        mItemHeight = (mMinScreen / 2.0).toInt()
        mItemWidth = (mItemHeight / 3.0 * 4).toInt()

        mExtendWidth = (mExtendHeight / 3.0 * 4).toInt()

    }

    private fun initView() {
        val root =
            LayoutInflater.from(context).inflate(R.layout.layout_first_aid_manager, this, true)
        mTextureView = root.findViewById(R.id.texture_view)
        mMapView = root.findViewById(R.id.map_view)
        mHkParentConstraintLayout = rootView.findViewById(R.id.hkview_constraint_parent)
        mHikVisionVideoView1 = root.findViewById(R.id.hik_vision_video_1)
        mHikVisionVideoView2 = root.findViewById(R.id.hik_vision_video_2)
        mGuidelineHorizontal = root.findViewById(R.id.guideline_horizontal)
        mGuidelineVertical = root.findViewById(R.id.guideline_vertical)
        //mDrawView = root.findViewById(R.id.draw_view)

        mConstraintSet = ConstraintSet()

        initCamera()

        initHikVision()
    }

    private fun initHikVision() {
        ConfigManager.getInstance().isDeveloperMode = true
        postDelayed({
            openDevice0()
            openDevice1()
        }, 300)
    }

    private fun openDevice0() {
        val device0String = SPUtils.getInstance().getString("Device0")
        if (device0String.isNotBlank()) {
            val device0 =
                Gson().fromJson(device0String, com.android.xg.ambulance.Device::class.java)
            if (device0 != null) {
                mHikVisionVideoView1?.type = HikVisionViewType.IS_REAL
                mHikVisionVideoView1?.doPreview(
                    device0.ip,
                    device0.port,
                    device0.account,
                    device0.password,
                    device0.chanel
                )
            }
        } else {
            Toast.makeText(context, "信号一未配置", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDevice1() {
        val device1String = SPUtils.getInstance().getString("Device1")

        if (device1String.isNotBlank()) {
            val device1 =
                Gson().fromJson(device1String, com.android.xg.ambulance.Device::class.java)
            if (device1 != null) {
                mHikVisionVideoView2?.type = HikVisionViewType.IS_REAL
                mHikVisionVideoView2?.doPreview(
                    device1.ip,
                    device1.port,
                    device1.account,
                    device1.password,
                    device1.chanel
                )
            }
        } else {
            Toast.makeText(context, "信号二未配置", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initListener() {
        mHikVisionVideoView1?.setOnHikVisionVideoClickListener(object :
            OnHikVisionVideoClickListener {
            override fun onAddDeviceClickListener() {
                openDevice0()
            }

            override fun onSingleClickListener() {

            }

            override fun onDoubleClickListener() {
                extend(0)
            }

            override fun onLoginLoading(
                type: HikVisionViewType?,
                hikVisionVideoStatus: HikVisionVideoStatus?
            ) {

            }

            override fun onLoginSuccess(type: HikVisionViewType?) {

            }

            override fun onLoginFailure(tag: Any?, type: HikVisionViewType?) {

            }

            override fun onPreviewLoading(
                type: HikVisionViewType?,
                hikVisionVideoStatus: HikVisionVideoStatus?
            ) {

            }

            override fun onPreviewSuccess(
                type: HikVisionViewType?,
                hikVisionVideoStatus: HikVisionVideoStatus?
            ) {

            }

            override fun onPreviewFailure(
                tag: Any?,
                type: HikVisionViewType?,
                hikVisionVideoStatus: HikVisionVideoStatus?
            ) {

            }

            override fun onStopPreViewSuccess(type: HikVisionViewType?) {

            }

            override fun onStopPreViewFailure(type: HikVisionViewType?) {

            }

            override fun onFindFileSuccess(
                type: HikVisionViewType?,
                playBackVideoFile: MutableList<RecordSegment>?
            ) {

            }

            override fun onNoVideoSource(type: HikVisionViewType?) {

            }

            override fun onPlayBackModeChanged(
                type: HikVisionViewType?,
                mode: HikVisionPlayBackMode?
            ) {

            }

            override fun onErrorListener(
                type: HikVisionViewType?,
                device: Device?,
                errorCode: Int
            ) {

            }

            override fun onChangeClarityListener(type: HikVisionStreamType?, status: Int) {

            }

            override fun onGetPictureListener(
                path: String?,
                hikVisionVideoStatus: HikVisionVideoStatus?,
                errorCode: Int
            ) {

            }

            override fun onGetVideoListener(
                hikVisionVideoStatus: HikVisionVideoStatus?,
                path: String?,
                captureTime: Int,
                errorCode: Int
            ) {

            }

            override fun onOpenVoiceErrorListener(
                type: HikVisionViewType?,
                device: Device?,
                errorCode: Int
            ) {

            }

            override fun onDraggingListener(tag: Any?, draggingStatus: DraggingStatus?) {

            }

            override fun onLocationListener(tag: Any?, x: Int, y: Int) {

            }
        })

        mHikVisionVideoView2?.setOnHikVisionVideoClickListener(object :
            OnHikVisionVideoClickListener {
            override fun onAddDeviceClickListener() {
                openDevice1()
            }

            override fun onSingleClickListener() {

            }

            override fun onDoubleClickListener() {
                extend(1)
            }

            override fun onLoginLoading(
                type: HikVisionViewType?,
                hikVisionVideoStatus: HikVisionVideoStatus?
            ) {

            }

            override fun onLoginSuccess(type: HikVisionViewType?) {

            }

            override fun onLoginFailure(tag: Any?, type: HikVisionViewType?) {

            }

            override fun onPreviewLoading(
                type: HikVisionViewType?,
                hikVisionVideoStatus: HikVisionVideoStatus?
            ) {

            }

            override fun onPreviewSuccess(
                type: HikVisionViewType?,
                hikVisionVideoStatus: HikVisionVideoStatus?
            ) {

            }

            override fun onPreviewFailure(
                tag: Any?,
                type: HikVisionViewType?,
                hikVisionVideoStatus: HikVisionVideoStatus?
            ) {

            }

            override fun onStopPreViewSuccess(type: HikVisionViewType?) {

            }

            override fun onStopPreViewFailure(type: HikVisionViewType?) {

            }

            override fun onFindFileSuccess(
                type: HikVisionViewType?,
                playBackVideoFile: MutableList<RecordSegment>?
            ) {

            }

            override fun onNoVideoSource(type: HikVisionViewType?) {

            }

            override fun onPlayBackModeChanged(
                type: HikVisionViewType?,
                mode: HikVisionPlayBackMode?
            ) {

            }

            override fun onErrorListener(
                type: HikVisionViewType?,
                device: Device?,
                errorCode: Int
            ) {

            }

            override fun onChangeClarityListener(type: HikVisionStreamType?, status: Int) {

            }

            override fun onGetPictureListener(
                path: String?,
                hikVisionVideoStatus: HikVisionVideoStatus?,
                errorCode: Int
            ) {

            }

            override fun onGetVideoListener(
                hikVisionVideoStatus: HikVisionVideoStatus?,
                path: String?,
                captureTime: Int,
                errorCode: Int
            ) {

            }

            override fun onOpenVoiceErrorListener(
                type: HikVisionViewType?,
                device: Device?,
                errorCode: Int
            ) {

            }

            override fun onDraggingListener(tag: Any?, draggingStatus: DraggingStatus?) {

            }

            override fun onLocationListener(tag: Any?, x: Int, y: Int) {

            }
        })



        mTextureView?.setOnMultiClickListener { extend(3) }
        mMapView?.setOnClickListener {

        }
    }

    private fun initMode() {
        val id0 = mHikVisionVideoView1?.id!!
        val id1 = mHikVisionVideoView2?.id!!
        val id2 = mMapView?.id!!
        val id3 = mTextureView?.id!!

        val idVertical = mGuidelineVertical?.id!!
        val idHorizontal = mGuidelineHorizontal?.id!!

        //获取当前目标控件的约束集合
        mConstraintSet?.clone(mHkParentConstraintLayout)

        mConstraintSet?.clear(id0)
        mConstraintSet?.clear(id1)
        mConstraintSet?.clear(id2)
        mConstraintSet?.clear(id3)

        //左上角
        mConstraintSet?.constrainWidth(id0, mItemWidth)
        mConstraintSet?.constrainHeight(id0, mItemHeight)
        mConstraintSet?.connect(id0, ConstraintSet.RIGHT, idVertical, ConstraintSet.LEFT)
        mConstraintSet?.connect(id0, ConstraintSet.BOTTOM, idHorizontal, ConstraintSet.TOP)


        //右上角
        mConstraintSet?.constrainWidth(id1, mItemWidth)
        mConstraintSet?.constrainHeight(id1, mItemHeight)
        mConstraintSet?.connect(id1, ConstraintSet.LEFT, idVertical, ConstraintSet.RIGHT)
        mConstraintSet?.connect(id1, ConstraintSet.BOTTOM, idHorizontal, ConstraintSet.TOP)

        //左下角
        mConstraintSet?.constrainWidth(id2, mItemWidth)
        mConstraintSet?.constrainHeight(id2, mItemHeight)
        mConstraintSet?.connect(id2, ConstraintSet.RIGHT, idVertical, ConstraintSet.LEFT)
        mConstraintSet?.connect(id2, ConstraintSet.TOP, idHorizontal, ConstraintSet.BOTTOM)

        //右下角
        mConstraintSet?.constrainWidth(id3, mItemWidth)
        mConstraintSet?.constrainHeight(id3, mItemHeight)
        mConstraintSet?.connect(id3, ConstraintSet.LEFT, idVertical, ConstraintSet.RIGHT)
        mConstraintSet?.connect(id3, ConstraintSet.TOP, idHorizontal, ConstraintSet.BOTTOM)

        mConstraintSet!!.applyTo(mHkParentConstraintLayout)
        isExtend = false
    }

    private fun extend(position: Int) {
        if (isExtend) {
            doubleClickToRestore()
        } else {
            doubleClickToExtend(position)
        }
    }

    private fun doubleClickToExtend(position: Int) {
        val id0 = mHikVisionVideoView1?.id!!
        val id1 = mHikVisionVideoView2?.id!!
        val id2 = mMapView?.id!!
        val id3 = mTextureView?.id!!

        mConstraintSet!!.clear(id0)
        mConstraintSet!!.clear(id1)
        mConstraintSet!!.clear(id2)
        mConstraintSet!!.clear(id3)

        //获取当前目标控件的约束集合
        mConstraintSet?.clone(mHkParentConstraintLayout)

        when (position) {
            0 -> {
                //左上角
                mConstraintSet?.constrainWidth(id0, mExtendWidth)
                mConstraintSet?.constrainHeight(id0, mExtendHeight)
                mConstraintSet?.centerHorizontally(id0, ConstraintSet.PARENT_ID)
                mConstraintSet?.centerVertically(id0, ConstraintSet.PARENT_ID)


                mConstraintSet?.constrainWidth(id1, 0)
                mConstraintSet?.constrainHeight(id1, 0)
                mConstraintSet?.constrainWidth(id2, 0)
                mConstraintSet?.constrainHeight(id2, 0)
                mConstraintSet?.constrainWidth(id3, 0)
                mConstraintSet?.constrainHeight(id3, 0)
            }
            1 -> {
                mConstraintSet?.constrainWidth(id1, mExtendWidth)
                mConstraintSet?.constrainHeight(id1, mExtendHeight)
                mConstraintSet?.centerHorizontally(id1, ConstraintSet.PARENT_ID)
                mConstraintSet?.centerVertically(id1, ConstraintSet.PARENT_ID)

                mConstraintSet?.constrainWidth(id0, 0)
                mConstraintSet?.constrainHeight(id0, 0)
                mConstraintSet?.constrainWidth(id2, 0)
                mConstraintSet?.constrainHeight(id2, 0)
                mConstraintSet?.constrainWidth(id3, 0)
                mConstraintSet?.constrainHeight(id3, 0)
            }
            2 -> {
                mConstraintSet?.constrainWidth(id2, mExtendWidth)
                mConstraintSet?.constrainHeight(id2, mExtendHeight)
                mConstraintSet?.centerHorizontally(id2, ConstraintSet.PARENT_ID)
                mConstraintSet?.centerVertically(id2, ConstraintSet.PARENT_ID)

                mConstraintSet?.constrainWidth(id0, 0)
                mConstraintSet?.constrainHeight(id0, 0)
                mConstraintSet?.constrainWidth(id1, 0)
                mConstraintSet?.constrainHeight(id1, 0)
                mConstraintSet?.constrainWidth(id3, 0)
                mConstraintSet?.constrainHeight(id3, 0)
            }
            3 -> {
                mConstraintSet?.constrainWidth(id3, mExtendWidth)
                mConstraintSet?.constrainHeight(id3, mExtendHeight)
                mConstraintSet?.centerHorizontally(id3, ConstraintSet.PARENT_ID)
                mConstraintSet?.centerVertically(id3, ConstraintSet.PARENT_ID)

                mConstraintSet?.constrainWidth(id1, 0)
                mConstraintSet?.constrainHeight(id1, 0)
                mConstraintSet?.constrainWidth(id2, 0)
                mConstraintSet?.constrainHeight(id2, 0)
                mConstraintSet?.constrainWidth(id0, 0)
                mConstraintSet?.constrainHeight(id0, 0)
            }
        }

        isExtend = true
        mConstraintSet!!.applyTo(mHkParentConstraintLayout)
    }

    private fun doubleClickToRestore() {
        initMode()
    }


    private fun initMap() {

        aMap?.setLocationSource(this)
        aMap?.uiSettings?.isMyLocationButtonEnabled = true
        aMap?.isMyLocationEnabled = true // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。


        val myLocationStyle = MyLocationStyle()

        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW)
        myLocationStyle.interval(2000)
        myLocationStyle.showMyLocation(true)
        aMap?.setMyLocationStyle(myLocationStyle) //设置定位蓝点的Style

    }

    private fun initCamera() {
        mCameraProxy = CameraProxy(context as Activity?)
        // mCameraProxy!!.switchCamera()
        mTextureView?.surfaceTextureListener = this

    }


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate() {
        mMapView?.onCreate(null)
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView?.map
        }
        initMap()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() {
        mMapView?.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        mMapView?.onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        //Toast.makeText(context,"destory",Toast.LENGTH_SHORT).show()
        mMapView?.onDestroy();
        mHikVisionVideoView1?.stopPreview()
        mHikVisionVideoView2?.stopPreview()
        mCameraProxy?.releaseCamera()
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        mCameraProxy!!.openCamera()
        mCameraProxy!!.startPreview(surface)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        mCameraProxy!!.releaseCamera()
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {

    }

    override fun activate(listener: LocationSource.OnLocationChangedListener?) {
        mListener = listener
        if (mLocationClient == null) {
            mLocationClient = AMapLocationClient(context)
            mLocationOption = AMapLocationClientOption()
            //设置定位监听
            mLocationClient?.setLocationListener(this)
            //设置为高精度定位模式
            mLocationOption?.setLocationMode(AMapLocationMode.Hight_Accuracy)
            //设置定位参数
            mLocationClient?.setLocationOption(mLocationOption)
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient?.startLocation()
        }
    }

    override fun deactivate() {
        mListener = null
        if (mLocationClient != null) {
            mLocationClient?.stopLocation()
            mLocationClient?.onDestroy()
        }
        mLocationClient = null
    }

    override fun onLocationChanged(amapLocation: AMapLocation?) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation.errorCode == 0
            ) {
                mListener!!.onLocationChanged(amapLocation) // 显示系统小蓝点
                aMap!!.moveCamera(CameraUpdateFactory.zoomTo(18f))
            } else {
                val errText =
                    "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo()
                Log.e("AmapErr", errText)
            }
        }
    }


    private fun showLogToast(msg: String) {
        //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        Log.i("FirstAidManagerLayout", msg)
    }


    var lastExtendPosition = -1

    fun switchScreen(i: Int) {
        if (i > 3) {
            return
        }
        if (i < 0) {
            return
        }
        if (i == lastExtendPosition) {
            //该次点击和上一次相同
            if (isExtend) {
                doubleClickToRestore()
            } else {
                extend(i)
                lastExtendPosition=i;
            }
        } else {
            //该次点击和上一次不同
            if (isExtend) {
                doubleClickToRestore()
            }
            extend(i)
            lastExtendPosition=i;
        }

    }

}