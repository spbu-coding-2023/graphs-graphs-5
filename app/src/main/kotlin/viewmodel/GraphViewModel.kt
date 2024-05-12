package viewmodel

import androidx.compose.runtime.State
import androidx.compose.ui.unit.dp
import model.Graph
import view.BlackAndWhite70

class GraphViewModel<V>(
    private val graph: Graph<V>,
    showVerticesLabels: State<Boolean>,
    showEdgesLabels: State<Boolean>,
) {
    private val _vertices = graph.vertices.associateWith { v ->
        VertexViewModel(0.dp, 0.dp, BlackAndWhite70, v, showVerticesLabels)
    }
    private val _edges = graph.edges.associateWith { e ->
        val fst = _vertices[e.source]
            ?: throw IllegalStateException("VertexView for ${e.source} not found")
        val snd = _vertices[e.destination]
            ?: throw IllegalStateException("VertexView for ${e.destination} not found")
        EdgeViewModel(fst, snd, e, showEdgesLabels, isDirected)
    }
    val vertices: Collection<VertexViewModel<V>>
        get() = _vertices.values
    val edges: Collection<EdgeViewModel<V>>
        get() = _edges.values
    val isDirected: Boolean
        get() = graph.isDirected
    /* add binding to ehc algorithm */
}