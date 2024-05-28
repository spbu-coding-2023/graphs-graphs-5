package viewmodel

interface RepresentationStrategy {
    fun <V> place(width: Double, height: Double,  graphViewModel: GraphViewModel<V>)
}