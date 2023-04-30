package xyz.potasyyum.mathsnap.ui.dashboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.orhanobut.logger.Logger
import xyz.potasyyum.mathsnap.util.Utils
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    uiState : DashboardUiState,
    onEvent : (DashboardEvent) -> Unit
) {
    val scaffoldState = rememberScaffoldState()

    val ctx = LocalContext.current
    val file = Utils.createImageFile(ctx)
    val uri: Uri = FileProvider.getUriForFile(
        ctx,
        "xyz.potasyyum.mathsnap.provider",
        file
    )
    val result = remember { mutableStateOf<Bitmap?>(null) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Utils.resizeAndRotateImage(uri,ctx)
                result.value = imageFromResult(ctx, uri)
                onEvent(DashboardEvent.GetTextFromPicture(file))
            }
        }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButtonPosition = FabPosition.Center,
            isFloatingActionButtonDocked = true,
            floatingActionButton = {
                var requestToOpen by remember {
                    mutableStateOf(false)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    DropdownMenu(
                        modifier = Modifier.wrapContentWidth(),
                        expanded = requestToOpen,
                        onDismissRequest = { requestToOpen = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                requestToOpen = false
                                launcher.launch(uri)
                            }
                        ) {
                            Text("From camera")
                        }
                        DropdownMenuItem(
                            onClick = {
                                requestToOpen = false
                                launcher.launch(uri)
                            }
                        ) {
                            Text("From gallery")
                        }
                    }
                    FloatingActionButton(
                        onClick = {
                            requestToOpen = true
                        },
                        backgroundColor = Color.Red,
                        contentColor = Color.White,
                    ) { Icon(Icons.Filled.Add, "Add") }
                }
            },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            bottom = it.calculateBottomPadding()
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()) {
//                        result.value?.let { image ->
//                            Image(image.asImageBitmap(), contentDescription = null)
//                        }
                        ListOcrResult(uiState)
                    }
                }
            }
        )
    }
}

private fun imageFromResult(context: Context, uri: Uri): Bitmap? {
    return try {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor
        val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor?.close()
        return bitmap
    } catch (e: IOException) {
        Logger.e("${e.message}")
        null
    }
}

@Composable
fun ListOcrResult (uiState: DashboardUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .background(Color.Transparent)
    ) {
        items(uiState.list) { item ->
            Text(text = item)
        }
    }
}
