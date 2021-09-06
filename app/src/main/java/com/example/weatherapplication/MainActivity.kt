package com.example.weatherapplication

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapplication.databinding.ActivityMainBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //=========================================================================
        // get current date
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val monthFormatter = SimpleDateFormat("MM")
        val currentMonth = monthFormatter.format(Date())
        val day = LocalDate.now().getDayOfWeek().name

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
        val simpleDateFormatter = SimpleDateFormat("hh:mm")
        val currentTime = simpleDateFormatter.format(Date())
        binding.textViewCurrentTime.text = currentTime
        //=========================================================================


        val client = OkHttpClient()
        val request = Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?q=tehran&appid=008f62bb7acf946eb8be7d3a2f3c282b&lang=fa&units=metric")
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.d("tagx", "onFailure: Failed")
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call, response: Response) {
                val rowContent = response.body!!.string()
                val jsonObject = JSONObject(rowContent)

                val weatherArray = jsonObject.getJSONArray("weather")
                val weatherObject = weatherArray.getJSONObject(0)

                val iconId = weatherObject.getString("icon")
                val imageUrl = "http://openweathermap.org/img/wn/${iconId}@2x.png"
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
                            windSpeed
                    )
                }
            }

        })

    }

    fun showValue(
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
            windSpeed: Double
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






//        Glide.with(this).load(imageUrl).into(binding.)

    }
    fun getTimeFromUnixTime(unixTime: Int): String {
        val time = unixTime * 1000.toLong()
        val date = Date(time)
        val formatter = SimpleDateFormat("HH:mm")
        return formatter.format(date)
    }
}