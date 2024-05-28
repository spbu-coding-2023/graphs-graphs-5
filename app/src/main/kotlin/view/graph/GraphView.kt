package view.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import viewmodel.GraphViewModel

@Composable
fun <V> DirectedGraphView(
    viewModel: GraphViewModel<V>,
    scale: Float,
    offset: DpOffset
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .offset(offset.x, offset.y)
    ) {
        viewModel.edges.forEach { e ->
            DirectedEdgeView(e)
        }
        viewModel.vertices.forEach { v ->
            VertexView(v)
        }
    }
}

@Composable
fun <V> UndirectedGraphView(
    viewModel: GraphViewModel<V>,
    scale: Float,
    offset: DpOffset
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .offset(offset.x, offset.y)
    ) {
        viewModel.edges.forEach { e ->
            UndirectedEdgeView(e)
        }
        viewModel.vertices.forEach { v ->
            VertexView(v)
        }
    }
}