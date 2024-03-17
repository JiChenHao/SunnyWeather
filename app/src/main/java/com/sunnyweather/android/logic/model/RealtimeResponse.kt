package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName

//获取实时天气信息接口返回的数据
data class RealtimeResponse(val status: String, val result: Result) {
    //这里将所有的数据模型类都定义在了内部，这样可以有效防止与其他数据模型类由同名冲突的情况
    data class Result(val realtime: Realtime)
    data class Realtime(
        val skycon: String, val temperature: Float,
        @SerializedName("air_quality") val airQuality: AirQuality
    )

    data class AirQuality(val aqi: AQI)
    data class AQI(val chn: Float)
}