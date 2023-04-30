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
import xyz.potasyyum.mathsnap.BuildConfig
import xyz.potasyyum.mathsnap.network.model.OcrRequest
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
        // TODO get list from db
        val test = mutableListOf<String>()
        test.add("1+2")
        test.add("100-5")

        uiState = uiState.copy(
            list = test
        )
    }

    fun onEvent(event : DashboardEvent) {
        when (event) {
            is DashboardEvent.GetTextFromPicture -> {
                viewModelScope.launch(Dispatchers.IO) {
                    uiState.isLoading = true
                    val ocrRequest = OcrRequest(
                        apiKey = BuildConfig.API_KEY,
                        file = event.file,
                        ocrEngine = "2"
                    )
                    val response = ocrRepository.getOcr(ocrRequest)
                    if (response.data != null){
                        response.data.parsedResults?.let { parsedList ->
                            val parsedTextList = mutableListOf<String>()
                            parsedList.forEach { it ->
                                it?.parsedText?.split("\n")?.forEach { line ->
                                    parsedTextList.add(line.replace("\\s".toRegex(), ""))
                                }
                            }
                            parsedTextList.forEach {
                                Logger.d("Loop EKHIW $it")
                                val regex = Regex("^(\\d+)([*\\/+-])(\\d+)$")
                                val match = regex.find(it)
                                if (match != null) {
                                    val groups = match.groupValues.drop(1)
                                    Logger.d("$it is a valid math equation")
                                    Logger.d("EKHIW \n${groups}")
                                    val result = Utils.calculateMathExpression(groups)

                                    // TODO save to database and show list from database
                                    val test = mutableListOf<String>()
                                    test.addAll(uiState.list)
                                    test.add("$it=${result}")

                                    uiState = uiState.copy(
                                        list = test,
                                        openResultDialog = true,
                                        equationResult = it,
                                        isLoading = false
                                    )
                                } else {
                                    // TODO show collect all non valid result, then show Dialog
                                    Logger.d("$it is not a valid math equation")
                                    uiState.isLoading = false
                                }
                            }

                        }
                    } else {
                        // TODO error handling
                        uiState.isLoading = false
                    }
                }
            }
            else -> {

            }
        }
    }
}
