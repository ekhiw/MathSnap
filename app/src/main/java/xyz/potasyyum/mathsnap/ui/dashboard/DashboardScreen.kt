package xyz.potasyyum.mathsnap.ui.dashboard

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.collectLatest
import xyz.potasyyum.mathsnap.BuildConfig
import xyz.potasyyum.mathsnap.R
import xyz.potasyyum.mathsnap.core.utils.TestTags
import xyz.potasyyum.mathsnap.domain.OcrResultItem
import xyz.potasyyum.mathsnap.domain.OcrResultList
import xyz.potasyyum.mathsnap.domain.TabPos
import xyz.potasyyum.mathsnap.ui.theme.MathSnapTheme
import xyz.potasyyum.mathsnap.ui.theme.tailwindColors
import xyz.potasyyum.mathsnap.util.Utils

@Composable
fun DashboardScreen(
    uiState : DashboardUiState,
    onEvent : (DashboardEvent) -> Unit
) {
    val scaffoldState = rememberScaffoldState()

    var loadingState by remember { mutableStateOf(false) }
    val ctx = LocalContext.current
    val file = Utils.createImageFile(ctx)
    val uri: Uri = FileProvider.getUriForFile(
        ctx,
        "xyz.potasyyum.mathsnap.provider",
        file
    )

    val launcherCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Utils.resizeAndRotateImage(uri,ctx)
                onEvent(DashboardEvent.GetTextFromPicture(file))
            }
        }
    val launcherImage =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) {
                Utils.resizeAndRotateImage(it,ctx,uri)
                Logger.i("EKHIW ${file.isFile} ${file.length()}")
                onEvent(DashboardEvent.GetTextFromPicture(file))
            }
        }

    LaunchedEffect(key1 = true) {
        uiState.loadingState.collectLatest { isLoading ->
            loadingState = isLoading
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
            bottomBar = {
                BottomAppBar(
                    backgroundColor = MaterialTheme.colors.primary,
                    cutoutShape = RoundedCornerShape(50),
                    content = {
                        BottomNavigationItem(
                            icon = { Icon(painterResource(R.drawable.database_48px), "Room db") },
                            selected = uiState.tabState == TabPos.ROOMTAB,
                            onClick = { onEvent(DashboardEvent.ChangeTabSelection(TabPos.ROOMTAB)) },
                            selectedContentColor = tailwindColors().blue800,
                            unselectedContentColor = Color.White,
                        )
                        BottomNavigationItem(
                            icon = { Icon(painterResource(R.drawable.folder_open_48px), "File") },
                            selected = uiState.tabState == TabPos.FILETAB,
                            onClick = { onEvent(DashboardEvent.ChangeTabSelection(TabPos.FILETAB)) },
                            selectedContentColor = tailwindColors().blue800,
                            unselectedContentColor = Color.White,
                        )
                    }
                )

            },
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
                            enabled = !BuildConfig.FS_ONLY && !loadingState,
                            onClick = {
                                requestToOpen = false
                                launcherCamera.launch(uri)
                            }
                        ) {
                            Text(text = if (!BuildConfig.FS_ONLY && loadingState) "Loading.." else "From camera")
                        }
                        DropdownMenuItem(
                            enabled = BuildConfig.FS_ONLY && !loadingState,
                            onClick = {
                                requestToOpen = false
                                launcherImage.launch(
                                    PickVisualMediaRequest(
                                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                        ) {
                            Text(text = if (BuildConfig.FS_ONLY && loadingState) "Loading.." else "From gallery")
                        }
                    }
                    FloatingActionButton(
                        onClick = {
                            requestToOpen = true
                        },
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
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp))
                    {
                        Text(text = "MathSnap",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        AnimatedVisibility(
                            visible = loadingState,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                                contentAlignment = Alignment.Center) {
                                Text(text = "Loading...",
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .testTag(TestTags.LOADING_TEXT))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        if (uiState.list.ocrResultList.isEmpty()) {
                            Text(text = "List is empty, click add to scan text")
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()) {
                                ListOcrResult(uiState)
                            }
                        }
                    }
                }
            }
        )
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
        items(uiState.list.ocrResultList) { item ->
            Card(
                shape = RoundedCornerShape(8.dp),
                backgroundColor = tailwindColors().gray800,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = "${item.leftNumber}${item.mathOperator}${item.rightNumber}=${item.result}")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MathSnapTheme {
        val itemList = mutableListOf<OcrResultItem>()
        (1..100).forEach {
            itemList.add(OcrResultItem("4","*","2","$it"))
        }
        DashboardScreen(uiState = DashboardUiState(
            list = OcrResultList(itemList)
        ), onEvent = { event ->

        })
    }
}