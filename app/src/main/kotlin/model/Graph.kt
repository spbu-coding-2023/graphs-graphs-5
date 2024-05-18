package model

interface Graph<V> {
    val vertices: Collection<Vertex<V>>
    val edges: Collection<Edge<V>>
    val isDirected: Boolean
    val graphType: GraphType
    fun addVertex(data: V): Vertex<V>
    fun addEdge(source: Vertex<V>, destination: Vertex<V>, weight: Double = 1.0)
    /* this method allows to get a list of edges for which current vertex is the source
    * it is also needed for simplifying of key vertices' identification */
    fun edges(source: Vertex<V>): ArrayList<Edge<V>>
    fun weight(source: Vertex<V>, destination: Vertex<V>): Double?
}

enum class GraphType {
    DIRECTED,
    UNDIRECTED
}