package viewmodel.graph

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import model.Edge

class EdgeViewModel<V>(
    val u: VertexViewModel<V>,
    val v: VertexViewModel<V>,
    val edge: Edge<V>,
    private val _labelVisible: State<Boolean>,
    color: Color
) {
    val label
        get() = (edge.weight.toInt()).toString()

    val labelVisible
        get() = _labelVisible.value

    private var _color = mutableStateOf(color)
    var color: Color
        get() = _color.value
        set(value) {
            _color.value = value
        }
}
