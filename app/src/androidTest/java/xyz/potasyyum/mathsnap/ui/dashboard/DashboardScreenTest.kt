package xyz.potasyyum.mathsnap.ui.dashboard

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import xyz.potasyyum.mathsnap.core.utils.TestTags
import xyz.potasyyum.mathsnap.di.DatabaseModule
import xyz.potasyyum.mathsnap.di.NetworkModule
import xyz.potasyyum.mathsnap.domain.OcrResultItem
import xyz.potasyyum.mathsnap.domain.OcrResultList


@HiltAndroidTest
@UninstallModules(NetworkModule::class, DatabaseModule::class)
class DashboardScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun setup() {
        val itemList = mutableListOf<OcrResultItem>()
        itemList.add(OcrResultItem("4","*","2","8"))

        composeRule.setContent {
            DashboardScreen(uiState = DashboardUiState(
                list = OcrResultList(itemList),
                loadingState = MutableStateFlow(true),
            ), onEvent = { event ->

            })
        }
    }

    @Test
    fun showList() {
        val itemList = mutableListOf<OcrResultItem>()
        itemList.add(OcrResultItem("4","*","2","8"))

        composeRule.setContent {
            DashboardScreen(uiState = DashboardUiState(
                listState = MutableStateFlow(OcrResultList(itemList)),
                list = OcrResultList(itemList)
            ), onEvent = { event ->

            })
        }
        composeRule.onNodeWithText("4*2=8").assertIsDisplayed()
    }

    @Test
    fun showFab() {
        composeRule.setContent {
            DashboardScreen(uiState = DashboardUiState(
                list = OcrResultList()
            ), onEvent = { event ->

            })
        }

        composeRule.onNodeWithContentDescription("Add").assertIsDisplayed()
    }

    @Test
    fun showDropdownMenu() {
        composeRule.setContent {
            DashboardScreen(uiState = DashboardUiState(
                list = OcrResultList()
            ), onEvent = { event ->

            })
        }

        composeRule.onNodeWithContentDescription("Add").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Add").performClick()
        composeRule.onNodeWithText("From camera").assertIsDisplayed()
        composeRule.onNodeWithText("From gallery").assertIsDisplayed()
    }
    @Test
    fun showLoading() {
        composeRule.setContent {
            DashboardScreen(uiState = DashboardUiState(
                list = OcrResultList(),
                loadingState = MutableStateFlow(true),
            ), onEvent = { event ->

            })
        }
        composeRule.onNodeWithTag(TestTags.LOADING_TEXT).assertIsDisplayed()
    }
}