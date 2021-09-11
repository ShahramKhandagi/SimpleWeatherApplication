package com.example.weatherapplication

import android.R
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ContentView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ContentInfoCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.weatherapplication.databinding.ActivityMainBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val tehran = "https://api.openweathermap.org/data/2.5/weather?q=tehran&appid=008f62bb7acf946eb8be7d3a2f3c282b&lang=fa&units=metric"
    val tabriz = "https://api.openweathermap.org/data/2.5/weather?q=tabriz&appid=a8b5ae98e7fbf8d55e1fef5777eb6b5f&lang=fa&units=metric"
    val mashhad = "https://api.openweathermap.org/data/2.5/weather?q=mashhad&appid=a8b5ae98e7fbf8d55e1fef5777eb6b5f&lang=fa&units=metric"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getCurrentCalenderAndTime()
        getData()


    }

    @SuppressLint("ResourceAsColor")
    private fun showValue(
            cityName: String,
            description: String,
            temp: Double,
            imageUrl: String,
            sunRise: Int,
            sunSet: Int,
            tempMin: Double,
            tempMax: Double,
            airPressure: Int,
            humidity: Int,
            windDeg: Int,
            windSpeed: Double,
            feelLike: Double,
            cloudiness: Int
    ) {
        binding.textViewCityName.text = cityName
        binding.textViewWeatherDescription.text = description
        binding.textViewTemp.text = temp.toString() + "°"
        binding.textViewSunRise.text = getTimeFromUnixTime(sunRise) + " AM"
        binding.textViewSunSet.text = getTimeFromUnixTime(sunSet) + " PM"
        binding.textViewTemperaturedown.text = tempMin.toString()
        binding.textViewTemperatureup.text = tempMax.toString()
        binding.textViewAirPressure.text = airPressure.toString()
        binding.textViewHumidity.text = humidity.toString()
        binding.textViewWindDeg.text = windDeg.toString()
        binding.textViewWindSpeed.text = windSpeed.toString()
        binding.textViewFeelLike.text = feelLike.toString()
        binding.textViewCloudiness.text = cloudiness.toString() + "%"
        // Glide : Upload Weather Icon
        Glide
                .with(this)
                .load(imageUrl)
                .into(binding.imageViewWeatherIcon)


        //===================================================================================================
        //Add the ability to change the background day and night with the time of sunrise and sunset
        val timeSunRise = sunRise * 1000.toLong()
        val dateSunRise = Date(timeSunRise)
        val formatterSunRise = SimpleDateFormat("HH")
        val hourFormatterSunRise = formatterSunRise.format(dateSunRise).toInt()
        val timeSunSet = sunSet * 1000.toLong()
        val dateSunSet = Date(timeSunSet)
        val formatterSunSet = SimpleDateFormat("HH")
        val hourFormatterSunSet = formatterSunSet.format(dateSunSet).toInt()
        val simpleDateFormatter = SimpleDateFormat("HH")
        val currentHour = simpleDateFormatter.format(Date()).toInt()
        if (currentHour >= hourFormatterSunSet || currentHour <= hourFormatterSunRise) {
            binding.mainPaper.setBackgroundResource(com.example.weatherapplication.R.drawable.nightlandscapewallpaper)
            binding.textViewCurrentTime.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.textViewCityName.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.textViewWeatherDescription.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.textViewTemp.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.textViewTehran.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.textViewTabriz.setTextColor(Color.parseColor("#FFFFFFFF"))
            binding.textViewMashhad.setTextColor(Color.parseColor("#FFFFFFFF"))

        }
        //===================================================================================================
    }

    private fun getTimeFromUnixTime(unixTime: Int): String {
        val time = unixTime * 1000.toLong()
        val date = Date(time)
        val formatter = SimpleDateFormat("HH:mm")
        return formatter.format(date)
    }


    private fun getData() {


        val client = OkHttpClient()

        var request = Request.Builder()
                .url(tabriz)
                .build()

        //================================================
        // change cities data
        if (binding.textViewTehran.isPressed) {
            request = Request.Builder()
                    .url(tehran)
                    .build()
        } else if (binding.textViewTabriz.isPressed) {
            request = Request.Builder()
                    .url(tabriz)
                    .build()
        } else if (binding.textViewMashhad.isPressed) {
            request = Request.Builder()
                    .url(mashhad)
                    .build()
        }
        //================================================


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.d("tagx", "onFailure: Failed")
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call, response: Response) {
                val rowContent = response.body!!.string()
                getDataAndShowThem(rowContent)
                binding.progressBar.visibility = View.INVISIBLE
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    getCurrentCalenderAndTime()
                }
            }
        })
    }


    private fun getDataAndShowThem(rawData: String) {

        val jsonObject = JSONObject(rawData)
        val weatherArray = jsonObject.getJSONArray("weather")
        val weatherObject = weatherArray.getJSONObject(0)
        val iconId = weatherObject.getString("icon")
        val imageUrl = "https://openweathermap.org/img/wn/${iconId}@2x.png"
        val temp = jsonObject.getJSONObject("main").getDouble("temp")

        //==================================================================================
        // round Temperature number
        val number3digits: Double = String.format("%.3f", temp).toDouble()
        val number2digits: Double = String.format("%.2f", number3digits).toDouble()
        val number1digits: Double = String.format("%.1f", number2digits).toDouble()
        val roundTemperature: Double = String.format("%.0f", number1digits).toDouble()

        //==================================================================================


        val sunRise = jsonObject.getJSONObject("sys").getInt("sunrise")
        val sunSet = jsonObject.getJSONObject("sys").getInt("sunset")
        val tempMin = jsonObject.getJSONObject("main").getDouble("temp_min")
        val tempMax = jsonObject.getJSONObject("main").getDouble("temp_max")
        val airPressure = jsonObject.getJSONObject("main").getInt("pressure")
        val humidity = jsonObject.getJSONObject("main").getInt("humidity")
        val windDeg = jsonObject.getJSONObject("wind").getInt("deg")
        val windSpeed = jsonObject.getJSONObject("wind").getDouble("speed")
        val feelLike = jsonObject.getJSONObject("main").getDouble("feels_like")
        val cloudiness = jsonObject.getJSONObject("clouds").getInt("all")



        runOnUiThread {
            showValue(
                    jsonObject.getString("name"),
                    weatherObject.getString("description"),
                    roundTemperature,
                    imageUrl,
                    sunRise,
                    sunSet,
                    tempMin,
                    tempMax,
                    airPressure,
                    humidity,
                    windDeg,
                    windSpeed,
                    feelLike,
                    cloudiness
            )
        }

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    fun reloadData(view: View) {


        setDefaultOption()

        if (binding.mainPaper.background.constantState == resources.getDrawable(com.example.weatherapplication.R.drawable.daylandscapewallpaper).constantState) {
            val selectedTextView = view as TextView
            selectedTextView.background = ContextCompat.getDrawable(this, com.example.weatherapplication.R.drawable.citytext_backgroundlight)
            selectedTextView.setTextColor(Color.parseColor("#132C42"))
        } else if (binding.mainPaper.background.constantState == resources.getDrawable(com.example.weatherapplication.R.drawable.nightlandscapewallpaper).constantState) {
            val selectedTextView = view as TextView
            selectedTextView.background = ContextCompat.getDrawable(this, com.example.weatherapplication.R.drawable.citytext_backgrounddark)
            selectedTextView.setTextColor(Color.parseColor("#FFFFFFFF"))
        }





        binding.progressBar.visibility = View.VISIBLE
        binding.textViewCityName.text = "---"
        binding.textViewWeatherDescription.text = "---"
        binding.textViewTemp.text = "---"
        binding.textViewSunRise.text = "---"
        binding.textViewSunSet.text = "---"
        binding.textViewTemperaturedown.text = "---"
        binding.textViewTemperatureup.text = "---"
        binding.textViewAirPressure.text = "---"
        binding.textViewHumidity.text = "---"
        binding.textViewWindDeg.text = "---"
        binding.textViewWindSpeed.text = "---"
        binding.textViewFeelLike.text = "---"
        binding.textViewCloudiness.text = "---"
        binding.textViewCalender.text = "---"
        binding.textViewDay.text = "---"
        binding.textViewCurrentTime.text = "--:--"
        binding.imageViewWeatherIcon.setImageResource(com.example.weatherapplication.R.drawable.ic_refresh_24)


        getData()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentCalenderAndTime() {
        //=========================================================================
        // get current date
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val monthFormatter = SimpleDateFormat("MM")
        val currentMonth = monthFormatter.format(Date())
        val day = LocalDate.now().dayOfWeek.name
        if (day == "SATURDAY") {
            binding.textViewDay.text = "شنبه"
        } else if (day == "SUNDAY") {
            binding.textViewDay.text = "یکشنبه"
        } else if (day == "MONDAY") {
            binding.textViewDay.text = "دوشنبه"
        } else if (day == "TUESDAY") {
            binding.textViewDay.text = "سه شنبه"
        } else if (day == "WEDNESDAY") {
            binding.textViewDay.text = "چهارشنبه"
        } else if (day == "THURSDAY") {
            binding.textViewDay.text = "پنجشنبه"
        } else if (day == "FRIDAY") {
            binding.textViewDay.text = "جمعه"
        }
        binding.textViewCalender.text = "$currentMonth $year"
        val simpleDateFormatter = SimpleDateFormat("HH:mm")
        val currentTime = simpleDateFormatter.format(Date())
        binding.textViewCurrentTime.text = currentTime
        //=========================================================================
    }

    // TODO NIGHT OPTION
    fun setDefaultOption() {
        var textViewCities = ArrayList<TextView>()
        textViewCities.add(binding.textViewTabriz)
        textViewCities.add(binding.textViewTehran)
        textViewCities.add(binding.textViewMashhad)



        if (binding.mainPaper.background.constantState == resources.getDrawable(com.example.weatherapplication.R.drawable.daylandscapewallpaper).constantState) {
            for (textViewCities in textViewCities) {
                textViewCities.setBackgroundResource(0)
                textViewCities.setTextColor(Color.parseColor("#132C42"))
            }
        } else if (binding.mainPaper.background.constantState == resources.getDrawable(com.example.weatherapplication.R.drawable.nightlandscapewallpaper).constantState) {
            for (textViewCities in textViewCities) {
                textViewCities.setBackgroundResource(0)
                textViewCities.setTextColor(Color.parseColor("#FFFFFFFF"))
            }
        }
    }


}