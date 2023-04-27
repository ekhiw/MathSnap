package xyz.potasyyum.mathsnap.ui.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
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
}
