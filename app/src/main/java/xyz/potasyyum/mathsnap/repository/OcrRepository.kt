package xyz.potasyyum.mathsnap.repository

import com.orhanobut.logger.Logger
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import xyz.potasyyum.mathsnap.network.OcrApi
import xyz.potasyyum.mathsnap.network.model.OcrRequest
import xyz.potasyyum.mathsnap.network.model.OcrResponse
import xyz.potasyyum.mathsnap.util.Resource
import javax.inject.Inject

class OcrRepository @Inject constructor(
    private val ocrApi: OcrApi,
) {


    suspend fun getOcr(request : OcrRequest) : Resource<OcrResponse> {
        return try {
            val image = MultipartBody.Part.createFormData(
                "file",
                request.file.name,
                request.file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            val map : HashMap<String, RequestBody> = HashMap()
            map["apikey"] = request.apiKey.toRequestBody()
            map["OCREngine"] = request.ocrEngine.toRequestBody()

            val response = ocrApi.getTextFromImage(image,map)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error("${e.message}")
        }
    }
}