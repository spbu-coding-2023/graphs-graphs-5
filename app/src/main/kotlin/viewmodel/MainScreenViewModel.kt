package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.Graph


class MainScreenViewModel<V>(graph: Graph<V>, private val representationStrategy: RepresentationStrategy) {
    val showVerticesLabels = mutableStateOf(false)
    val showEdgesLabels = mutableStateOf(false)
    val graphViewModel = GraphViewModel(graph, showVerticesLabels, showEdgesLabels)
    init {
        representationStrategy.place(650.0, 550.0, graphViewModel.vertices)
    }
    fun resetGraphView() {
        representationStrategy.place(650.0, 550.0, graphViewModel.vertices)
        graphViewModel.vertices.forEach{ v ->
            /* change color when applying view and theme */
            v.color = Color.Black
            v.radius = 20.dp
        }
    }
}