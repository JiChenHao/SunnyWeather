package com.sunnyweather.android.ui.weather

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.databinding.ActivityWeatherBinding
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale

//显示特定城市天气的界面

class WeatherActivity : AppCompatActivity() {
    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }
    private lateinit var activityWeatherBinding: ActivityWeatherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        activityWeatherBinding = ActivityWeatherBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        //====START美化界面，将Activity布局设置到状态栏上面并将状态栏设置为透明
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        //====END美化界面
        setContentView(activityWeatherBinding.root)
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }

    private fun showWeatherInfo(weather: Weather) {

        activityWeatherBinding.nowLayout.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        //填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toInt()}℃"
        activityWeatherBinding.nowLayout.currentTemp.text = currentTempText
        activityWeatherBinding.nowLayout.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数${realtime.airQuality.aqi.chn.toInt()}"
        activityWeatherBinding.nowLayout.currentAQI.text = currentPM25Text
        activityWeatherBinding.nowLayout.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        //填充forecast.xml布局中的数据
        activityWeatherBinding.forecastLayout.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(
                R.layout.forecast_item,
                activityWeatherBinding.forecastLayout.forecastLayout,
                false
            )
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()}~${temperature.max.toInt()}℃"
            temperatureInfo.text = tempText
            activityWeatherBinding.forecastLayout.forecastLayout.addView(view)
        }
        //填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        activityWeatherBinding.lifeIndexLayout.coldRiskText.text = lifeIndex.coldRisk[0].desc
        activityWeatherBinding.lifeIndexLayout.dressingText.text = lifeIndex.dressing[0].desc
        activityWeatherBinding.lifeIndexLayout.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        activityWeatherBinding.lifeIndexLayout.carWashingText.text = lifeIndex.carWashing[0].desc
        activityWeatherBinding.weatherLayout.visibility = View.VISIBLE
    }
}