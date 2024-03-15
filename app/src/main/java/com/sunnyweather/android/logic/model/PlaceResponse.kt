package com.sunnyweather.android.logic.model

import android.location.Location
import com.google.gson.annotations.SerializedName

//定义数据类以接受API返回的JSON数据
//JSON字段的一些命名与Kotlin的命名规范有所不同，所以使用了@SerializedName注解啦让JSON字段与Kotlin字段之间建立映射关系
data class PlaceResponse(val status: String, val places: List<Place>)
data class Place(
    val name: String,
    val location: Location,
    @SerializedName("formatted_address") val address: String
)

data class Location(val lng: String, val lat: String)