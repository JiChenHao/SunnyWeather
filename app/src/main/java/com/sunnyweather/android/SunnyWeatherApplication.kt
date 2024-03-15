package com.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

//全局类，继承自Application，用于更加方便的在项目的任何位置获取全局Context，
//注意要更新注册的Application
class SunnyWeatherApplication : Application() {
    //伴生实例类，用于获取全局Context
    companion object {
        //填入申请到的彩云APP令牌，方便全局使用这个值
        const val TOKEN = "Zj080K63KvYBcxSm"
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}