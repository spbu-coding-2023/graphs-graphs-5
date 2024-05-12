package viewmodel

import androidx.compose.runtime.State
import model.Edge

class EdgeViewModel<V>(
    val u: VertexViewModel<V>,
    val v: VertexViewModel<V>,
    private val edge: Edge<V>,
    private val _labelVisible: State<Boolean>,
    private val _isDirected: Boolean
) {
    val label
        get() = edge.weight.toString()

    val labelVisible
        get() = _labelVisible.value

    val isDirected
        get() = _isDirected
}
