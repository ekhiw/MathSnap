package xyz.potasyyum.mathsnap.repository

import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import xyz.potasyyum.mathsnap.database.AppDatabase
import xyz.potasyyum.mathsnap.database.OcrResultEntity
import xyz.potasyyum.mathsnap.database.asDomainModel
import xyz.potasyyum.mathsnap.domain.OcrResultItem
import xyz.potasyyum.mathsnap.domain.OcrResultList
import xyz.potasyyum.mathsnap.network.OcrApi
import xyz.potasyyum.mathsnap.network.model.OcrRequest
import xyz.potasyyum.mathsnap.network.model.OcrResponse
import xyz.potasyyum.mathsnap.util.Resource
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class OcrRepository @Inject constructor(
    private val ocrApi: OcrApi,
    private val appDatabase: AppDatabase,
    @Named("jsonfile") private val jsonFile : File
) {

    val ocrList: Flow<OcrResultList?> =
        appDatabase.ocrResultDao.getOcrList().map { it?.asDomainModel() }

    fun getListFromJsonFile() : String {
        return jsonFile.readText()
    }

    fun writeListToJsonFile(jsonString: String) {
        jsonFile.writeText(jsonString)
    }

    fun insertOcrResult(ocrResultItem : OcrResultItem) {
        val ocrResultEntity = OcrResultEntity(
            leftNumber = ocrResultItem.leftNumber,
            mathOperator = ocrResultItem.mathOperator,
            rightNumber = ocrResultItem.rightNumber,
            result = ocrResultItem.result
        )
        appDatabase.ocrResultDao.insertOcrItem(ocrResultEntity)
    }

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