package view

import ScreenFactory
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
//import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import menu
import model.GraphType
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
        //var neo4jInputState by remember { mutableStateOf(Neo4jInput())}
        var message by remember { mutableStateOf("") }

        var showSnackbar by remember { mutableStateOf(false) }

        var isGraphLoaded by remember { mutableStateOf(false) }
        var showDBSelectionDialog by remember { mutableStateOf(false) }
        var selectedDatabase by remember { mutableStateOf("") }
        var neo4jInput by remember { mutableStateOf(Neo4jInput()) }

        var dBInput by remember { mutableStateOf(DBInput()) }
        var loadGraph by remember { mutableStateOf(false)}

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
                        onClick = { showDBSelectionDialog = true },
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
                    val graphLoadingState = { mutableStateOf(isGraphLoaded) }
                    Button(
                        onClick = {
                            //println(isGraphLoaded)
                            if (graphLoadingState.equals(false)) {
                                message = "no graph provided, please load your graph"
                                showSnackbar = true

                            }
                            else {
                                //implement other bases
                                message = viewModel.saveAlgoResults()
                            }

                        },
                        //enabled = isGraphLoaded,
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = if (graphLoadingState.equals(false)) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background
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
                                println(dBInput)
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
                                if (message.isNotEmpty()) {
                                    showSnackbar = true
                                }
                                scope.launch {
                                    if (showSnackbar && (message != "")) {
                                        snackbarHostState.showSnackbar(
                                            message,
                                            "Dismiss",
                                            duration = SnackbarDuration.Short
                                        )
                                        showSnackbar = false
                                    }
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
                    var scale by viewModel.scale
                    fun adjustScale(scrollAmount: Int) {
                        scale = (scale * exp(scrollAmount * 0.1f)).coerceIn(0.05f, 4.0f)
                    }
                    var offset by viewModel.offset
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .onPointerEvent(PointerEventType.Scroll) {
                                val change = it.changes.first()
                                val scrollAmount = change.scrollDelta.y.toInt().sign
                                adjustScale(scrollAmount)
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
            //мб сдвинуть вниз
            if (showDBSelectionDialog) {
                AlertDialog(
                    onDismissRequest = { showDBSelectionDialog = false },
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
                                dBInput.dBType = "neo4j"
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(text = "Enter Neo4j Details:")
                                Spacer(modifier = Modifier.height(8.dp))

                                // Text fields for URI, login, password
                                OutlinedTextField(
                                    value = dBInput.uri,
                                    onValueChange = {newURI ->
                                        dBInput = dBInput.copy(uri = newURI)
                                    },
                                    label = { Text("URI") }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = dBInput.login,
                                    onValueChange = {newLogin ->
                                        dBInput = dBInput.copy(login = newLogin)   },
                                    label = { Text("Login") }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = dBInput.password,
                                    onValueChange = {newPass ->
                                        dBInput = dBInput.copy(password = newPass) },
                                    label = { Text("Password") }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Switcher for "is database updated"
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Switch(
                                        checked = dBInput.isUpdatedNeo4j,
                                        onCheckedChange = {newState ->
                                            dBInput = dBInput.copy(isUpdatedNeo4j = newState)},
                                    )
                                    Text(
                                        text = "Is database updated? (no/yes) If no, results of algorithms from previous runs can be displayed",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Switch(
                                        checked = dBInput.isUndirected,
                                        onCheckedChange = {newState ->
                                            dBInput = dBInput.copy(isUndirected = newState)},
                                    )
                                    Text(
                                        text = "Is graph undirected? (no/yes)",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                            if (selectedDatabase == "sqlite") {
                                dBInput.dBType = "sqlite"
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(text = "Enter SQLite Details:")
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = dBInput.pathToDb,
                                    onValueChange = { newPathToDb ->
                                        dBInput = dBInput.copy(pathToDb = newPathToDb)
                                    },
                                    label = { Text("Path to database") }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = dBInput.name,
                                    onValueChange = { newName ->
                                        dBInput = dBInput.copy(name = newName)
                                    },
                                    label = { Text("Name") }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Switch(
                                        checked = dBInput.isUpdatedSql,
                                        onCheckedChange = { newState ->
                                            dBInput = dBInput.copy(isUpdatedSql = newState)
                                        },
                                    )
                                    Text(
                                        text = "Is database updated? (no/yes) If no, results of algorithms from previous runs can be displayed",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    },
                    //переместить эту кнопку внутрь конкретной дата базы
                    confirmButton = {
                        Button(
                            onClick = {
                                //isGraphLoaded = true
                                showDBSelectionDialog = false
                                loadGraph = if (dBInput.dBType == "sqlite" && (dBInput.name.isEmpty() || dBInput.pathToDb.isEmpty())) false
                                else true
                                println(loadGraph)
                                if (!loadGraph) message = "Error: no data input"
                            }
                        ) {
                            Text("Load")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDBSelectionDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (loadGraph) {
                message = drawGraph(viewModel, dBInput)
                //showSnackbar = message.isNotEmpty()
                if (message.isNotEmpty()) {
                    showSnackbar = true
                }
                else {
                    isGraphLoaded = true
//                    println("here")
                }

                scope.launch {
                    if ((showSnackbar) && (message != "")) {
                        snackbarHostState.showSnackbar(
                            message,
                            "Dismiss",
                            duration = SnackbarDuration.Short
                        )
                        showSnackbar = false
                        //message = ""
                    }
                }
            }
        }
    }
}



//@Composable
//fun <V> paintGraph(viewModel: MainScreenViewModel<V>, input: SQLiteInput): String {
//    val (graph, message) = viewModel.configureSQLiteRepo(input)
//    if (message.isNotEmpty()) {
//        return message
//    }
//    else if (graph != null){
//        ScreenFactory.createView(graph)
//    }
//    return ""
//}

@Composable
fun <V> drawGraph(viewModel: MainScreenViewModel<V>, input: DBInput): String {
    println(input.pathToDb)
    println(input.name)
    println(input.dBType)
    when (input.dBType) {
        "neo4j" -> {
            val (graph, message) = viewModel.configureNeo4jRepo(input)
            if (message.isNotEmpty()) {
                return message
            }
            else if (graph != null){
                ScreenFactory.createView(graph, input)
            }

        }
        "sqlite" -> {
            val (graph, message) = viewModel.configureSQLiteRepo(input)
            if (message.isNotEmpty()) {
                return message
            }
            else if (graph != null){
                ScreenFactory.createView(graph, input)
            }
        }
        else -> {}
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



//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun menu(algoList: List<String>): MenuInput {
//
//    var showOneVertexSelection by remember { mutableStateOf(false) }
//    var showTwoVertexSelection by remember { mutableStateOf(false) }
//
//    var showNoInputError by remember { mutableStateOf(false) }
//    var showIncorrectInputError by remember { mutableStateOf(false) }
//
//    var menuInputState by remember { mutableStateOf(MenuInput()) }
//
//    /* by remember : if the variable changes, the parts of code where it's used change view accordingly */
//    var selectedText by remember {
//        mutableStateOf("Pick an algo")
//    }
//    var isExpanded by remember {
//        mutableStateOf(false)
//    }
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 4.dp),
//        horizontalAlignment = Alignment.Start
//    ) {
//        androidx.compose.material3.ExposedDropdownMenuBox(
//            expanded = isExpanded,
//            onExpandedChange = { isExpanded = !isExpanded },
//            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
//        ) {
//            OutlinedTextField(
//                value = selectedText,
//                onValueChange = {},
//                readOnly = true,
//                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
//                colors = TextFieldDefaults.outlinedTextFieldColors(
//                    textColor = MaterialTheme.colorScheme.onSurface,
//                    focusedBorderColor = MaterialTheme.colorScheme.outline,
//                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
//                ),
//                modifier = Modifier.menuAnchor()
//            )
//            ExposedDropdownMenu(
//                expanded = isExpanded,
//                onDismissRequest = { isExpanded = false }
//            ) {
//                algoList.forEachIndexed { index, text ->
//                    androidx.compose.material3.DropdownMenuItem(
//                        text = {
//                            Text(
//                                text = text,
//                                color = MaterialTheme.colorScheme.onSurface,
//                                style = MaterialTheme.typography.bodyMedium
//                            )
//                        },
//                        onClick = {
////                            menuInputState.algoNum = index
//                            menuInputState.text = algoList[index]
//                            showOneVertexSelection = menuInputState.text == "Cycles"
//                            showTwoVertexSelection = menuInputState.text == "Min path (Dijkstra)" ||
//                                    menuInputState.text == "Min path (Ford-Bellman)"
//                            selectedText = algoList[index]
//                            isExpanded = false
//                        },
//                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
//                    )
//                }
//                Divider(
//                    color = MaterialTheme.colorScheme.outline
//                )
//                Text(
//                    modifier = Modifier.padding(8.dp),
//                    text = when {
//                        (selectedText == "Pick an algo") -> "Currently selected: none"
//                        else -> "Currently selected: $selectedText"
//                    },
//                    color = MaterialTheme.colorScheme.onSurface,
//                    style = MaterialTheme.typography.bodySmall
//                )
//            }
//        }
//        if (showOneVertexSelection) {
//            Box(
//                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
//            ) {
//                Card(
//                    elevation = 8.dp,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        Text(text = "Insert vertex index")
//                        Spacer(modifier = Modifier.height(8.dp))
//                        TextField(
//                            value = menuInputState.inputValueOneVertex,
//                            onValueChange = { newValue ->
//                                menuInputState = menuInputState.copy(inputValueOneVertex = newValue)
//                                showNoInputError = false
//                                showIncorrectInputError = false
//                            },
//                            label = {
//                                Text(
//                                    text = "Index",
//                                    color = MaterialTheme.colorScheme.onSecondary
//                                )
//                            },
//                            isError = showNoInputError || showIncorrectInputError,
//                            colors = TextFieldDefaults.textFieldColors(
//                                backgroundColor = MaterialTheme.colorScheme.surface,
//                                cursorColor = MaterialTheme.colorScheme.onSecondary,
//                                focusedIndicatorColor = MaterialTheme.colorScheme.onSecondary
//                            )
//                        )
//                        if (showNoInputError) {
//                            Text(
//                                text = "Error: no input passed. Please check that input values are integer",
//                                color = MaterialTheme.colorScheme.errorContainer
//                            )
//                        }
//                        if (showIncorrectInputError) {
//                            Text(
//                                text = "Error: invalid input passed. Please check that input values are integer",
//                                color = MaterialTheme.colorScheme.errorContainer
//                            )
//                        }
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Row(
//                            horizontalArrangement = Arrangement.End
//                        ) {
//                            Button(
//                                onClick = {
//                                    if (menuInputState.inputValueOneVertex.all { it.isDigit() } && menuInputState.inputValueOneVertex.isNotEmpty()) {
//                                        // Действие при нажатии на кнопку подтверждения
//                                        showOneVertexSelection = false
//                                    } else if (menuInputState.inputValueOneVertex.isEmpty()) {
//                                        showNoInputError = true // Показать сообщение об ошибке
//                                    }
//                                    else {
//                                        showIncorrectInputError = true
//                                    }
//                                },
//                                colors = ButtonDefaults.outlinedButtonColors(
//                                    backgroundColor = MaterialTheme.colorScheme.secondary
//                                )
//                            ) {
//                                Text(
//                                    text = "Select",
//                                    color = MaterialTheme.colorScheme.onSecondary
//                                )
//                            }
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Button(
//                                onClick = {
//                                    showOneVertexSelection = false
//                                    showIncorrectInputError = false
//                                },
//                                colors = ButtonDefaults.outlinedButtonColors(
//                                    backgroundColor = MaterialTheme.colorScheme.secondary
//                                )
//                            ) {
//                                Text(
//                                    text = "Escape",
//                                    color = MaterialTheme.colorScheme.onSecondary
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        if (showTwoVertexSelection) {
//            Box(
//                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
//            ) {
//                Card(
//                    elevation = 8.dp,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        Text(text = "Insert start and end vertex indices",
//                            color = MaterialTheme.colorScheme.onSecondary)
//                        Spacer(modifier = Modifier.height(8.dp))
//                        TextField(
//                            value = menuInputState.inputStartTwoVer,
//                            onValueChange = { newValue ->
//                                menuInputState = menuInputState.copy(inputStartTwoVer = newValue)
//                                showNoInputError = false // Скрыть сообщение об ошибке при изменении текста
//                            },
//                            label = {
//                                Text(
//                                    text = "Start index",
//                                    color = MaterialTheme.colorScheme.onSecondary
//                                )
//                            },
//                            isError = showNoInputError,
//                            colors = TextFieldDefaults.textFieldColors(
//                                backgroundColor = MaterialTheme.colorScheme.surface,
//                                cursorColor = MaterialTheme.colorScheme.onSecondary,
//                                focusedIndicatorColor = MaterialTheme.colorScheme.onSecondary
//                            )
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        TextField(
//                            value = menuInputState.inputEndTwoVer,
//                            onValueChange = { newValue ->
//                                menuInputState = menuInputState.copy(inputEndTwoVer = newValue)
//                                showNoInputError = false // Скрыть сообщение об ошибке при изменении текста
//                                showIncorrectInputError = false
//                            },
//                            label = {
//                                Text(
//                                    text = "End index",
//                                    color = MaterialTheme.colorScheme.onSecondary
//                                )
//                            },
//                            isError = showNoInputError || showIncorrectInputError,
//                            colors = TextFieldDefaults.textFieldColors(
//                                backgroundColor = MaterialTheme.colorScheme.surface,
//                                cursorColor = MaterialTheme.colorScheme.onSecondary,
//                                focusedIndicatorColor = MaterialTheme.colorScheme.onSecondary
//                            )
//                        )
//                        if (showNoInputError) {
//                            Text(
//                                text = "Error: no input passed. Please check that input values are integer",
//                                color = MaterialTheme.colorScheme.errorContainer
//                            )
//                        }
//                        if (showIncorrectInputError) {
//                            Text(
//                                text = "Error: invalid input passed. Please check that input values are integer",
//                                color = MaterialTheme.colorScheme.errorContainer
//                            )
//                        }
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Row(
//                            horizontalArrangement = Arrangement.End
//                        ) {
//                            Button(
//                                onClick = {
//                                    if (menuInputState.inputStartTwoVer.all { it.isDigit() }
//                                        && menuInputState.inputEndTwoVer.all { it.isDigit() }
//                                        && menuInputState.inputStartTwoVer.isNotEmpty()
//                                        && menuInputState.inputEndTwoVer.isNotEmpty()) {
//                                        // Действие при нажатии на кнопку подтверждения
//                                        showTwoVertexSelection = false
//                                    } else if (menuInputState.inputStartTwoVer.isEmpty()
//                                        || menuInputState.inputEndTwoVer.isEmpty()){
//                                        showNoInputError = true // Показать сообщение об ошибке
//                                    } else {
//                                        showIncorrectInputError = true
//                                    }
//                                },
//                                colors = ButtonDefaults.outlinedButtonColors(
//                                    backgroundColor = MaterialTheme.colorScheme.secondary
//                                )
//                            ) {
//                                Text(
//                                    text = "Select",
//                                    color = MaterialTheme.colorScheme.onSecondary
//                                    )
//                            }
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Button(
//                                onClick = {
//                                    showTwoVertexSelection = false
//                                },
//                                colors = ButtonDefaults.outlinedButtonColors(
//                                    backgroundColor = MaterialTheme.colorScheme.secondary
//                                )
//                            ) {
//                                Text(
//                                    text = "Escape",
//                                    color = MaterialTheme.colorScheme.onSecondary
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//    return menuInputState
//}
