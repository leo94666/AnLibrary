package com.top.hook

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HookTest :IXposedHookLoadPackage {



    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam?.packageName.equals("com.top.hook")){
            XposedBridge.log("how hook!")
            val loadClass = lpparam?.classLoader?.loadClass("com.top.hook.MainActivity")
            XposedHelpers.findAndHookMethod(loadClass,"toastMessage",object:XC_MethodHook(){
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    super.beforeHookedMethod(param)

                }

                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    param?.result="您已被劫持"
                }
            })
        }
    }
}