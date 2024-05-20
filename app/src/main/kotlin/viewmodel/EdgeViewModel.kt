package viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import model.Edge

class EdgeViewModel<V>(
    val u: VertexViewModel<V>,
    val v: VertexViewModel<V>,
    private val edge: Edge<V>,
    private val _labelVisible: State<Boolean>,
    private val _isDirected: Boolean,
    color: Color
) {
    val label
        get() = (edge.weight.toInt()).toString()

    val labelVisible
        get() = _labelVisible.value

    val isDirected
        get() = _isDirected

    private var _color = mutableStateOf(color)
    var color: Color
        get() = _color.value
        set(value) {
            _color.value = value
        }
}
