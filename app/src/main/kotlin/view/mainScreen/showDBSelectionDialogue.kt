package view.mainScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import view.inputs.DBInput


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