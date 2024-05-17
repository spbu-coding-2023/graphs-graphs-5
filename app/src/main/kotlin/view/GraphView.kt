package view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import viewmodel.GraphViewModel

@Composable
fun <V> DirectedGraphView(
    viewModel: GraphViewModel<V>,
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        viewModel.vertices.forEach { v ->
            VertexView(v)
        }
        viewModel.edges.forEach { e ->
            DirectedEdgeView(e)
        }
    }
}

@Composable
fun <V> UndirectedGraphView(
    viewModel: GraphViewModel<V>,
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        viewModel.vertices.forEach { v ->
            VertexView(v)
        }
        viewModel.edges.forEach { e ->
            UndirectedEdgeView(e)
        }
    }
}