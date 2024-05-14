package model

interface Graph<V> {
    val vertices: Collection<Vertex<V>>
    val edges: Collection<Edge<V>>
    val isDirected: Boolean
    fun addVertex(data: V): Vertex<V>
    fun addEdge(source: Vertex<V>, destination: Vertex<V>, weight: Double? = 1.0)
    /* this method allows to get a list of edges for which current vertex is the source
    * it is also needed for simplifying of key vertices' identification */
    fun edges(source: Vertex<V>): ArrayList<Edge<V>>
    fun weight(source: Vertex<V>, destination: Vertex<V>): Double?
    /* this method is needed for identifying key vertices and further mapping with viewmodel */
    fun getRankingListOfVertices(): List<Pair<Vertex<V>, Double>>
    fun findStrongComponents(): MutableList<List<Vertex<V>>>
}