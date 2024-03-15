package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers

//仓库层的统一封装入口
object Repository {
    //为了将异步获取的数据以响应式编程的方式通知给上一层，通常会返回一个LiveData对象
    //这里的LiveData()函数有一个特性：
    //它可以自动构建并且返回一个LiveData对象，然后在它的代码块中提供一个挂起函数的上下文，
    //这样我们就可以在LiveData()的代码块中调用任意的挂起函数了
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        //liveData函数的线程参数类型被指定成了Dispatchers.IO，这样代码块中所有的代码就都运行在子线程中了
        val result = try {
            //这里调用了SunnyWeatherNetwork.searchPlaces()来搜索城市数据，然后判断
            //如果服务器的响应状态是ok，那么就使用Kotlin内置的Result.success()方法来包装获取到的城市数据列表
            //否则就用Result.failure()方法来包装一个异常信息
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        //最后使用一个emit()方法将包装的结果发送出去
        emit(result)
    }
}