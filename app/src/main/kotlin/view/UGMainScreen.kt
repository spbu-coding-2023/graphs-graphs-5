package view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import viewmodel.UGScreenViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <V> UGMainScreen(viewModel: UGScreenViewModel<V>, theme: MutableState<Theme>) {}
//    Material3AppTheme(theme = theme.value) {
//        val snackbarHostState = remember { SnackbarHostState() }
//        val scope = rememberCoroutineScope()
//        var menuInputState by remember { mutableStateOf(MenuInput()) }
//        var message by remember { mutableStateOf("") }
//
//        var isGraphLoaded by remember { mutableStateOf(false) }
//        var showDialog by remember { mutableStateOf(false) }
//        var selectedDatabase by remember { mutableStateOf("") }
//        var neo4jInput by remember { mutableStateOf(Neo4jInput()) }
//        var sqliteInput by remember { mutableStateOf(SQLiteInput()) }
//
//        var showSnackbar by remember { mutableStateOf(false) }
//
//        Scaffold(
//            backgroundColor = MaterialTheme.colorScheme.surface,
//            snackbarHost = {
//                SnackbarHost(
//                    hostState = snackbarHostState,
////            modifier = Modifier.padding(16.dp)
//                ) { snackbarData ->
//                    val snackbarBackgroundColor = if (message.contains("Error")) {
//                        MaterialTheme.colorScheme.error
//                    } else {
//                        MaterialTheme.colorScheme.background
//                    }
//                    val snackbarContentColor = if (message.contains("Error")) {
//                        MaterialTheme.colorScheme.onError
//                    } else {
//                        MaterialTheme.colorScheme.onSurface
//                    }
//                    Snackbar(
//                        snackbarData = snackbarData,
//                        backgroundColor = snackbarBackgroundColor,
//                        contentColor = snackbarContentColor,
//                        actionColor = snackbarContentColor
//                    )
//                }
//            }
//        ) {
//            Column(
//                modifier = Modifier.fillMaxSize().padding(16.dp)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Button(
//                        onClick = { showDialog = true },
//                        colors = ButtonDefaults.outlinedButtonColors(
//                            backgroundColor = MaterialTheme.colorScheme.secondary
//                        )
//                    ) {
//                        Text(
//                            text = "Load graph",
//                            color = MaterialTheme.colorScheme.onSurface,
//                            style = MaterialTheme.typography.bodyLarge
//                        )
//                    }
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Button(
//                        onClick = {
//                            if (!isGraphLoaded) {
//                                message = "no graph provided, please load your graph"
//                            }
//                            if (selectedDatabase == "sqlite") viewModel.saveToSqliteRepo(sqliteInput)
//                            if (selectedDatabase == "neo4j") TODO()
//                        },
//                        //enabled = isGraphLoaded,
//                        colors = ButtonDefaults.outlinedButtonColors(
//                            backgroundColor = if (isGraphLoaded) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background
//                        )
//                    ) {
//                        Text(
//                            text = "Save",
//                            color = MaterialTheme.colorScheme.onSurface,
//                            style = MaterialTheme.typography.bodyLarge
//                        )
//                    }
//                    Spacer(modifier = Modifier.weight(1f))
//                    ThemeSwitcher(
//                        theme,
//                        size = 45.dp,
//                        padding = 5.dp
//                    )
//                }
//                Row(
//                ) {
//                    Column(modifier = Modifier.width(300.dp)) {
//                        Spacer(modifier = Modifier.padding(8.dp))
//                        showVerticesLabels(viewModel)
//                        showEdgesLabels(viewModel)
//                        resetGraphView(viewModel)
//                        Button(
//                            onClick = {
//                                when (menuInputState.text) {
//                                    "Cycles" -> {
//                                        if (menuInputState.inputValueOneVertex != "") {
//                                            message = viewModel.run(menuInputState, dBInput)
//                                            showSnackbar = message.isNotEmpty()
//                                        } else {
//                                            showSnackbar = true
//                                            message =
//                                                "Error: no required parameter for chosen algo was passed. Please enter parameter"
//                                        }
//                                    }
//                                    //add another types
//                                    else -> message = viewModel.run(menuInputState, dBInput)
//
//
//                                }
//                                if (message.isNotEmpty()) {
//                                    showSnackbar = true
//                                }
//                            },
//                            enabled = true,
//                            colors = ButtonDefaults.outlinedButtonColors(
//                                backgroundColor = MaterialTheme.colorScheme.secondary
//                            ),
//                            modifier = Modifier.padding(4.dp)
//                        ) {
//                            Text(
//                                text = "Run", color = MaterialTheme.colorScheme.onSecondary
//                            )
//                        }
//                        val newState = menu(viewModel.getListOfAlgorithms())
//                        menuInputState = menuInputState.copy(
////                            algoNum = newState.algoNum,
//                            text = newState.text,
//                            inputValueOneVertex = newState.inputValueOneVertex,
//                            inputStartTwoVer = newState.inputStartTwoVer,
//                            inputEndTwoVer = newState.inputEndTwoVer
//                        )
//                    }
//                    var scale by viewModel.scale
//                    fun adjustScale(scrollAmount: Int) {
//                        scale = (scale * exp(scrollAmount * 0.1f)).coerceIn(0.05f, 4.0f)
//                    }
//                    var offset by viewModel.offset
//                    Surface(
//                        modifier = Modifier
//                            .weight(1f)
//                            .onPointerEvent(PointerEventType.Scroll) {
//                                val change = it.changes.first()
//                                val scrollAmount = change.scrollDelta.y.toInt().sign
//                                adjustScale(scrollAmount)
//                            }
//                            .pointerInput(Unit) {
//                                detectDragGestures { change, dragAmount ->
//                                    change.consume()
//                                    offset += DpOffset(
//                                        (dragAmount.x * (1 / scale)).toDp(),
//                                        (dragAmount.y * (1 / scale)).toDp()
//                                    )
//                                }
//                            }
//                    ) {
//                        UndirectedGraphView(
//                            viewModel.graphViewModel,
//                            scale,
//                            offset
//                        )
//                    }
//                }
//            }
//        }
//
//        var loadGraph by remember { mutableStateOf(false)}
//
//        if (showDialog) {
//            AlertDialog(
//                onDismissRequest = { showDialog = false },
//                title = { Text(text = "Load Graph") },
//                text = {
//                    Column {
//                        Text(text = "Select Database:")
//
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            RadioButton(
//                                selected = selectedDatabase == "neo4j",
//                                onClick = { selectedDatabase = "neo4j" }
//                            )
//                            Text(text = "neo4j")
//                        }
//
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            RadioButton(
//                                selected = selectedDatabase == "sqlite",
//                                onClick = { selectedDatabase = "sqlite" }
//                            )
//                            Text(text = "sqlite")
//                        }
//
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            RadioButton(
//                                selected = selectedDatabase == ".csv",
//                                onClick = { selectedDatabase = ".csv" }
//                            )
//                            Text(text = ".csv file")
//                        }
//
//                        if (selectedDatabase == "neo4j") {
//                            Spacer(modifier = Modifier.height(16.dp))
//
//                            Text(text = "Enter Neo4j Details:")
//                            Spacer(modifier = Modifier.height(8.dp))
//
//                            // Text fields for URI, login, password
//                            OutlinedTextField(
//                                value = neo4jInput.uri,
//                                onValueChange = {newURI ->
//                                    neo4jInput = neo4jInput.copy(uri = newURI)
//                                },
//                                label = { Text("URI") }
//                            )
//
//                            Spacer(modifier = Modifier.height(8.dp))
//
//                            OutlinedTextField(
//                                value = neo4jInput.login,
//                                onValueChange = {newLogin ->
//                                    neo4jInput = neo4jInput.copy(login = newLogin)   },
//                                label = { Text("Login") }
//                            )
//
//                            Spacer(modifier = Modifier.height(8.dp))
//
//                            OutlinedTextField(
//                                value = neo4jInput.password,
//                                onValueChange = {newPass ->
//                                    neo4jInput = neo4jInput.copy(password = newPass) },
//                                label = { Text("Password") }
//                            )
//
//                            Spacer(modifier = Modifier.height(8.dp))
//
//                            // Switcher for "is database updated"
//                            Row(modifier = Modifier.fillMaxWidth()) {
//                                Switch(
//                                    checked = neo4jInput.isUpdated,
//                                    onCheckedChange = {newState ->
//                                        neo4jInput = neo4jInput.copy(isUpdated = newState)},
//                                )
//                                Text(
//                                    text = "Is database updated? (no/yes) If no, results of algorithms from previous runs can be displayed",
//                                    modifier = Modifier.padding(16.dp)
//                                )
//                            }
//
//                            Row(modifier = Modifier.fillMaxWidth()) {
//                                Switch(
//                                    checked = neo4jInput.isUndirected,
//                                    onCheckedChange = {newState ->
//                                        neo4jInput = neo4jInput.copy(isUndirected = newState)},
//                                )
//                                Text(
//                                    text = "Is graph undirected? (no/yes)",
//                                    modifier = Modifier.padding(16.dp)
//                                )
//                            }
//                        }
//                        if (selectedDatabase == "sqlite") {
//                            Spacer(modifier = Modifier.height(16.dp))
//
//                            Text(text = "Enter SQLite Details:")
//                            Spacer(modifier = Modifier.height(8.dp))
//
//                            OutlinedTextField(
//                                value = sqliteInput.pathToDb,
//                                onValueChange = { newPathToDb ->
//                                    sqliteInput = sqliteInput.copy(pathToDb = newPathToDb)
//                                },
//                                label = { Text("Path to database") }
//                            )
//
//                            Spacer(modifier = Modifier.height(8.dp))
//                            OutlinedTextField(
//                                value = sqliteInput.name,
//                                onValueChange = { newName ->
//                                    sqliteInput = sqliteInput.copy(name = newName)
//                                },
//                                label = { Text("Name") }
//                            )
//                            Spacer(modifier = Modifier.height(8.dp))
//                            Row(modifier = Modifier.fillMaxWidth()) {
//                                Switch(
//                                    checked = sqliteInput.isUpdated,
//                                    onCheckedChange = { newState ->
//                                        sqliteInput = sqliteInput.copy(isUpdated = newState)
//                                    },
//                                )
//                                Text(
//                                    text = "Is database updated? (no/yes) If no, results of algorithms from previous runs can be displayed",
//                                    modifier = Modifier.padding(16.dp)
//                                )
//                            }
//                        }
//                    }
//                },
//                //переместить эту кнопку внутрь конкретной дата базы
//                confirmButton = {
//                    Button(
//                        onClick = {
//                            isGraphLoaded = if (selectedDatabase == "neo4j" && neo4jInput.uri.isNotEmpty()
//                                && neo4jInput.login.isNotEmpty() && neo4jInput.password.isNotEmpty()
//                            ) true
//                            else if (selectedDatabase == "sqlite" && sqliteInput.pathToDb.isNotEmpty()
//                                && sqliteInput.name.isNotEmpty()
//                            ) true
//                            else false
//                            showDialog = false
//                            loadGraph = if (selectedDatabase == "neo4j" && neo4jInput.uri.isNotEmpty()
//                                && neo4jInput.login.isNotEmpty() && neo4jInput.password.isNotEmpty()
//                            ) true
//                            else if (selectedDatabase == "sqlite" && sqliteInput.pathToDb.isNotEmpty()
//                                && sqliteInput.name.isNotEmpty()
//                            ) true
//                            else false
//                        }
//                    ) {
//                        Text("Load")
//                    }
//                },
//                dismissButton = {
//                    Button(onClick = { showDialog = false }) {
//                        Text("Cancel")
//                    }
//                }
//            )
//        }
//
//        if (loadGraph) {
//            if (selectedDatabase == "neo4j") {
//                message = drawGraph(viewModel, neo4jInput)
//            }
//            else if (selectedDatabase == "sqlite") {
//                message = paintGraph(viewModel, sqliteInput)
//            }
//            else {
//                TODO()
//            }
//        }
//
//        scope.launch {
//            if (showSnackbar) {
//                snackbarHostState.showSnackbar(
//                    message,
//                    "Dismiss",
//                    duration = SnackbarDuration.Short
//                )
//                showSnackbar = false
//            }
//        }
//    }
//}
