package com.example.weatherapplication

import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.weatherapplication.databinding.ActivityMainBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val simpleDateFormatter = SimpleDateFormat("hh:mm")
        val currentTime = simpleDateFormatter.format(Date())
        binding.textViewCurrentTime.text = currentTime

        val client = OkHttpClient()
        val request = Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?q=tehran&appid=008f62bb7acf946eb8be7d3a2f3c282b&lang=fa&units=metric")
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.d("tagx", "onFailure: Failed")
            }

            override fun onResponse(call: Call, response: Response) {
                val rowContent = response.body!!.string()
                val jsonObject = JSONObject(rowContent)

                val weatherArray = jsonObject.getJSONArray("weather")
                val weatherObject = weatherArray.getJSONObject(0)

                val iconId = weatherObject.getString("icon")
                val imageUrl = "http://openweathermap.org/img/wn/${iconId}@2x.png"


                val temp = jsonObject.getJSONObject("main").getDouble("temp")

                runOnUiThread {
                    showValue(
                            jsonObject.getString("name"),
                            weatherObject.getString("description"),
                            temp,
                            imageUrl
                    )
                }
            }

        })

    }

    fun showValue(cityName: String, description: String, temp: Double, imageUrl: String) {
        binding.textViewCityName.text = cityName
        binding.textViewWeatherDescription.text = description
        binding.textViewTemp.text = temp.toString()

//        Glide.with(this).load(imageUrl).into(binding.)

    }
}