package model

interface Graph<V> {
    val vertices: Collection<Vertex<V>>
    val edges: Collection<Edge<V>>
    val isDirected: Boolean
    fun addVertex(data: V): Vertex<V>
    fun addEdge(source: Vertex<V>, destination: Vertex<V>, weight: Double? = 1.0)
    fun edges(source: Vertex<V>): ArrayList<Edge<V>>
    fun weight(source: Vertex<V>, destination: Vertex<V>): Double?
    fun getRankingList(): List<Pair<Vertex<V>, Double>>
}