package xyz.potasyyum.mathsnap.ui.dashboard

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class DashboardScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun showList() {
        val itemList = mutableListOf<String>()
        itemList.add("item1")
        itemList.add("item2")

        composeRule.setContent {
            DashboardScreen(uiState = DashboardUiState(
                list = itemList
            ), onEvent = { event ->

            })
        }

        itemList.forEach { item ->
            composeRule.onNodeWithText(item).assertIsDisplayed()
        }
    }

    @Test
    fun showFab() {
        composeRule.setContent {
            DashboardScreen(uiState = DashboardUiState(
                list = mutableListOf()
            ), onEvent = { event ->

            })
        }

        composeRule.onNodeWithContentDescription("Add").assertIsDisplayed()
    }

    @Test
    fun showDropdownMenu() {
        composeRule.setContent {
            DashboardScreen(uiState = DashboardUiState(
                list = mutableListOf()
            ), onEvent = { event ->

            })
        }

        composeRule.onNodeWithContentDescription("Add").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Add").performClick()
        composeRule.onNodeWithText("From camera").assertIsDisplayed()
        composeRule.onNodeWithText("From gallery").assertIsDisplayed()
    }
}