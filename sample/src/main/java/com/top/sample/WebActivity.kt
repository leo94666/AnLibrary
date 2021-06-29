package com.top.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_webview.*

/**
 *
 * android中的scheme是一种页面内跳转协议。
 * 通过定义自己的scheme协议，可以非常方便跳转app中的各个页面；
 * 通过scheme协议，服务器可以定制化告诉App跳转到APP内部页面。
 *
 * Scheme协议在Android中使用场景
 *      - H5跳转到native页面
 *      - 客户端获取push消息中后，点击消息跳转到APP内部页面
 *      - APP根据URL跳转到另外一个APP指定页面


 *
 *
<!-- 要想在别的App上能成功调起App，必须添加intent过滤器 -->
<!-- 协议部分，随便设置 -->
<intent-filter>

    <!--下面这几行也必须得设置-->
    <action android:name="android.intent.action.VIEW"/>
    <category android:name="android.intent.category.BROWSABLE"/>
    <category android:name="android.intent.category.DEFAULT"/>

    <!--协议部分，随便设置-->
    <data
        android:host="com.android.xg.ebs"
        android:port="8089"
        android:scheme="xg"
        android:path="/patient" />

</intent-filter>

 */

class WebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        web_view.loadUrl("file:///android_asset/go.html")
    }


}