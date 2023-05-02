package xyz.potasyyum.mathsnap.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class OcrResultList(

    @Json(name="ocrResultList")
    val ocrResultList: MutableList<OcrResultItem> = mutableListOf()
)
