package xyz.potasyyum.mathsnap.ui.dashboard

import kotlinx.coroutines.flow.MutableStateFlow
import xyz.potasyyum.mathsnap.domain.OcrResultItem

data class DashboardUiState(
    var list: MutableList<OcrResultItem> = mutableListOf(),
    var parsedTextList: MutableList<String> = mutableListOf(),
    var openResultDialog : Boolean = false,
    var equationResult : String = "",
    val loadingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
)
