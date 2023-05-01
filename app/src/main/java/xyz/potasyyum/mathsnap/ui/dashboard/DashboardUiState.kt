package xyz.potasyyum.mathsnap.ui.dashboard

import xyz.potasyyum.mathsnap.domain.OcrResultItem

data class DashboardUiState(
    var list: MutableList<OcrResultItem> = mutableListOf(),
    var parsedTextList: MutableList<String> = mutableListOf(),
    var openResultDialog : Boolean = false,
    var equationResult : String = "",
    var isLoading : Boolean = false
)
