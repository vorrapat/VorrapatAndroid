package com.example.vorrapatandroid

import ApiService
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class AddCarActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null

    // Register the ActivityResultLauncher for image picking
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            imageView.setImageURI(it)
            Log.d("RequestDebug", "Selected image URI: $imageUri")
        } ?: run {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addcars)

        val brandEditText = findViewById<EditText>(R.id.brandedittxt)
        val modelEditText = findViewById<EditText>(R.id.modeledittxt)
        val mfgEditText = findViewById<EditText>(R.id.yearedittxt)
        val colorEditText = findViewById<EditText>(R.id.coloredittxt)
        val priceEditText = findViewById<EditText>(R.id.priceedittxt)
        val fuelTypeEditText = findViewById<EditText>(R.id.fueledittxt)
        val gearTypeEditText = findViewById<EditText>(R.id.gearedittxt)
        val doorsEditText = findViewById<EditText>(R.id.dooreach)
        val seatsEditText = findViewById<EditText>(R.id.seateach)
        imageView = findViewById(R.id.imageView)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val selectImageButton = findViewById<Button>(R.id.button3)

        selectImageButton.setOnClickListener {
            openImagePicker()
        }

        saveButton.setOnClickListener {
            val brand = brandEditText.text.toString().trim()
            val model = modelEditText.text.toString().trim()
            val mfg = mfgEditText.text.toString().toIntOrNull()
            val color = colorEditText.text.toString()
            val price = priceEditText.text.toString().toDoubleOrNull()
            val fuelType = fuelTypeEditText.text.toString()
            val gearType = gearTypeEditText.text.toString()
            val doors = doorsEditText.text.toString().toIntOrNull()
            val seats = seatsEditText.text.toString().toIntOrNull()

            Log.d("DEBUG", "Brand: $brand, Model: $model, Mfg: $mfg, Color: $color, Price: $price, FuelType: $fuelType, GearType: $gearType, Doors: $doors, Seats: $seats, ImageUri: $imageUri")

            if (brand.isNotEmpty() && model.isNotEmpty() && mfg != null && color.isNotEmpty() &&
                price != null && fuelType.isNotEmpty() && gearType.isNotEmpty() &&
                doors != null && seats != null && imageUri != null) {

                saveCar(brand, model, mfg, color, price, fuelType, gearType, doors, seats)
            } else {
                Toast.makeText(this, "กรุณากรอกข้อมูลให้ครบทุกช่องและเลือกรูปภาพด้วย", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openImagePicker() {
        pickImageLauncher.launch("image/*")
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = it.getString(columnIndex)
            }
        }
        return path
    }

    private fun saveCar(
        brand: String, model: String, mfg: Int, color: String, price: Double,
        fuelType: String, gearType: String, doors: Int, seats: Int
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)

        val brandPart = brand.toRequestBody("text/plain".toMediaTypeOrNull())
        val modelPart = model.toRequestBody("text/plain".toMediaTypeOrNull())
        val mfgPart = mfg.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val colorPart = color.toRequestBody("text/plain".toMediaTypeOrNull())
        val pricePart = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val fuelTypePart = fuelType.toRequestBody("text/plain".toMediaTypeOrNull())
        val gearTypePart = gearType.toRequestBody("text/plain".toMediaTypeOrNull())
        val doorsPart = doors.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val seatsPart = seats.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart = imageUri?.let {
            val file = File(getRealPathFromURI(it) ?: return)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestFile)  // เปลี่ยนจาก "image_url" เป็น "image"
        }

        if (imagePart == null) {
            Toast.makeText(this, "กรุณาเลือกภาพ", Toast.LENGTH_SHORT).show()
            return
        }

        service.addCar(brandPart, modelPart, mfgPart, colorPart, pricePart, fuelTypePart, gearTypePart, doorsPart, seatsPart, imagePart)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddCarActivity, "Car added successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AddCarActivity, "Failed to add car: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@AddCarActivity, "Failed to connect", Toast.LENGTH_SHORT).show()
                }
            })
    }
}