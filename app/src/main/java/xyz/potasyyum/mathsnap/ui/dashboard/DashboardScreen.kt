package xyz.potasyyum.mathsnap.ui.dashboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.orhanobut.logger.Logger
import xyz.potasyyum.mathsnap.BuildConfig
import xyz.potasyyum.mathsnap.ui.theme.MathSnapTheme
import xyz.potasyyum.mathsnap.util.Utils
import java.io.*

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
                            enabled = !BuildConfig.FS_ONLY && !uiState.isLoading,
                            onClick = {
                                requestToOpen = false
                                launcher.launch(uri)
                            }
                        ) {
                            Text(text = if (!BuildConfig.FS_ONLY && uiState.isLoading) "Loading.." else "From camera")
                        }
                        DropdownMenuItem(
                            enabled = BuildConfig.FS_ONLY && !uiState.isLoading,
                            onClick = {
                                requestToOpen = false
                                launcher.launch(uri)
                            }
                        ) {
                            Text(text = if (BuildConfig.FS_ONLY && uiState.isLoading) "Loading.." else "From gallery")
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
                    if (uiState.openResultDialog){
                        Dialog(onDismissRequest = {
                            onEvent(DashboardEvent.CloseDialog)
                        }) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                shape = RoundedCornerShape(size = 10.dp)
                            ) {
                                Column(modifier = Modifier.padding(all = 16.dp)) {
                                    Text(
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(12.dp),
                                        text = "List of parsed text from response")
                                    Column(modifier = Modifier
                                        .padding(all = 16.dp)
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.6f)
                                        .verticalScroll(rememberScrollState())
                                    ) {
                                        uiState.parsedTextList.forEach {textItem ->
                                            Text(text = textItem)
                                        }
                                    }
                                    Box(modifier = Modifier
                                            .fillMaxWidth(1f)
                                            .padding(12.dp),
                                        contentAlignment = Alignment.Center) {
                                        Button(onClick = { onEvent(DashboardEvent.CloseDialog) }) {
                                            Text(text = "Close")
                                        }

                                    }
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()) {
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
            Text(text = "${item.leftNumber}${item.mathOperator}${item.rightNumber}=${item.result}")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MathSnapTheme {
//        DashboardScreen(uiState = DashboardUiState(
//            list = mutableListOf()
//        ), onEvent = { event ->
//
//        })
    }
}