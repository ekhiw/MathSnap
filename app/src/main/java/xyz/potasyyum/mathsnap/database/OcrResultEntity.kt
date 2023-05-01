package xyz.potasyyum.mathsnap.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import xyz.potasyyum.mathsnap.domain.OcrResultItem

@Entity
data class OcrResultEntity constructor(
    @PrimaryKey(true)
    val id: Int? = null,
    val leftNumber: String,
    val mathOperator: String,
    val rightNumber: String,
    val result : String,
)

fun List<OcrResultEntity>.asDomainModel(): List<OcrResultItem> {
    return map {
        OcrResultItem(
            it.leftNumber,
            it.mathOperator,
            it.rightNumber,
            it.result
        )
    }
}