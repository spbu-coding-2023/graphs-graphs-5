package model

class UndirectedGraph<V> : DirectedGraph<V>(), Graph<V> {
    override fun addEdge(source: Vertex<V>, destination: Vertex<V>, weight: Double?) {
        addDirectedEdge(source, destination, weight)
        addDirectedEdge(destination, source, weight)
    }
    override val isDirected: Boolean
        get() = false
}
