package com.top.arch.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment


/**
 * Kotlin 拓展函数帮助类
 */

@Suppress("UNCHECKED_CAST")
fun <F : Fragment> AppCompatActivity.getNavigationFragment(fragmentClass: Class<F>): F? {
    val navHostFragment = this.supportFragmentManager.fragments.first() as NavHostFragment

    navHostFragment.childFragmentManager.fragments.forEach {
        if (fragmentClass.isAssignableFrom(it.javaClass)) {
            return it as F
        }
    }
    return null
}


@Suppress("UNCHECKED_CAST")
fun <F : Fragment> Fragment.getChildFragment(fragmentClass: Class<F>): F? {
    val navFragment = this.childFragmentManager.fragments

    navFragment.forEach {
        if (fragmentClass.isAssignableFrom(it.javaClass)) {
            return it as F
        }
    }
    return null
}

