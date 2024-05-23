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
        var message by remember { mutableStateOf("") }
        Scaffold(
            backgroundColor = MaterialTheme.colorScheme.surface,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
//            modifier = Modifier.padding(16.dp)
                ) { snackbarData ->
                    val snackbarBackgroundColor = if (message.contains("No cycles") || message.contains("unattainable")) {
                        MaterialTheme.colorScheme.background
                    } else {
                        MaterialTheme.colorScheme.error
                    }

                    val snackbarContentColor = if (message.contains("No cycles")) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onError
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
                        var showSnackbar by remember { mutableStateOf(false) }
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
                                            message = "No required parameter for chosen algo was passed. Please enter parameter"
                                        }
                                    }
                                    "Min path (Dijkstra)", "Min path (Ford-Bellman)" -> {
                                        if (menuInputState.inputStartTwoVer != "" && menuInputState.inputEndTwoVer != "") {
                                            message = viewModel.run(menuInputState)
                                            showSnackbar = message.isNotEmpty()
                                        } else {
                                            showSnackbar = true
                                            message = "No required parameter for chosen algo was passed. Please enter parameter"
                                        }
                                    }
                                    else -> message = viewModel.run(menuInputState)
                                }
                                if (message.isNotEmpty()) {
                                    showSnackbar = true
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
//                    Surface(
//                        modifier = Modifier.weight(1f)
//                            .graphicsLayer(
//                                scaleX = viewModel.scale,
//                                scaleY = viewModel.scale,
//                                translationX = viewModel.offsetX,
//                                translationY = viewModel.offsetY
//                            )
//                            .pointerInput(Unit) {
//                                detectTransformGestures { _, pan, zoom, _ ->
//                                    viewModel.handleTransformGestures(pan, zoom)
//                                }
//                            }
//                    ) {
//                        Box(
//                            modifier = Modifier.fillMaxSize()
//                                .pointerInput(Unit) {
//                                    detectTransformGestures { _, pan, _, _ ->
//                                        viewModel.moveSurface(pan)
//                                    }
//                                }
//                        ) {
//                            DirectedGraphView(viewModel.graphViewModel)
//                        }
//                    }


//                    ZoomableSurface {
//                        DirectedGraphView(viewModel.graphViewModel)
//                    }
//                    var scale by remember { mutableStateOf(1f) }
//
//                    Surface(
//                        modifier = Modifier
//                            .weight(1f)
//                            .fillMaxSize()
//                            .padding(16.dp)
//                            .pointerInput(Unit) {
//                                detectTransformGestures { _, _, zoom, _ ->
//                                    scale *= zoom
//                                }
//                            }
//                            .graphicsLayer(
//                                scaleX = scale,
//                                scaleY = scale
//                            )
//                    ) {
//                        DirectedGraphView(viewModel.graphViewModel)
//                    }
                }
            }
        }
    }
}

//@Composable
//fun ZoomableSurface(content: @Composable () -> Unit) {
//    var scale by remember { mutableStateOf(1f) }
//    var offset by remember { mutableStateOf(Offset.Zero) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//            .fillMaxSize()
//            .pointerInput(Unit) {
//                detectTransformGestures { _, pan, zoom, _ ->
//                    scale *= zoom
//                    offset += pan / scale
//                }
//            }
//            .pointerInput(Unit) {
//                detectTapGestures { offset = Offset.Zero }
//            }
//    ) {
//        content()
//    }
//}

@Composable
fun <V> UGMainScreen(viewModel: UGScreenViewModel<V>, theme: MutableState<Theme>) {
    Material3AppTheme(theme = theme.value) {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        var menuInputState by remember { mutableStateOf(MenuInput()) }
        var message by remember { mutableStateOf("") }
        Scaffold(
            backgroundColor = MaterialTheme.colorScheme.surface,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
//            modifier = Modifier.padding(16.dp)
                ) { snackbarData ->
                    val snackbarBackgroundColor = if (message.contains("No cycles")) {
                        MaterialTheme.colorScheme.background
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                    val snackbarContentColor = if (message.contains("No cycles")) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onError
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
                        var showSnackbar by remember { mutableStateOf(false) }
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
                                                "No required parameter for chosen algo was passed. Please enter parameter"
                                        }
                                    }
                                    //add another types
                                    else -> message = viewModel.run(menuInputState)


                                }
                                if (message.isNotEmpty()) {
                                    showSnackbar = true
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
    }
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
                                text = "No input passed. Please check that input values are integer",
                                color = MaterialTheme.colorScheme.errorContainer
                            )
                        }
                        if (showIncorrectInputError) {
                            Text(
                                text = "Invalid input passed. Please check that input values are integer",
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
                                text = "No input passed. Please check that input values are integer",
                                color = MaterialTheme.colorScheme.errorContainer
                            )
                        }
                        if (showIncorrectInputError) {
                            Text(
                                text = "Invalid input passed. Please check that input values are integer",
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