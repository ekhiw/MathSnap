package xyz.potasyyum.mathsnap.network.model

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class OcrResponse(

	@Json(name="IsErroredOnProcessing")
	val isErroredOnProcessing: Boolean? = null,

	@Json(name="ParsedResults")
	val parsedResults: List<ParsedResultsItem?>? = null,

	@Json(name="ProcessingTimeInMilliseconds")
	val processingTimeInMilliseconds: String? = null,

	@Json(name="SearchablePDFURL")
	val searchablePDFURL: String? = null,

	@Json(name="OCRExitCode")
	val oCRExitCode: Int? = null
)

@JsonClass(generateAdapter = true)
data class TextOverlay(

	@Json(name="Lines")
	val lines: List<LinesItem?>? = null,

	@Json(name="HasOverlay")
	val hasOverlay: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class ParsedResultsItem(

	@Json(name="TextOrientation")
	val textOrientation: String? = null,

	@Json(name="ParsedText")
	val parsedText: String? = null,

	@Json(name="FileParseExitCode")
	val fileParseExitCode: Int? = null,

	@Json(name="ErrorDetails")
	val errorDetails: String? = null,

	@Json(name="ErrorMessage")
	val errorMessage: String? = null,

	@Json(name="TextOverlay")
	val textOverlay: TextOverlay? = null
)

@JsonClass(generateAdapter = true)
data class WordsItem(

	@Json(name="Left")
	val left: Any? = null,

	@Json(name="Top")
	val top: Any? = null,

	@Json(name="Height")
	val height: Any? = null,

	@Json(name="WordText")
	val wordText: String? = null,

	@Json(name="Width")
	val width: Any? = null
)

@JsonClass(generateAdapter = true)
data class LinesItem(

	@Json(name="LineText")
	val lineText: String? = null,

	@Json(name="MinTop")
	val minTop: Any? = null,

	@Json(name="Words")
	val words: List<WordsItem?>? = null,

	@Json(name="MaxHeight")
	val maxHeight: Any? = null
)
