package com.android.xg.ambulance.meet

import android.app.Activity
import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
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
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.LocationSource
import com.amap.api.maps2d.LocationSource.OnLocationChangedListener
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.MyLocationStyle
import com.android.xg.ambulance.R
import com.android.xg.ambulancelib.personal.AmbulanceProfileManager
import com.android.xg.bean.Device
import com.android.xg.hikvision.*
import com.android.xg.hkview.DraggingStatus
import com.android.xg.timeaxis.RecordSegment
import com.google.gson.Gson
import com.top.androidx.superview.SuperTextureView
import com.top.arch.camera.CameraProxy
import com.top.arch.util.SPUtils
import com.top.arch.util.ScreenUtils


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

    private var lastExtendPosition = -1


    private var mHkParentConstraintLayout: ConstraintLayout? = null
    private var mConstraintSet: ConstraintSet? = null

    //适用于四分频
    private var isExtend = false

    //适用于2分屏
    private var mDefaultISDevice = true

    private var mDefaultMode = MODE.TWO


    private lateinit var onSelectPositionListener: ((Int) -> Unit)

    fun setOnSelectPositionListener(listen: ((Int) -> Unit)) {
        this.onSelectPositionListener = listen
    }

    constructor(context: Context) : super(context) {
        initView()
        initListener()
    }


    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initView()
        initListener()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        initView()
        initListener()
    }


    //1080
    private fun initScreenInfo() {
        val widthPixels = ScreenUtils.getScreenWidth() //手机宽度像素
        val heightPixels =ScreenUtils.getScreenHeight()

        showLogToast("widthPixels：$widthPixels  heightPixels：$heightPixels")
        mScreenHeight = heightPixels
        mScreenWidth = widthPixels
        mMinScreen = if (widthPixels > heightPixels) {
            heightPixels
        } else {
            widthPixels
        }


        //mMinScreen -= (DimensUtils.dip2px(35f)+ScreenUtils.getStatusBarHeight())
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
        initScreenInfo()

        mTextureView = root.findViewById(R.id.texture_view)
        mMapView = root.findViewById(R.id.map_view)
        mHkParentConstraintLayout = rootView.findViewById(R.id.hkview_constraint_parent)
        mHikVisionVideoView1 = root.findViewById(R.id.hik_vision_video_1)
        mHikVisionVideoView2 = root.findViewById(R.id.hik_vision_video_2)
        mGuidelineHorizontal = root.findViewById(R.id.guideline_horizontal)
        mGuidelineVertical = root.findViewById(R.id.guideline_vertical)
        //mDrawView = root.findViewById(R.id.draw_view)
        mConstraintSet = ConstraintSet()
        isChildrenDrawingOrderEnabled = true

        if(AmbulanceProfileManager.getInstance().screenNum==4){
            mDefaultMode= MODE.FOR
        }else{
            mDefaultMode= MODE.TWO
        }
        initMode(mDefaultMode)

    }

    private fun initHikVision() {
        //ConfigManager.getInstance().isDeveloperMode = true
        postDelayed({
            openDevice0()
            openDevice1()
        }, 500)
    }

    private fun openDevice0() {
        val device0String = SPUtils.getInstance().getString("Device0")
        if (device0String.isNotBlank()) {
            val device0 =
                Gson().fromJson(device0String, com.android.xg.ambulancelib.bean.Device::class.java)
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
        if (mDefaultMode == MODE.TWO) return
        val device1String = SPUtils.getInstance().getString("Device1")

        if (device1String.isNotBlank()) {
            val device1 =
                Gson().fromJson(device1String, com.android.xg.ambulancelib.bean.Device::class.java)
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
                if (mDefaultMode == MODE.TWO) {
                    if (mDefaultISDevice) {

                    } else {
                        initTwoMode(!mDefaultISDevice)
                    }
                } else {
                    sendScreenSwitchMessage(0)
                    extend(0)
                }
            }

            override fun onLoginLoading(
                type: HikVisionViewType?,
                hikVisionVideoStatus: HikVisionVideoStatus?
            ) {

            }

            override fun onLoginSuccess(type: HikVisionViewType?) {

            }

            override fun onLoginFailure(tag: Any?, type: HikVisionViewType?) {
                Toast.makeText(context,"信号一登陆失败",Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context,"信号一预览失败",Toast.LENGTH_SHORT).show()

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
                sendScreenSwitchMessage(1)
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
                Toast.makeText(context,"信号二登陆失败",Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context,"信号二预览失败",Toast.LENGTH_SHORT).show()

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


        mTextureView?.setOnMultiClickListener {
            if (mDefaultMode == MODE.TWO) {
                if (mDefaultISDevice) {
                    initTwoMode(!mDefaultISDevice)
                } else {

                }
            } else {
                sendScreenSwitchMessage(3)
                extend(3)

            }
        }
        mMapView?.setOnClickListener {

        }
    }

    private fun initFourMode() {
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

    private fun initTwoMode(defaultISDevice: Boolean) {

        val id0 = mHikVisionVideoView1?.id!!
        //val id1 = mHikVisionVideoView2?.id!!
        //val id2 = mMapView?.id!!
        val id3 = mTextureView?.id!!


        //获取当前目标控件的约束集合
        mConstraintSet?.clone(mHkParentConstraintLayout)

        mConstraintSet?.clear(id0)
        //mConstraintSet?.clear(id1)
        //mConstraintSet?.clear(id2)
        mConstraintSet?.clear(id3)

        if (defaultISDevice) {
            mTextureView?.bringToFront()

            //左上角
            mConstraintSet?.constrainWidth(id0, mExtendWidth)
            mConstraintSet?.constrainHeight(id0, mExtendWidth)
            mConstraintSet?.connect(
                id0,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )
            mConstraintSet?.connect(
                id0,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )
            mConstraintSet?.connect(
                id0,
                ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT
            )
            mConstraintSet?.connect(
                id0,
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT
            )
            //右下角
            mConstraintSet?.constrainWidth(id3, mExtendWidth / 3)
            mConstraintSet?.constrainHeight(id3, mExtendWidth / 3)
            mConstraintSet?.connect(id3, ConstraintSet.RIGHT, id0, ConstraintSet.RIGHT)
            mConstraintSet?.connect(id3, ConstraintSet.BOTTOM, id0, ConstraintSet.BOTTOM)
        } else {
            mHikVisionVideoView1?.bringToFront()
            //左上角
            mConstraintSet?.constrainWidth(id3, mExtendWidth)
            mConstraintSet?.constrainHeight(id3, mExtendWidth)
            mConstraintSet?.connect(
                id3,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )
            mConstraintSet?.connect(
                id3,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )
            mConstraintSet?.connect(
                id3,
                ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT
            )
            mConstraintSet?.connect(
                id3,
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT
            )

            //右下角
            mConstraintSet?.constrainWidth(id0, mExtendWidth / 3)
            mConstraintSet?.constrainHeight(id0, mExtendWidth / 3)
            mConstraintSet?.connect(id0, ConstraintSet.RIGHT, id3, ConstraintSet.RIGHT)
            mConstraintSet?.connect(
                id0,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )
        }

        //右上角
        //mConstraintSet?.constrainWidth(id1, 0)
        //mConstraintSet?.constrainHeight(id1, 0)

        //左下角
        //mConstraintSet?.constrainWidth(id2, 0)
        // mConstraintSet?.constrainHeight(id2, 0)

        mConstraintSet!!.applyTo(mHkParentConstraintLayout)
        mDefaultISDevice = defaultISDevice
    }

    private fun sendScreenSwitchMessage(position: Int) {
        if (mDefaultMode == MODE.FOR) {
            if (isExtend) {
                onSelectPositionListener.invoke(4)
            } else {
                onSelectPositionListener.invoke(position)
            }
        } else {
            onSelectPositionListener.invoke(5)
        }
    }

    private fun extend(position: Int) {
        if (isExtend) {
            doubleClickToRestore()
        } else {
            doubleClickToExtend(position)
        }
    }

    private fun doubleClickToExtend(position: Int) {
        when (mDefaultMode) {
            MODE.TWO -> {
                initTwoMode(false)
            }
            MODE.FOR -> {
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
        }
    }

    private fun doubleClickToRestore() {
        when (mDefaultMode) {
            MODE.FOR -> {
                initFourMode()
            }
            MODE.TWO -> {
                initTwoMode(true)
            }
        }
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
        Log.e("OnLifecycleEvent", "=========Lifecycle.Event.ON_CREATE")
        mMapView?.onCreate(null)
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView?.map
        }
        initMap()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStart() {
        Log.e("OnLifecycleEvent", "=========Lifecycle.Event.ON_START")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onStop() {
        Log.e("OnLifecycleEvent", "=========Lifecycle.Event.ON_STOP")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        Log.e("OnLifecycleEvent", "=========Lifecycle.Event.ON_RESUME")
        mMapView?.onResume()
        initHikVision()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() {
        Log.e("OnLifecycleEvent", "=========Lifecycle.Event.ON_PAUSE")
        mMapView?.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        Log.e("OnLifecycleEvent", "=========Lifecycle.Event.ON_DESTROY")
        //Toast.makeText(context,"destory",Toast.LENGTH_SHORT).show()
        mMapView?.onDestroy();
        mHikVisionVideoView1?.stopPreview()
        mHikVisionVideoView2?.stopPreview()
        mCameraProxy?.releaseCamera()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    private fun onAny() {
        Log.e("OnLifecycleEvent", "=========Lifecycle.Event.ON_ANY")
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
            mLocationOption?.locationMode = AMapLocationMode.Hight_Accuracy
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

    fun initMode(mode: MODE) {
        mDefaultMode = mode
        if (mDefaultMode == MODE.TWO) {
            mHikVisionVideoView2?.visibility = View.GONE
            mMapView?.visibility = View.GONE
            initTwoMode(true)
        } else {
            mHikVisionVideoView2?.visibility = View.VISIBLE
            mMapView?.visibility = View.VISIBLE
            initFourMode()
        }
        initCamera()
    }

    fun changeMode(num: Int) {
        if (num == 2) {
            initMode(MODE.TWO)
        } else if (num == 4) {
            initMode(MODE.FOR)
        }
    }

    fun changeMode(mode: MODE) {
        initMode(mode)
    }

    fun switchScreen(i: Int) {
        //Toast.makeText(context, "======: $i", Toast.LENGTH_SHORT).show()
        if (i == 4) {
            doubleClickToRestore()
        }
        if (i == 5) {
            if (mDefaultMode == MODE.TWO) {
                initTwoMode(!mDefaultISDevice)
            }
        }
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
                lastExtendPosition = i;
            }
        } else {
            //该次点击和上一次不同
            if (isExtend) {
                doubleClickToRestore()
            }
            extend(i)
            lastExtendPosition = i
        }

    }


}