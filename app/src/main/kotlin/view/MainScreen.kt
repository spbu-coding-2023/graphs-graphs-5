package view

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
//import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import viewmodel.DGScreenViewModel
import viewmodel.MainScreenViewModel
import viewmodel.UGScreenViewModel

@Composable
fun <V> DGMainScreen(viewModel: DGScreenViewModel<V>, theme: MutableState<Theme>) {
    Material3AppTheme(theme = theme.value) {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        var menuInputState by remember { mutableStateOf(MenuInput()) }
        //var neo4jInputState by remember { mutableStateOf(Neo4jInput())}
        var message by remember { mutableStateOf("") }

        var isGraphLoaded by remember { mutableStateOf(false) }
        var showDialog by remember { mutableStateOf(false) }

        var showSnackbar by remember { mutableStateOf(false) }


        Scaffold(
            backgroundColor = MaterialTheme.colorScheme.surface,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
//            modifier = Modifier.padding(16.dp)
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
                        onClick = { showDialog = true },
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
                                message = "no graph provided, please load your graph"

                            }
                        },
                        //enabled = isGraphLoaded,
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
                                //message = viewModel.run(menuInputState.algoNum)
                                when (menuInputState.text) {
                                    "Cycles" -> {
                                        if (menuInputState.inputValueOneVertex != "") {
                                            message = viewModel.run(menuInputState)
                                            showSnackbar = message.isNotEmpty()
                                        } else {
                                            showSnackbar = true
                                            message = "Error: no required parameter for chosen algo was passed. Please enter parameter"
                                        }
                                    }
                                    "Min path (Dijkstra)", "Min path (Ford-Bellman)" -> {
                                        if (menuInputState.inputStartTwoVer != "" && menuInputState.inputEndTwoVer != "") {
                                            message = viewModel.run(menuInputState)
                                            showSnackbar = message.isNotEmpty()
                                        } else {
                                            showSnackbar = true
                                            message = "Error: no required parameter for chosen algo was passed. Please enter parameter"
                                        }
                                    }
                                    else -> message = viewModel.run(menuInputState)
                                }
                            },
                            enabled = true,
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
                    //сюда вставить zoomable
                    Surface(
                        modifier = Modifier.weight(1f)
                    ) {
                        DirectedGraphView(viewModel.graphViewModel)
                    }
                }
                if (message.isNotEmpty()) {
                    showSnackbar = true
                }
            }
        }

        var selectedDatabase by remember { mutableStateOf("") }
        var neo4jInput by remember { mutableStateOf(Neo4jInput()) }
        var loadGraph by remember { mutableStateOf(false)}

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Load Graph") },
                text = {
                    Column {
                        Text(text = "Select Database:")

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedDatabase == "neo4j",
                                onClick = { selectedDatabase = "neo4j" }
                            )
                            Text(text = "neo4j")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedDatabase == "sqlite",
                                onClick = { selectedDatabase = "sqlite" }
                            )
                            Text(text = "sqlite")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedDatabase == ".csv",
                                onClick = { selectedDatabase = ".csv" }
                            )
                            Text(text = ".csv file")
                        }

                        if (selectedDatabase == "neo4j") {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "Enter Neo4j Details:")
                            Spacer(modifier = Modifier.height(8.dp))

                            // Text fields for URI, login, password
                            OutlinedTextField(
                                value = neo4jInput.uri,
                                onValueChange = {newURI ->
                                    neo4jInput = neo4jInput.copy(uri = newURI)
                                },
                                label = { Text("URI") }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = neo4jInput.login,
                                onValueChange = {newLogin ->
                                    neo4jInput = neo4jInput.copy(login = newLogin)   },
                                label = { Text("Login") }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = neo4jInput.password,
                                onValueChange = {newPass ->
                                    neo4jInput = neo4jInput.copy(password = newPass) },
                                label = { Text("Password") }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Switcher for "is database updated"
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Switch(
                                    checked = neo4jInput.isUpdated,
                                    onCheckedChange = {newState ->
                                        neo4jInput = neo4jInput.copy(isUpdated = newState)},
                                )
                                Text(
                                    text = "Is database updated? (no/yes) If no, results of algorithms from previous runs can be displayed",
                                    modifier = Modifier.padding(16.dp)
                                    )
                            }

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Switch(
                                    checked = neo4jInput.isUndirected,
                                    onCheckedChange = {newState ->
                                        neo4jInput = neo4jInput.copy(isUndirected = newState)},
                                )
                                Text(
                                    text = "Is graph undirected? (no/yes)",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        if (selectedDatabase == "sqlite") {
                            TODO()
                        }
                    }
                },
                //переместить эту кнопку внутрь конкретной дата базы
                confirmButton = {
                    Button(
                        onClick = {
                            isGraphLoaded = true
                            showDialog = false
                            loadGraph = true
                        }
                    ) {
                        Text("Load")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (loadGraph) {
            if (selectedDatabase == "neo4j") {
                message = drawGraph(viewModel, neo4jInput)
            }
            else if (selectedDatabase == "sqlite") {
                TODO()
            }
            else {
                TODO()
            }
        }

        scope.launch {
            if (showSnackbar) {
                snackbarHostState.showSnackbar(
                    message,
                    "Dismiss",
                    duration = SnackbarDuration.Short
                )
                showSnackbar = false
            }
        }
    }
}

@Composable
fun <V> UGMainScreen(viewModel: UGScreenViewModel<V>, theme: MutableState<Theme>) {
    Material3AppTheme(theme = theme.value) {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        var menuInputState by remember { mutableStateOf(MenuInput()) }
        var message by remember { mutableStateOf("") }

        var isGraphLoaded by remember { mutableStateOf(false) }
        var showDialog by remember { mutableStateOf(false) }

        var showSnackbar by remember { mutableStateOf(false) }

        Scaffold(
            backgroundColor = MaterialTheme.colorScheme.surface,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
//            modifier = Modifier.padding(16.dp)
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
                        onClick = { showDialog = true },
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
                                message = "no graph provided, please load your graph"

                            }
                        },
                        //enabled = isGraphLoaded,
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
                Row(
                ) {
                    Column(modifier = Modifier.width(300.dp)) {
                        Spacer(modifier = Modifier.padding(8.dp))
                        showVerticesLabels(viewModel)
                        showEdgesLabels(viewModel)
                        resetGraphView(viewModel)
                        Button(
                            onClick = {
                                when (menuInputState.text) {
                                    "Cycles" -> {
                                        if (menuInputState.inputValueOneVertex != "") {
                                            message = viewModel.run(menuInputState)
                                            showSnackbar = message.isNotEmpty()
                                        } else {
                                            showSnackbar = true
                                            message =
                                                "Error: no required parameter for chosen algo was passed. Please enter parameter"
                                        }
                                    }
                                    //add another types
                                    else -> message = viewModel.run(menuInputState)


                                }
                                if (message.isNotEmpty()) {
                                    showSnackbar = true
                                }
                            },
                            enabled = true,
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
//                            algoNum = newState.algoNum,
                            text = newState.text,
                            inputValueOneVertex = newState.inputValueOneVertex,
                            inputStartTwoVer = newState.inputStartTwoVer,
                            inputEndTwoVer = newState.inputEndTwoVer
                        )
                    }
                    Surface(
                        modifier = Modifier.weight(1f),
                    ) {
                        UndirectedGraphView(viewModel.graphViewModel)
                    }
                }
            }
        }
        var selectedDatabase by remember { mutableStateOf("") }
        var neo4jInput by remember { mutableStateOf(Neo4jInput()) }
        var loadGraph by remember { mutableStateOf(false)}

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Load Graph") },
                text = {
                    Column {
                        Text(text = "Select Database:")

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedDatabase == "neo4j",
                                onClick = { selectedDatabase = "neo4j" }
                            )
                            Text(text = "neo4j")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedDatabase == "sqlite",
                                onClick = { selectedDatabase = "sqlite" }
                            )
                            Text(text = "sqlite")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedDatabase == ".csv",
                                onClick = { selectedDatabase = ".csv" }
                            )
                            Text(text = ".csv file")
                        }

                        if (selectedDatabase == "neo4j") {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "Enter Neo4j Details:")
                            Spacer(modifier = Modifier.height(8.dp))

                            // Text fields for URI, login, password
                            OutlinedTextField(
                                value = neo4jInput.uri,
                                onValueChange = {newURI ->
                                    neo4jInput = neo4jInput.copy(uri = newURI)
                                },
                                label = { Text("URI") }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = neo4jInput.login,
                                onValueChange = {newLogin ->
                                    neo4jInput = neo4jInput.copy(login = newLogin)   },
                                label = { Text("Login") }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = neo4jInput.password,
                                onValueChange = {newPass ->
                                    neo4jInput = neo4jInput.copy(password = newPass) },
                                label = { Text("Password") }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Switcher for "is database updated"
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Switch(
                                    checked = neo4jInput.isUpdated,
                                    onCheckedChange = {newState ->
                                        neo4jInput = neo4jInput.copy(isUpdated = newState)},
                                )
                                Text(
                                    text = "Is database updated? (no/yes) If no, results of algorithms from previous runs can be displayed",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Switch(
                                    checked = neo4jInput.isUndirected,
                                    onCheckedChange = {newState ->
                                        neo4jInput = neo4jInput.copy(isUndirected = newState)},
                                )
                                Text(
                                    text = "Is graph undirected? (no/yes)",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        if (selectedDatabase == "sqlite") {
                            TODO()
                        }
                    }
                },
                //переместить эту кнопку внутрь конкретной дата базы
                confirmButton = {
                    Button(
                        onClick = {
                            isGraphLoaded = true
                            showDialog = false
                            loadGraph = true
                        }
                    ) {
                        Text("Load")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (loadGraph) {
            if (selectedDatabase == "neo4j") {
                message = drawGraph(viewModel, neo4jInput)
            }
            else if (selectedDatabase == "sqlite") {
                TODO()
            }
            else {
                TODO()
            }
        }

        scope.launch {
            if (showSnackbar) {
                snackbarHostState.showSnackbar(
                    message,
                    "Dismiss",
                    duration = SnackbarDuration.Short
                )
                showSnackbar = false
            }
        }
    }
}

@Composable
fun <V> drawGraph(viewModel: MainScreenViewModel<V>, input: Neo4jInput): String {
    val (graph, message) = viewModel.configureNeo4jRepo(input)
    if (message != "") {
        return message
    }
    else if (graph != null){
        ScreenFactory.createView(graph)
    }
    return ""
}

@Composable
fun <V> showVerticesLabels(viewModel: MainScreenViewModel<V>) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = viewModel.showVerticesLabels.value,
            onCheckedChange = { viewModel.showVerticesLabels.value = it },
            colors = CheckboxDefaults.colors(
                checkmarkColor = MaterialTheme.colorScheme.onSecondary,
                checkedColor = MaterialTheme.colorScheme.secondary,
            )
        )
        Text(
            text = "Show vertices' labels",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun <V> showEdgesLabels(viewModel: MainScreenViewModel<V>) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = viewModel.showEdgesLabels.value,
            onCheckedChange = { viewModel.showEdgesLabels.value = it },
            colors = CheckboxDefaults.colors(
                checkmarkColor = MaterialTheme.colorScheme.onSecondary,
                checkedColor = MaterialTheme.colorScheme.secondary,
            )
        )
        Text(
            text = "Show edges' weights",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun <V> resetGraphView(viewModel: MainScreenViewModel<V>) {
    Button(
        onClick = viewModel::resetGraphView,
        enabled = true,
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = "Reset default settings",
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun menu(algoList: List<String>): MenuInput {

    var showOneVertexSelection by remember { mutableStateOf(false) }
    var showTwoVertexSelection by remember { mutableStateOf(false) }

    var showNoInputError by remember { mutableStateOf(false) }
    var showIncorrectInputError by remember { mutableStateOf(false) }

    var menuInputState by remember { mutableStateOf(MenuInput()) }

    /* by remember : if the variable changes, the parts of code where it's used change view accordingly */
    var selectedText by remember {
        mutableStateOf("Pick an algo")
    }
    var isExpanded by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        androidx.compose.material3.ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                algoList.forEachIndexed { index, text ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = {
                            Text(
                                text = text,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
//                            menuInputState.algoNum = index
                            menuInputState.text = algoList[index]
                            showOneVertexSelection = menuInputState.text == "Cycles"
                            showTwoVertexSelection = menuInputState.text == "Min path (Dijkstra)" ||
                                    menuInputState.text == "Min path (Ford-Bellman)"
                            selectedText = algoList[index]
                            isExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
                Divider(
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = when {
                        (selectedText == "Pick an algo") -> "Currently selected: none"
                        else -> "Currently selected: $selectedText"
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        if (showOneVertexSelection) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Card(
                    elevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "Insert vertex index")
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = menuInputState.inputValueOneVertex,
                            onValueChange = { newValue ->
                                menuInputState = menuInputState.copy(inputValueOneVertex = newValue)
                                showNoInputError = false
                                showIncorrectInputError = false
                            },
                            label = {
                                Text(
                                    text = "Index",
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            },
                            isError = showNoInputError || showIncorrectInputError,
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = MaterialTheme.colorScheme.surface,
                                cursorColor = MaterialTheme.colorScheme.onSecondary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                        if (showNoInputError) {
                            Text(
                                text = "Error: no input passed. Please check that input values are integer",
                                color = MaterialTheme.colorScheme.errorContainer
                            )
                        }
                        if (showIncorrectInputError) {
                            Text(
                                text = "Error: invalid input passed. Please check that input values are integer",
                                color = MaterialTheme.colorScheme.errorContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    if (menuInputState.inputValueOneVertex.all { it.isDigit() } && menuInputState.inputValueOneVertex.isNotEmpty()) {
                                        // Действие при нажатии на кнопку подтверждения
                                        showOneVertexSelection = false
                                    } else if (menuInputState.inputValueOneVertex.isEmpty()) {
                                        showNoInputError = true // Показать сообщение об ошибке
                                    }
                                    else {
                                        showIncorrectInputError = true
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text(
                                    text = "Select",
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    showOneVertexSelection = false
                                    showIncorrectInputError = false
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text(
                                    text = "Escape",
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showTwoVertexSelection) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Card(
                    elevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "Insert start and end vertex indices",
                            color = MaterialTheme.colorScheme.onSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = menuInputState.inputStartTwoVer,
                            onValueChange = { newValue ->
                                menuInputState = menuInputState.copy(inputStartTwoVer = newValue)
                                showNoInputError = false // Скрыть сообщение об ошибке при изменении текста
                            },
                            label = {
                                Text(
                                    text = "Start index",
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            },
                            isError = showNoInputError,
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = MaterialTheme.colorScheme.surface,
                                cursorColor = MaterialTheme.colorScheme.onSecondary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = menuInputState.inputEndTwoVer,
                            onValueChange = { newValue ->
                                menuInputState = menuInputState.copy(inputEndTwoVer = newValue)
                                showNoInputError = false // Скрыть сообщение об ошибке при изменении текста
                                showIncorrectInputError = false
                            },
                            label = {
                                Text(
                                    text = "End index",
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            },
                            isError = showNoInputError || showIncorrectInputError,
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = MaterialTheme.colorScheme.surface,
                                cursorColor = MaterialTheme.colorScheme.onSecondary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                        if (showNoInputError) {
                            Text(
                                text = "Error: no input passed. Please check that input values are integer",
                                color = MaterialTheme.colorScheme.errorContainer
                            )
                        }
                        if (showIncorrectInputError) {
                            Text(
                                text = "Error: invalid input passed. Please check that input values are integer",
                                color = MaterialTheme.colorScheme.errorContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    if (menuInputState.inputStartTwoVer.all { it.isDigit() }
                                        && menuInputState.inputEndTwoVer.all { it.isDigit() }
                                        && menuInputState.inputStartTwoVer.isNotEmpty()
                                        && menuInputState.inputEndTwoVer.isNotEmpty()) {
                                        // Действие при нажатии на кнопку подтверждения
                                        showTwoVertexSelection = false
                                    } else if (menuInputState.inputStartTwoVer.isEmpty()
                                        || menuInputState.inputEndTwoVer.isEmpty()){
                                        showNoInputError = true // Показать сообщение об ошибке
                                    } else {
                                        showIncorrectInputError = true
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text(
                                    text = "Select",
                                    color = MaterialTheme.colorScheme.onSecondary
                                    )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    showTwoVertexSelection = false
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text(
                                    text = "Escape",
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    return menuInputState
}

@Composable
fun ThemeSwitcher(
    theme: MutableState<Theme>,
    size: Dp = 150.dp,
    iconSize: Dp = size / 3,
    padding: Dp = 10.dp,
    borderWidth: Dp = 2.dp,
    parentShape: Shape = CircleShape,
    toggleShape: Shape = CircleShape,
    animationSpec: AnimationSpec<Dp> = tween(durationMillis = 300)
) {
    val offset by animateDpAsState(
        targetValue = if (theme.value == Theme.SPECIAL) 0.dp else size,
        animationSpec = animationSpec
    )
    Box(modifier = Modifier
        .width(size * 2)
        .height(size)
        .clip(shape = parentShape)
        .clickable(enabled = true) {
            if (theme.value == Theme.SPECIAL) {
                theme.value = Theme.CLASSIC
            } else {
                theme.value = Theme.SPECIAL
            }
        }
        .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .offset(x = offset)
                .padding(all = padding)
                .clip(shape = toggleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {}
        Row(
            modifier = Modifier
                .border(
                    border = BorderStroke(
                        width = borderWidth,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    shape = parentShape
                )
        ) {
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.HeartBroken,
                    contentDescription = null,
                    tint = if (theme.value == Theme.SPECIAL) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.primary
                )
            }
            Box(
                modifier = Modifier.size(size),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = if (theme.value == Theme.SPECIAL) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    }
}