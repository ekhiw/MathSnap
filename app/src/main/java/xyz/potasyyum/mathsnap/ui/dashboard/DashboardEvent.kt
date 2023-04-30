package xyz.potasyyum.mathsnap.ui.dashboard

import android.net.Uri
import java.io.File

sealed class DashboardEvent {
    data class GetTextFromPicture(val file: File): DashboardEvent()
}
