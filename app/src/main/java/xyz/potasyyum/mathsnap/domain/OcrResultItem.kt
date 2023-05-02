package xyz.potasyyum.mathsnap.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OcrResultItem(

    @Json(name="leftNumber")
    val leftNumber: String,

    @Json(name="mathOperator")
    val mathOperator: String,

    @Json(name="rightNumber")
    val rightNumber: String,

    @Json(name="result")
    val result : String,
)
