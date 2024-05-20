package view

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import viewmodel.DGScreenViewModel
import viewmodel.MainScreenViewModel

//@Composable
//fun <V> MainScreenFactory(graphType: GraphType) {
//    when (graphType) {
//        GraphType.DIRECTED -> DGMainScreen(darkTheme = darkTheme,
//            onThemeUpdated = { darkTheme = !darkTheme },
//            DGScreenViewModel(graph, CircularPlacementStrategy())
//    }
//}

@Composable
fun <V> DGMainScreen(darkTheme: Boolean, onThemeUpdated: () -> Unit, viewModel: DGScreenViewModel<V>) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var menuInputState by remember { mutableStateOf(menuInput()) }

    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(
            hostState = snackbarHostState,
//            modifier = Modifier.padding(16.dp)
        ) { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                backgroundColor = MaterialTheme.colorScheme.error, // Background color of the Snackbar
                contentColor = MaterialTheme.colorScheme.onError, // Text color of the Snackbar
                actionColor = MaterialTheme.colorScheme.onError, // Action (button) text color
            )
        }
        }

    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(
                    text = "",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(16.dp))
                ThemeSwitcher(
                    darkTheme = darkTheme,
                    size = 45.dp,
                    padding = 5.dp,
                    onClick = onThemeUpdated
                )
            }
            Row {
                Column(modifier = Modifier.width(300.dp)) {
                    Spacer(modifier = Modifier.padding(8.dp))
                    showVerticesLabels(viewModel)
                    showEdgesLabels(viewModel)
                    resetGraphView(viewModel)

                    var showSnackbar by remember { mutableStateOf(false) }
                    var message by remember { mutableStateOf("") }
                    Button(
                        onClick = {
                            //message = viewModel.run(menuInputState.algoNum)
                            when (menuInputState.algoNum) {
                                2 -> {
                                    if (menuInputState.inputValueOneVertex != "") {
                                        message = viewModel.run(menuInputState.algoNum)
                                    }
                                    else {
                                        showSnackbar = true
                                    }
                                }
                                6, 7 -> {
                                    if (menuInputState.inputStartTwoVer != "" && menuInputState.inputEndTwoVer != "") {
                                        message = viewModel.run(menuInputState.algoNum)
                                    }
                                    else {
                                        showSnackbar = true
                                    }
                                }
                                else -> message = viewModel.run(menuInputState.algoNum)
                            }
                            scope.launch {
                                if (showSnackbar) {
                                    snackbarHostState.showSnackbar(
                                        "No required parameter for chosen algo was passed. Please enter parameter",
                                        "Dismiss",
                                        duration = SnackbarDuration.Short
                                    )
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
                    val newState = menu()
                    menuInputState = menuInputState.copy(algoNum = newState.algoNum,
                        inputValueOneVertex = newState.inputValueOneVertex,
                        inputStartTwoVer = newState.inputStartTwoVer,
                        inputEndTwoVer = newState.inputEndTwoVer
                    )
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    DirectedGraphView(viewModel.graphViewModel)
                }
            }
        }
    }
}

//@Composable
//fun <V> UGMainScreen(darkTheme: Boolean, onThemeUpdated: () -> Unit, viewModel: UGScreenViewModel<V>) {
//    val snackbarHostState = remember { SnackbarHostState() }
//    val scope = rememberCoroutineScope()
//    Scaffold(
//        /* add snackbar for some messages */
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize().padding(16.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth().height(50.dp)
//            ) {
//                Text(
//                    text = "",
//                    modifier = Modifier.weight(1f),
//                    color = MaterialTheme.colorScheme.onSurface,
//                    style = MaterialTheme.typography.bodyLarge
//                )
//                Spacer(modifier = Modifier.width(16.dp))
//                ThemeSwitcher(
//                    darkTheme = darkTheme,
//                    size = 45.dp,
//                    padding = 5.dp,
//                    onClick = onThemeUpdated
//                )
//            }
//            Row(
//            ) {
//                Column(modifier = Modifier.width(300.dp)) {
//                    Spacer(modifier = Modifier.padding(8.dp))
//                    showVerticesLabels(viewModel)
//                    showEdgesLabels(viewModel)
//                    resetGraphView(viewModel)
//                    var algoNum by remember { mutableStateOf(0)}
//                    var message by remember { mutableStateOf("") }
//                    Button(
//                        onClick = { message = viewModel.run(algoNum) },
//                        enabled = true,
//                        colors = ButtonDefaults.outlinedButtonColors(
//                            backgroundColor = MaterialTheme.colorScheme.secondary
//                        ),
//                        modifier = Modifier.padding(4.dp)
//                    ) {
//                        Text(
//                            text = "Run", color = MaterialTheme.colorScheme.onSecondary
//                        )
//                    }
//                    algoNum = menu()
//                }
//                Surface(
//                    modifier = Modifier.weight(1f),
//                    color = MaterialTheme.colorScheme.surface
//                ) {
//                    UndirectedGraphView(viewModel.graphViewModel)
//                }
//            }
//
//        }
//    }
//}
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
            fontSize = 20.sp,
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
            fontSize = 20.sp,
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
fun menu(): menuInput {

    var showOneVertexSelection by remember { mutableStateOf(false) }
    var showTwoVertexSelection by remember { mutableStateOf(false) }

    var showNoInputError by remember { mutableStateOf(false) }
    var showIncorrectInputError by remember { mutableStateOf(false) }

    var menuInputState by remember { mutableStateOf(menuInput()) }

    //var algoNum by remember { mutableStateOf(0)}
    val list = listOf(
        "Graph Clustering", "Key vertices", "Cycles", "Min tree", "Components",
        "Bridges", "Min path (Dijkstra)", "Min path (Ford-Bellman)"
    )
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
                list.forEachIndexed { index, text ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = {
                            Text(
                                text = text,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            menuInputState.algoNum = index
                            showOneVertexSelection = menuInputState.algoNum == 2
                            showTwoVertexSelection = menuInputState.algoNum == 6 || menuInputState.algoNum == 7
                            selectedText = list[index]
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
                            label = { Text("index") },
                            isError = showNoInputError || showIncorrectInputError
                        )
                        if (showNoInputError) {
                            Text(
                                text = "No input passed. Please check that input values are integer",
                            )
                        }
                        if (showIncorrectInputError) {
                            Text(
                                text = "Invalid input passed. Please check that input values are integer",
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
                                }
                            ) {
                                Text("Select")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    showOneVertexSelection = false
                                    showIncorrectInputError = false
                                }
                            ) {
                                Text("Escape")
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
                        Text(text = "Insert start and end vertex indices")
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = menuInputState.inputStartTwoVer,
                            onValueChange = { newValue ->
                                menuInputState = menuInputState.copy(inputStartTwoVer = newValue)
                                showNoInputError = false // Скрыть сообщение об ошибке при изменении текста
                            },
                            label = { Text("Start index") },
                            isError = showNoInputError
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = menuInputState.inputEndTwoVer,
                            onValueChange = { newValue ->
                                menuInputState = menuInputState.copy(inputEndTwoVer = newValue)
                                showNoInputError = false // Скрыть сообщение об ошибке при изменении текста
                                showIncorrectInputError = false
                            },
                            label = { Text("End index") },
                            isError = showNoInputError || showIncorrectInputError
                        )
                        if (showNoInputError) {
                            Text(
                                text = "No input passed. Please check that input values are integer",
                            )
                        }
                        if (showIncorrectInputError) {
                            Text(
                                text = "Invalid input passed. Please check that input values are integer",
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
                                }
                            ) {
                                Text("Select")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    showTwoVertexSelection = false
                                }
                            ) {
                                Text("Escape")
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
    darkTheme: Boolean = false,
    size: Dp = 150.dp,
    iconSize: Dp = size / 3,
    padding: Dp = 10.dp,
    borderWidth: Dp = 2.dp,
    parentShape: Shape = CircleShape,
    toggleShape: Shape = CircleShape,
    animationSpec: AnimationSpec<Dp> = tween(durationMillis = 300),
    onClick: () -> Unit
) {
    val offset by animateDpAsState(
        targetValue = if (darkTheme) 0.dp else size,
        animationSpec = animationSpec
    )
    Box(modifier = Modifier
        .width(size * 2)
        .height(size)
        .clip(shape = parentShape)
        .clickable { onClick() }
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
                    tint = if (darkTheme) MaterialTheme.colorScheme.secondaryContainer
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
                    tint = if (darkTheme) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    }
}
