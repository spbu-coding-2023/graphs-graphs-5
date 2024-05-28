package viewmodel

import androidx.compose.ui.unit.dp
import view.Theme.BlackAndWhite60
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CircularPlacementStrategy : RepresentationStrategy {
    override fun <V> place(width: Double, height: Double,  graphViewModel: GraphViewModel<V>) {
        val vertices = graphViewModel.vertices
        if (vertices.isEmpty()) {
            println("CircularPlacementStrategy.place: there is nothing to place 👐🏻")
            return
        }
        val center = Pair(width / 2, height / 2)
        val angle = 2 * Math.PI / vertices.size
        val sorted = vertices.sortedBy { it.label }
        val first = sorted.first()
        var point = Pair(center.first, center.second - min(width, height) / 2)
        first.x = point.first.dp
        first.y = point.second.dp
        first.color = BlackAndWhite60
        sorted
            .drop(1)
            .onEach {
                point = point.rotate(center, angle)
                it.x = point.first.dp
                it.y = point.second.dp
            }
    }
    private fun Pair<Double, Double>.rotate(pivot: Pair<Double, Double>, angle: Double): Pair<Double, Double> {
        val sin = sin(angle)
        val cos = cos(angle)

        val diff = first - pivot.first to second - pivot.second
        val rotated = Pair(
            diff.first * cos - diff.second * sin,
            diff.first * sin + diff.second * cos,
        )
        return rotated.first + pivot.first to rotated.second + pivot.second
    }
}