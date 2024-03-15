package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//这个类负责向API发起调用请求并获得返回的JSON数据
interface PlaceService {
    //使用@GET注解，当调用这个方法的时候，Retrofit就会发起一条GET请求，去访问@GET注解中配置的地址
    @GET("v2/place?token=${SunnyWeatherApplication.TOKEN}&lang=zh_CN")
    //方法的返回值被声明成了Call<PlaceResponse>，这样Retrofit就会将服务器返回的JSON数据自动解析成PlaceResponse对象
    fun searchPlaces(@Query("query") query: String): Call<PlaceResponse>
}