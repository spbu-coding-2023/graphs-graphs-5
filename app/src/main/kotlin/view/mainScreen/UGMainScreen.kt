package view.mainScreen

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import view.theme.Material3AppTheme
import view.theme.Theme
import view.graph.UndirectedGraphView
import view.inputs.DBInput
import view.inputs.MenuInput
import viewmodel.mainScreenVM.UGScreenViewModel

import kotlin.math.sign

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <V> UGMainScreen(viewModel: UGScreenViewModel<V>, theme: MutableState<Theme>) {
    Material3AppTheme(theme = theme.value) {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        var menuInputState by remember { mutableStateOf(MenuInput()) }
        var message by remember { mutableStateOf("") }

        var showSnackbar by remember { mutableStateOf(false) }
        var isGraphLoaded by remember { mutableStateOf(false) }
        var showDBSelectionDialogue by remember { mutableStateOf(false) }
        var selectedDatabase by remember { mutableStateOf("") }
        var dBInput by remember { mutableStateOf(DBInput()) }
        var loadGraph by remember { mutableStateOf(false) }

        fun showSnackbarMessage(message: String) {
            if (message.isNotEmpty()) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message,
                        "Dismiss",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
        Scaffold(
            backgroundColor = MaterialTheme.colorScheme.surface,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState
                ) { snackbarData ->
                    val snackbarBackgroundColor = if (message.contains("Error")) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.background
                    }
                    val snackbarContentColor = if (message.contains("Error")) {
                        MaterialTheme.colorScheme.onError
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }

                    Snackbar(
                        snackbarData = snackbarData,
                        backgroundColor = snackbarBackgroundColor,
                        contentColor = snackbarContentColor,
                        actionColor = snackbarContentColor
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { showDBSelectionDialogue = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            text = "Load graph",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (!isGraphLoaded) {
                                message =
                                    "No connection to database provided, please load your graph from database before saving algorithm results"
                            } else {
                                message = viewModel.saveAlgoResults()
                                showSnackbar = message.isNotEmpty()
                                message = "Results saved!"
                            }
                            showSnackbarMessage(message)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = if (isGraphLoaded) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background
                        )
                    ) {
                        Text(
                            text = "Save",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    ThemeSwitcher(
                        theme,
                        size = 45.dp,
                        padding = 5.dp
                    )
                }
                Row {
                    Column(modifier = Modifier.width(300.dp)) {
                        Spacer(modifier = Modifier.padding(8.dp))
                        showVerticesLabels(viewModel)
                        showEdgesLabels(viewModel)
                        resetGraphView(viewModel)
                        Button(
                            onClick = {
                                when (menuInputState.text) {
                                    "Min path (Dijkstra)" -> {
                                        if (menuInputState.inputStartTwoVer.isNotEmpty() && menuInputState.inputEndTwoVer.isNotEmpty()) {
                                            message = viewModel.run(menuInputState)
                                            showSnackbar = message.isNotEmpty()
                                        } else {
                                            showSnackbar = true
                                            message =
                                                "Error: no required parameter for chosen algo was passed. Please enter parameter"
                                        }
                                    }

                                    else -> message = viewModel.run(menuInputState)
                                }
                                if (message.isNotEmpty()) {
                                    showSnackbar = true
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = "Run", color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                        val newState = menu(viewModel.getListOfAlgorithms())
                        menuInputState = menuInputState.copy(
                            text = newState.text,
                            inputValueOneVertex = newState.inputValueOneVertex,
                            inputStartTwoVer = newState.inputStartTwoVer,
                            inputEndTwoVer = newState.inputEndTwoVer
                        )
                    }
                    var scale by viewModel.scale
                    var offset by viewModel.offset
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .onPointerEvent(PointerEventType.Scroll) {
                                val change = it.changes.first()
                                val scrollAmount = change.scrollDelta.y.toInt().sign
                                scale = adjustScale(scrollAmount, scale)
                            }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    offset += DpOffset(
                                        (dragAmount.x * (1 / scale)).toDp(),
                                        (dragAmount.y * (1 / scale)).toDp()
                                    )
                                }
                            }
                    ) {
                        UndirectedGraphView(
                            viewModel.graphViewModel,
                            scale,
                            offset
                        )
                    }
                }
            }
            if (showDBSelectionDialogue) {
                showDBSelectionDialogue(
                    showDBSelectionDialogue,
                    selectedDatabase,
                    dBInput,
                    { showDBSelectionDialogue = it },
                    { selectedDatabase = it },
                    { dBInput = it },
                    { loadGraph = it },
                    { isGraphLoaded = it }
                )
            }
            if (loadGraph) {
                val newMessage = drawGraph(viewModel, dBInput)
                if (newMessage.isNotEmpty()) {
                    if (newMessage != message) {
                        message = newMessage
                        showSnackbar = true
                        loadGraph = false
                    }
                } else {
                    isGraphLoaded = true
                }
            }
            if (showSnackbar) {
                showSnackbarMessage(message)
                showSnackbar = false
            }
        }
    }
}