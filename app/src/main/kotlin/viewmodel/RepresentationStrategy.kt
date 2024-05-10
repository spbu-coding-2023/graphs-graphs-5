package viewmodel

interface RepresentationStrategy {
    fun <V> place(width: Double, height: Double, vertices: Collection<VertexViewModel<V>>)
}