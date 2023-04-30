package xyz.potasyyum.mathsnap.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import xyz.potasyyum.mathsnap.BuildConfig
import xyz.potasyyum.mathsnap.network.model.OcrRequest
import xyz.potasyyum.mathsnap.repository.OcrRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val ocrRepository: OcrRepository
) : ViewModel() {

    var uiState by mutableStateOf(DashboardUiState())
        private set

    init {
        val test = mutableListOf<String>()
        test.add("item1")
        test.add("item2")

        uiState = uiState.copy(
            list = test
        )
    }

    fun onEvent(event : DashboardEvent) {
        when (event) {
            is DashboardEvent.GetTextFromPicture -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val ocrRequest = OcrRequest(
                        apiKey = BuildConfig.API_KEY,
                        file = event.file,
                        ocrEngine = "2"
                    )
                    val response = ocrRepository.getOcr(ocrRequest)
                    if (response.data != null){
                        Logger.d(response.data.parsedResults)
                        response.data.parsedResults?.let { parsedList ->
                            parsedList.forEach {
                                val regex = Regex("^\\d+([*\\/+-]\\d+)+$")
                                val match = regex.find("${it?.parsedText}")
                                if (match != null) {
                                    Logger.d("${it?.parsedText} is a valid math equation")
                                } else {
                                    Logger.d("${it?.parsedText} is not a valid math equation")
                                }
                            }

                        }
                    }
                }
            }
            else -> {

            }
        }
    }
}
