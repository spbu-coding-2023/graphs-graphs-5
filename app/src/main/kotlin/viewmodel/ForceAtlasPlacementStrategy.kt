package viewmodel

import androidx.compose.ui.unit.dp
import model.algorithms.VertexOffset
import model.algorithms.applyForceAtlas2

class ForceAtlasPlacementStrategy : RepresentationStrategy {
    override fun <V> place(width: Double, height: Double, graphViewModel: GraphViewModel<V>) {
        val vertices = graphViewModel.vertices
        if (vertices.isEmpty()) {
            println("forceAtlas2.place: there is nothing to place 👐🏻")
            return
        }
        val positions = applyForceAtlas2(graphViewModel.graph)
        val sorted = vertices.sortedBy { it.label }
        var point: VertexOffset<V>
        val center = Pair(width / 2, height / 2)
        var i = 0
        sorted
            .onEach {
                point = positions[i]
                it.x = (point.offset.x * 250).dp + center.first.dp
                it.y = (point.offset.y * 250).dp + center.second.dp
                i++
            }
    }
}