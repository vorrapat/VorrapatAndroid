import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("cars")
    fun addCar(
        @Part("brand") brand: RequestBody,
        @Part("model") model: RequestBody,
        @Part("mfg") mfg: RequestBody,
        @Part("color") color: RequestBody,
        @Part("price") price: RequestBody,
        @Part("fuel_type") fuelType: RequestBody,
        @Part("gear_type") gearType: RequestBody,
        @Part("doors") doors: RequestBody,
        @Part("seats") seats: RequestBody,
        @Part image: MultipartBody.Part // ฟิลด์สำหรับไฟล์
    ): Call<Void>
}