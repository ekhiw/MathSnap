package xyz.potasyyum.mathsnap.ui.dashboard

import android.net.Uri
import xyz.potasyyum.mathsnap.domain.TabPos
import java.io.File

sealed class DashboardEvent {
    data class GetTextFromPicture(val file: File): DashboardEvent()
    data class ChangeTabSelection(val tab: TabPos): DashboardEvent()
    object CloseDialog: DashboardEvent()
}
