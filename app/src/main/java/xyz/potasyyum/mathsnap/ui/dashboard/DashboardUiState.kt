package xyz.potasyyum.mathsnap.ui.dashboard

data class DashboardUiState(
    var list: MutableList<String> = mutableListOf(),
    var openResultDialog : Boolean = false,
    var equationResult : String = "",
    var isLoading : Boolean = false
)
