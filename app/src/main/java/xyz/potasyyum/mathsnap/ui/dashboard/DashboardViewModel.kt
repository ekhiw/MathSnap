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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.potasyyum.mathsnap.BuildConfig
import xyz.potasyyum.mathsnap.domain.OcrResultItem
import xyz.potasyyum.mathsnap.network.model.OcrRequest
import xyz.potasyyum.mathsnap.network.model.OcrResponse
import xyz.potasyyum.mathsnap.repository.OcrRepository
import xyz.potasyyum.mathsnap.util.Utils
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val ocrRepository: OcrRepository
) : ViewModel() {

    var uiState by mutableStateOf(DashboardUiState())
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            ocrRepository.ocrList.collect {
                if (!it.isNullOrEmpty()) {
                    val resultList = mutableListOf<OcrResultItem>()
                    resultList.addAll(it)
                    withContext(Dispatchers.Main) {
                        uiState = uiState.copy(
                            list = resultList
                        )
                    }
                }
            }
        }
    }

    private fun handleOcrResult(response: OcrResponse) {
        response.parsedResults?.let { parsedList ->
            val parsedTextList = mutableListOf<String>()
            parsedList.forEach { it ->
                it?.parsedText?.split("\n")?.forEach { line ->
                    parsedTextList.add(line.replace("\\s".toRegex(), ""))
                }
            }
            uiState = uiState.copy(
                parsedTextList = parsedTextList
            )
            parsedTextList.forEach {
                val regex = Regex("(\\d+)([×xX*\\/+-])(\\d+)")
                val match = regex.find(it)
                if (match != null) {
                    val groups = match.groupValues.drop(1)
                    Logger.d("$it is a valid math equation")
                    Logger.d("EKHIW \n${groups}")
                    val result = Utils.calculateMathExpression(groups)
                    val ocrResultItem = OcrResultItem(
                        groups[0],
                        groups[1],
                        groups[2],
                        "$result"
                    )

                    ocrRepository.insertOcrResult(ocrResultItem)
                    val test = mutableListOf<OcrResultItem>()
                    test.addAll(uiState.list)
                    test.add(ocrResultItem)

                    uiState = uiState.copy(
                        list = test,
                        equationResult = it,
                    )
                } else {
                    Logger.d("$it is not a valid math equation")
                }
            }
            uiState = uiState.copy(
                openResultDialog = true,
            )

        }
    }

    fun onEvent(event : DashboardEvent) {
        when (event) {
            is DashboardEvent.GetTextFromPicture -> {
                viewModelScope.launch(Dispatchers.IO) {
                    uiState.loadingState.emit(true)
                    val ocrRequest = OcrRequest(
                        apiKey = BuildConfig.API_KEY,
                        file = event.file,
                        ocrEngine = "2"
                    )
                    val response = ocrRepository.getOcr(ocrRequest)
                    if (response.data != null){
                        handleOcrResult(response.data)
                        uiState.loadingState.emit(false)
                    } else {
                        // TODO error handling
                        uiState.loadingState.emit(false)
                    }
                }
            }
            is DashboardEvent.CloseDialog -> {
                uiState = uiState.copy(
                    openResultDialog = false
                )
            }
            else -> {

            }
        }
    }
}
