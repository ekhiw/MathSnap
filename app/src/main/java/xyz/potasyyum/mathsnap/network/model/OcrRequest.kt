package xyz.potasyyum.mathsnap.network.model

import java.io.File

data class OcrRequest(
    val apiKey: String,
    val file: File,
    val ocrEngine: String
)
