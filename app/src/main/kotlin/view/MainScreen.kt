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
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import viewmodel.MainScreenViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.*

@Composable
fun <V> MainScreen(darkTheme: Boolean, onThemeUpdated: () -> Unit, viewModel: MainScreenViewModel<V>) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        /* add snackbar for some messages */
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
                /* add switcher for themes */
            }
            Row(
            ) {
                Column(modifier = Modifier.width(300.dp)) {
                    Spacer(modifier = Modifier.padding(8.dp))
                    showVerticesLabels(viewModel)
                    showEdgesLabels(viewModel)
                    resetGraphView(viewModel)
                    Button(
                        onClick = { },
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
                /* add menu */
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.surface
                    ) {
                    GraphView(viewModel.graphViewModel)
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