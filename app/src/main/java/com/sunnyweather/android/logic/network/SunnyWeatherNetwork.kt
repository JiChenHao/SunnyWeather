package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.await
import java.lang.RuntimeException
import javax.security.auth.callback.Callback
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//统一的网络数据源访问入口，对所有网络请求的API进行封装
object SunnyWeatherNetwork {
    //使用ServiceCreator创建了一个PlacesService接口的动态代理对象
    private val placeService = ServiceCreator.create<PlaceService>()

    //调用代理对象的方法发起搜索城市数据的请求
    //使用协程技术实现的Retrofit回调的简化写法，
    //并且将searchPlaces()函数声明成了挂起函数
    //这样，当外部调用这个函数的时候，Retrofit就会立即发起网络请求，同时当前的协程也会被阻塞
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : retrofit2.Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

            })
        }
    }
}