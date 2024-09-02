package com.example.vorrapatandroid

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.content.Intent
import com.example.vorrapatandroid.ui.CarCheck


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // เรียกใช้ฟังก์ชันการเชื่อมต่อ API
        fetchCarsData()

        // ตั้งค่า event listener สำหรับปุ่ม
        val addButton = findViewById<android.widget.Button>(R.id.addcarbtn)
        addButton.setOnClickListener {
            // เปิดหน้า AddCarActivity เมื่อกดปุ่ม
            val intent = Intent(this, AddCarActivity::class.java)
            startActivity(intent)
        }

        // ตั้งค่า event listener สำหรับปุ่ม viewcarbtn
        val viewButton = findViewById<android.widget.Button>(R.id.viewcarbtn)
        viewButton.setOnClickListener {
            // เปิดหน้า CarCheckActivity เมื่อกดปุ่ม
            val intent = Intent(this, CarCheck::class.java)
            startActivity(intent)
        }

        supportActionBar?.hide()
    }

    private fun fetchCarsData() {
        // ใช้ Thread แยกต่างหากเพื่อเชื่อมต่อกับ API
        Thread {
            try {
                val url = URL("http://10.0.2.2:3000/cars")  // เปลี่ยน URL ให้ตรงกับ API ของคุณ
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"

                val inputStream = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (inputStream.readLine().also { line = it } != null) {
                    response.append(line)
                }

                inputStream.close()
                urlConnection.disconnect()

                // แสดงผลลัพธ์ที่ได้รับจาก API
                runOnUiThread {
                    Toast.makeText(this@MainActivity, response.toString(), Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    // แสดง Toast เมื่อการเชื่อมต่อ API ล้มเหลว
                    Toast.makeText(this@MainActivity, "Failed to connect to API", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}