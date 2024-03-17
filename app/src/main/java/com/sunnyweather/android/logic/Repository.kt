package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.PlaceResponse
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

//仓库层的统一封装入口
object Repository {
    //为了将异步获取的数据以响应式编程的方式通知给上一层，通常会返回一个LiveData对象
    //这里的LiveData()函数有一个特性：
    //它可以自动构建并且返回一个LiveData对象，然后在它的代码块中提供一个挂起函数的上下文，
    //这样我们就可以在LiveData()的代码块中调用任意的挂起函数了
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    //仓库层直接提供一个方法同时获取实时天气和未来天气（通过SunnyWeatherNetwork的两个方法调用WeatherService）
    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        //并发执行获取实时天气和未来天气两个操作
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather =
                    Weather(
                        realtimeResponse.result.realtime,
                        dailyResponse.result.daily
                    )
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    //fire是一个按照liveData()函数参数接收标准定义的一个高阶函数，在fire()内部线调用一下liveData()函数，
    //然后在liveData函数代码块中统一进行了try catch处理，并将结果使用emit方法发射出去，从而避免了每一个调用都要
    //用一次try catch的繁琐
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
}


