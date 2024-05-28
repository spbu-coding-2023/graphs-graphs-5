//package view.mainScreen

import androidx.compose.foundation.gestures.detectDragGestures
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import org.sqlite.core.DB
import view.*
import viewmodel.*
import kotlin.math.exp
import kotlin.math.sign

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <V> DGMainScreen(viewModel: DGScreenViewModel<V>, theme: MutableState<Theme>) {
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
                    //val graphLoadingState = { mutableStateOf(isGraphLoaded) }
                    Button(
                        onClick = {
                            if (!isGraphLoaded) {
                                message = "No connection to database provided, please load your graph from database before saving algorithm results"
                                showSnackbar = true
                            } else {
                                message = viewModel.saveAlgoResults()
                                showSnackbar = message.isNotEmpty()
                            }
                            showSnackbarMessage(message)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = if (!isGraphLoaded) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background
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
                                message = handleAlgorithmExecution(viewModel, menuInputState)
                                showSnackbarMessage(message)
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
                        DirectedGraphView(
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
                }
                else {
                    isGraphLoaded = true
                }
            }
            if (showSnackbar) {
                showSnackbarMessage(message)
                showSnackbar = false // Сбрасываем флаг после показа Snackbar
            }
        }
    }
}

@Composable
fun showDBSelectionDialogue(
    showDBSelectionDialogue: Boolean,
    selectedDatabase: String,
    dBInput: DBInput,
    showDBselection: (Boolean) -> Unit,
    selectedDB: (String) -> Unit,
    onDBInputChange: (DBInput) -> Unit,
    onLoadGraphChange: (Boolean) -> Unit,
    isLoaded: (Boolean) -> Unit
) {
    var newState by remember { mutableStateOf(DBInput()) }
    if (showDBSelectionDialogue) {
        AlertDialog(
            onDismissRequest = { showDBselection(false) },
            title = { Text(text = "Load Graph") },
            text = {
                Column {
                    Text(text = "Select Database:")

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedDatabase == "neo4j",
                            onClick = { selectedDB("neo4j") }
                        )
                        Text(text = "neo4j")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedDatabase == "sqlite",
                            onClick = { selectedDB("sqlite") }
                        )
                        Text(text = "sqlite")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedDatabase == ".csv",
                            onClick = { selectedDB(".csv") }
                        )
                        Text(text = ".csv file")
                    }

                    when (selectedDatabase) {
                        "neo4j" -> {
                            newState = newState.copy(dBType = "neo4j")
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "Enter Neo4j Details:")
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newState.uri,
                                onValueChange = { newState = newState.copy(uri = it) },
                                label = { Text("URI") }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newState.login,
                                onValueChange = { newState = newState.copy(login = it) },
                                label = { Text("Login") }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newState.password,
                                onValueChange = { newState = newState.copy(password = it) },
                                label = { Text("Password") }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Switch(
                                    checked = newState.isUpdatedNeo4j,
                                    onCheckedChange = { newState = newState.copy(isUpdatedNeo4j = it) },
                                )
                                Text(
                                    text = "Is database updated? (no/yes) If no, results of algorithms from previous runs can be displayed",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Switch(
                                    checked = newState.isUndirected,
                                    onCheckedChange = { newState = newState.copy(isUndirected = it) },
                                )
                                Text(
                                    text = "Is graph undirected? (no/yes)",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        "sqlite" -> {
                            newState = newState.copy(dBType = "sqlite")
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Enter SQLite Details:")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newState.pathToDb,
                                onValueChange = { newState = newState.copy(pathToDb = it) },
                                label = { Text("Path to database") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newState.name,
                                onValueChange = { newState = newState.copy(name = it) },
                                label = { Text("Name") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Switch(
                                    checked = newState.isUpdatedSql,
                                    onCheckedChange = { newState = newState.copy(isUpdatedSql = it) },
                                )
                                Text(
                                    text = "Is database updated? (no/yes) If no, results of algorithms from previous runs can be displayed",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDBselection(false)
                        isLoaded(false)
                        onDBInputChange(dBInput.copy(
                            dBType = newState.dBType,
                            isUpdatedSql = newState.isUpdatedSql,
                            pathToDb = newState.pathToDb,
                            name = newState.name,
                            isUpdatedNeo4j = newState.isUpdatedNeo4j,
                            uri = newState.uri,
                            login = newState.login,
                            password = newState.password,
                            isUndirected = newState.isUndirected))
                        onLoadGraphChange(
                            when {
                                dBInput.dBType == "sqlite" && (dBInput.name.isEmpty() || dBInput.pathToDb.isEmpty()) -> false
                                else -> true
                            }
                        )
                    }
                ) {
                    Text("Load")
                }
            },
            dismissButton = {
                Button(onClick = { showDBselection(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun adjustScale(scrollAmount: Int, scale: Float): Float {
    return (scale * exp(scrollAmount * 0.1f)).coerceIn(0.05f, 4.0f)
}

fun handleAlgorithmExecution(viewModel: DGScreenViewModel<*>, menuInputState: MenuInput): String {
    return when (menuInputState.text) {
        "Cycles" -> {
            if (menuInputState.inputValueOneVertex.isNotEmpty()) {
                viewModel.run(menuInputState)
            } else {
                "Error: no required parameter for chosen algo was passed. Please enter parameter"
            }
        }
        "Min path (Dijkstra)", "Min path (Ford-Bellman)" -> {
            if (menuInputState.inputStartTwoVer.isNotEmpty() && menuInputState.inputEndTwoVer.isNotEmpty()) {
                viewModel.run(menuInputState)
            } else {
                "Error: no required parameter for chosen algo was passed. Please enter parameter"
            }
        }
        else -> viewModel.run(menuInputState)
    }
}


