package xyz.potasyyum.mathsnap.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import xyz.potasyyum.mathsnap.network.model.OcrResponse

interface OcrApi {

    @JvmSuppressWildcards
    @Multipart
    @POST("/parse/image")
    suspend fun getTextFromImage(
        @Part image: MultipartBody.Part,
        @PartMap data: Map<String,RequestBody>
    ): Response<OcrResponse>
}