package com.example.vorrapatandroid.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vorrapatandroid.MainActivity
import com.example.vorrapatandroid.R
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class CarCheck : AppCompatActivity() {

    private lateinit var brandView: TextView
    private lateinit var modelView: TextView
    private lateinit var mfgView: TextView
    private lateinit var colorView: TextView
    private lateinit var priceView: TextView
    private lateinit var gearView: TextView
    private lateinit var fuelView: TextView
    private lateinit var doorView: TextView
    private lateinit var seatView: TextView
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_check)

        // Initialize views
        brandView = findViewById(R.id.brandview)
        modelView = findViewById(R.id.modelview)
        mfgView = findViewById(R.id.mfgview)
        colorView = findViewById(R.id.colorview)
        priceView = findViewById(R.id.priceview)
        gearView = findViewById(R.id.gearview)
        fuelView = findViewById(R.id.fuelv)
        doorView = findViewById(R.id.doorview)
        seatView = findViewById(R.id.seatview)
        imageView = findViewById(R.id.imageView)

        val backbtn = findViewById<android.widget.Button>(R.id.backButton)
        backbtn.setOnClickListener {
            // Go back to MainActivity when the back button is clicked
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Fetch car data from the server
        fetchCarData()
    }

    private fun fetchCarData() {
        val client = OkHttpClient()
        val url = "http://10.0.2.2:3000/cars" // ใช้ /cars เพื่อดึงข้อมูลรถคันล่าสุด

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CarCheck, "Failed to fetch car data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@CarCheck, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                response.body?.let { responseBody ->
                    val responseData = responseBody.string()
                    val jsonObject = JSONObject(responseData)
                    runOnUiThread {
                        // Set data to views
                        brandView.text = jsonObject.optString("brand")
                        modelView.text = jsonObject.optString("model")
                        mfgView.text = jsonObject.optString("mfg")
                        colorView.text = jsonObject.optString("color")
                        priceView.text = jsonObject.optString("price")
                        gearView.text = jsonObject.optString("gear_type")
                        fuelView.text = jsonObject.optString("fuel_type")
                        doorView.text = jsonObject.optString("doors")
                        seatView.text = jsonObject.optString("seats")

                        // Load image using Picasso
                        val imagePart = jsonObject.optString("image_url")
                        Picasso.get()
                            .load("http://10.0.2.2:3000/uploads/${imagePart}")
                            .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                            .error(R.drawable.error_image) // Image to show if there's an error
                            .into(imageView, object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    // Image loaded successfully
                                }

                                override fun onError(e: Exception?) {
                                    // Handle the error
                                    e?.printStackTrace() // Print the stack trace for debugging
                                }
                            })
                    }
                }
            }
        })
    }
}