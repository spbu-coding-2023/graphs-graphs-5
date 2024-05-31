package view.mainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import view.inputs.MenuInput

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
            modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline
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
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
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
                    color = androidx.compose.material3.MaterialTheme.colorScheme.outline
                )
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = when {
                        (selectedText == "Pick an algo") -> "Currently selected: none"
                        else -> "Currently selected: $selectedText"
                    },
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
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
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                                )
                            },
                            isError = showNoInputError || showIncorrectInputError,
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                                cursorColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary,
                                focusedIndicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                            )
                        )
                        if (showNoInputError) {
                            Text(
                                text = "Error: no input passed. Please check that input values are integer",
                                color = androidx.compose.material3.MaterialTheme.colorScheme.errorContainer
                            )
                        }
                        if (showIncorrectInputError) {
                            Text(
                                text = "Error: invalid input passed. Please check that input values are integer",
                                color = androidx.compose.material3.MaterialTheme.colorScheme.errorContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    if (menuInputState.inputValueOneVertex.all { it.isDigit() } && menuInputState.inputValueOneVertex.isNotEmpty()) {
                                        showOneVertexSelection = false
                                    } else if (menuInputState.inputValueOneVertex.isEmpty()) {
                                        showNoInputError = true
                                    } else {
                                        showIncorrectInputError = true
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text(
                                    text = "Select",
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    showOneVertexSelection = false
                                    showIncorrectInputError = false
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text(
                                    text = "Escape",
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
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
                        Text(
                            text = "Insert start and end vertex indices",
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = menuInputState.inputStartTwoVer,
                            onValueChange = { newValue ->
                                menuInputState = menuInputState.copy(inputStartTwoVer = newValue)
                                showNoInputError = false
                            },
                            label = {
                                Text(
                                    text = "Start index",
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                                )
                            },
                            isError = showNoInputError,
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                                cursorColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary,
                                focusedIndicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = menuInputState.inputEndTwoVer,
                            onValueChange = { newValue ->
                                menuInputState = menuInputState.copy(inputEndTwoVer = newValue)
                                showNoInputError = false
                                showIncorrectInputError = false
                            },
                            label = {
                                Text(
                                    text = "End index",
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                                )
                            },
                            isError = showNoInputError || showIncorrectInputError,
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                                cursorColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary,
                                focusedIndicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                            )
                        )
                        if (showNoInputError) {
                            Text(
                                text = "Error: no input passed. Please check that input values are integer",
                                color = androidx.compose.material3.MaterialTheme.colorScheme.errorContainer
                            )
                        }
                        if (showIncorrectInputError) {
                            Text(
                                text = "Error: invalid input passed. Please check that input values are integer",
                                color = androidx.compose.material3.MaterialTheme.colorScheme.errorContainer
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
                                        showTwoVertexSelection = false
                                    } else if (menuInputState.inputStartTwoVer.isEmpty()
                                        || menuInputState.inputEndTwoVer.isEmpty()
                                    ) {
                                        showNoInputError = true
                                    } else {
                                        showIncorrectInputError = true
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text(
                                    text = "Select",
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    showTwoVertexSelection = false
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text(
                                    text = "Escape",
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
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
