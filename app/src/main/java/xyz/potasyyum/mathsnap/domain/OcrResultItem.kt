package xyz.potasyyum.mathsnap.domain

data class OcrResultItem(
    val leftNumber: String,
    val mathOperator: String,
    val rightNumber: String,
    val result : String,
)
