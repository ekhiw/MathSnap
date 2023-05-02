package xyz.potasyyum.mathsnap.ui.dashboard

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.potasyyum.mathsnap.domain.OcrResultItem
import xyz.potasyyum.mathsnap.domain.OcrResultList
import xyz.potasyyum.mathsnap.domain.TabPos

data class DashboardUiState(
    var list: OcrResultList = OcrResultList(),
    var parsedTextList: MutableList<String> = mutableListOf(),
    var openResultDialog : Boolean = false,
    var equationResult : String = "",
    val loadingState: MutableStateFlow<Boolean> = MutableStateFlow(false),
    val tabState: TabPos = TabPos.ROOMTAB
)
