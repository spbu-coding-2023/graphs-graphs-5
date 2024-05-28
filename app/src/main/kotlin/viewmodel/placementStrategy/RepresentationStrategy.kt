package viewmodel.placementStrategy

import viewmodel.graph.GraphViewModel

interface RepresentationStrategy {
    fun <V> place(width: Double, height: Double,  graphViewModel: GraphViewModel<V>)
}