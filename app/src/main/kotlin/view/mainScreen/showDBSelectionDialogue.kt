package view.mainScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import view.inputs.DBInput

@Composable
fun showDBSelectionDialogue(
    showDBSelectionDialogue: Boolean,
    selectedDatabase: String,
    dBInput: DBInput,
    showDBSelection: (Boolean) -> Unit,
    selectedDB: (String) -> Unit,
    onDBInputChange: (DBInput) -> Unit,
    onLoadGraphChange: (Boolean) -> Unit,
    isLoaded: (Boolean) -> Unit
) {
    var newState by remember { mutableStateOf(DBInput()) }
    if (showDBSelectionDialogue) {
        AlertDialog(
            onDismissRequest = { showDBSelection(false) },
            title = {
                Text(
                    text = "Load Graph",
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Select Database:",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedDatabase == "neo4j",
                            onClick = { selectedDB("neo4j") },
                            colors = RadioButtonDefaults.colors(androidx.compose.material3.MaterialTheme.colorScheme.secondary)

                        )
                        Text(
                            text = "neo4j",
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedDatabase == "sqlite",
                            onClick = { selectedDB("sqlite") },
                            colors = RadioButtonDefaults.colors(androidx.compose.material3.MaterialTheme.colorScheme.secondary)
                        )
                        Text(
                            text = "sqlite",
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedDatabase == ".json",
                            onClick = { selectedDB(".json") },
                            colors = RadioButtonDefaults.colors(androidx.compose.material3.MaterialTheme.colorScheme.secondary)
                        )
                        Text(
                            text = ".json file",
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    when (selectedDatabase) {
                        "neo4j" -> {
                            newState = newState.copy(dBType = "neo4j")
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Enter Neo4j Details:",
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newState.uri,
                                onValueChange = { newState = newState.copy(uri = it) },
                                label = {
                                    Text(
                                        text = "URI",
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    cursorColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    focusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline
                                ),
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newState.login,
                                onValueChange = { newState = newState.copy(login = it) },
                                label = {
                                    Text(
                                        text = "Login",
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    cursorColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    focusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline
                                ),
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newState.password,
                                onValueChange = { newState = newState.copy(password = it) },
                                label = {
                                    Text(
                                        text = "Password",
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    cursorColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    focusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline
                                ),
                                visualTransformation = PasswordVisualTransformation()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Switch(
                                    checked = newState.isUpdatedNeo4j,
                                    onCheckedChange = { newState = newState.copy(isUpdatedNeo4j = it) },
                                    colors = SwitchDefaults.colors(androidx.compose.material3.MaterialTheme.colorScheme.secondary)
                                )
                                Text(
                                    text = "Is database updated? (no/yes) If no, results of algorithms from previous runs can be displayed",
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Switch(
                                    checked = newState.isUndirected,
                                    onCheckedChange = { newState = newState.copy(isUndirected = it) },
                                    colors = SwitchDefaults.colors(androidx.compose.material3.MaterialTheme.colorScheme.secondary)
                                )
                                Text(
                                    text = "Is graph undirected? (no/yes)",
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        "sqlite" -> {
                            newState = newState.copy(dBType = "sqlite")
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Enter SQLite Details:",
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newState.pathToDb,
                                onValueChange = { newState = newState.copy(pathToDb = it) },
                                label = {
                                    Text(
                                        text = "Path to database",
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    cursorColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    focusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline
                                ),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newState.name,
                                onValueChange = { newState = newState.copy(name = it) },
                                label = {
                                    Text(
                                        text = "Name",
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    cursorColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    focusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline
                                ),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Switch(
                                    checked = newState.isUpdatedSql,
                                    onCheckedChange = { newState = newState.copy(isUpdatedSql = it) },
                                    colors = SwitchDefaults.colors(androidx.compose.material3.MaterialTheme.colorScheme.secondary)
                                )
                                Text(
                                    text = "Is database updated? (no/yes) If no, results of algorithms from previous runs can be displayed",
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        ".json" -> {
                            newState = newState.copy(dBType = ".json")
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Enter .json Details:",
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newState.pathToDb,
                                onValueChange = { newState = newState.copy(pathToDb = it) },
                                label = {
                                    Text(
                                        text = "Path to file",
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    cursorColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    focusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline
                                ),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newState.name,
                                onValueChange = { newState = newState.copy(name = it) },
                                label = {
                                    Text(
                                        text = "Name",
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                                    )
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    cursorColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    focusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
                                    unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline
                                ),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDBSelection(false)
                        isLoaded(false)
                        onDBInputChange(
                            dBInput.copy(
                                dBType = newState.dBType,
                                isUpdatedSql = newState.isUpdatedSql,
                                pathToDb = newState.pathToDb,
                                name = newState.name,
                                isUpdatedNeo4j = newState.isUpdatedNeo4j,
                                uri = newState.uri,
                                login = newState.login,
                                password = newState.password,
                                isUndirected = newState.isUndirected
                            )
                        )
                        onLoadGraphChange(
                            when {
                                dBInput.dBType == "sqlite" && (dBInput.name.isEmpty() || dBInput.pathToDb.isEmpty()) -> false
                                else -> true
                            }
                        )
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(
                        text = "Load",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDBSelection(false) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(
                        text = "Cancel",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        )
    }
}