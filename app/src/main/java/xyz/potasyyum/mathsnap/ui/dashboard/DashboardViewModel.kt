package xyz.potasyyum.mathsnap.ui.dashboard

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xyz.potasyyum.mathsnap.BuildConfig
import xyz.potasyyum.mathsnap.domain.OcrResultItem
import xyz.potasyyum.mathsnap.domain.OcrResultList
import xyz.potasyyum.mathsnap.domain.TabPos
import xyz.potasyyum.mathsnap.network.model.OcrRequest
import xyz.potasyyum.mathsnap.network.model.OcrResponse
import xyz.potasyyum.mathsnap.repository.OcrRepository
import xyz.potasyyum.mathsnap.util.Utils
import javax.inject.Inject

@OptIn(ExperimentalStdlibApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val ocrRepository: OcrRepository,
    private val moshi: Moshi
) : ViewModel() {

    var uiState by mutableStateOf(DashboardUiState())
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            ocrRepository.ocrList.collect {
                if (it != null && it.ocrResultList.isNotEmpty()) {
                    val resultList = OcrResultList(it.ocrResultList)
                    withContext(Dispatchers.Main) {
                        uiState.listState.emit(resultList)
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

                    val tempList = uiState.list
                    tempList.ocrResultList.add(ocrResultItem)

                    if (uiState.tabState == TabPos.ROOMTAB) {
                        ocrRepository.insertOcrResult(ocrResultItem)
                    } else {
                        val adapter = moshi.adapter<OcrResultList>()
                        ocrRepository.writeListToJsonFile(Utils.encryptText(adapter.toJson(tempList)))
                    }

                    viewModelScope.launch(Dispatchers.Main) {
                        uiState.listState.emit(tempList)
                    }

                    uiState = uiState.copy(
                        list = tempList,
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

    private fun handleTabChange(tab: TabPos) {
        when(tab) {
            TabPos.ROOMTAB -> {
                viewModelScope.launch(Dispatchers.IO) {
                    ocrRepository.ocrList.collect {
                        if (it != null && it.ocrResultList.isNotEmpty()) {
                            val resultList = OcrResultList(it.ocrResultList)
                            withContext(Dispatchers.Main) {
                                uiState.listState.emit(resultList)
                                uiState = uiState.copy(
                                    list = resultList
                                )
                            }
                        } else {
                            uiState.listState.emit(OcrResultList())
                            uiState = uiState.copy(
                                list = OcrResultList()
                            )
                        }
                    }
                }
            }
            TabPos.FILETAB -> {
                val storageString = ocrRepository.getListFromJsonFile()
                if (storageString.isNotEmpty()) {
                    val decryptString = Utils.decryptText(storageString)
                    val adapter = moshi.adapter<OcrResultList>()
                    val decryptedResult = adapter.fromJson(decryptString)
                    viewModelScope.launch(Dispatchers.Main) {
                        uiState.listState.emit(decryptedResult ?: OcrResultList())
                    }
                    uiState = uiState.copy(
                        list = decryptedResult ?: OcrResultList(),
                    )
                } else {
                    viewModelScope.launch(Dispatchers.Main) {
                        uiState.listState.emit(OcrResultList())
                    }
                    uiState = uiState.copy(
                        list = OcrResultList(),
                    )
                }
            }
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
            is DashboardEvent.ChangeTabSelection -> {
                handleTabChange(event.tab)
                uiState = uiState.copy(
                    tabState = event.tab
                )
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
