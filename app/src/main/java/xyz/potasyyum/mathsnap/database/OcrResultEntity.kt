package xyz.potasyyum.mathsnap.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import xyz.potasyyum.mathsnap.domain.OcrResultItem
import xyz.potasyyum.mathsnap.domain.OcrResultList

@Entity
data class OcrResultEntity constructor(
    @PrimaryKey(true)
    val id: Int? = null,
    val leftNumber: String,
    val mathOperator: String,
    val rightNumber: String,
    val result : String,
)

fun List<OcrResultEntity>.asDomainModel(): OcrResultList {
    val tempList : MutableList<OcrResultItem> = mutableListOf()
    tempList.addAll(
        map {
            OcrResultItem(
                it.leftNumber,
                it.mathOperator,
                it.rightNumber,
                it.result
            )
        }
    )
    return OcrResultList(tempList)
}