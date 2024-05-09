package model

open class DirectedGraph<V> : Graph<V> {
    private val _vertices = hashMapOf<Int, Vertex<V>>()
    private val _edges = hashMapOf<Int, Edge<V>>()

    override val vertices: Collection<Vertex<V>>
        get() = _vertices.values

    override val edges: Collection<Edge<V>>
        get() = _edges.values

    override val isDirected: Boolean
        get() = true

    private val adjacencies: HashMap<Vertex<V>, ArrayList<Edge<V>>> = HashMap()

    override fun addVertex(data: V): Vertex<V> {
        val vertex = Vertex(adjacencies.count(), data)
        _vertices[adjacencies.count()] = vertex
        adjacencies[vertex] = ArrayList()
        return vertex
    }

    override fun addEdge(source: Vertex<V>, destination: Vertex<V>, weight: Double?) {
        addDirectedEdge(source, destination, weight)
    }

    fun addDirectedEdge(source: Vertex<V>, destination: Vertex<V>, weight: Double?) {
        val edge = Edge(_edges.count(), source, destination, weight)
        _edges[_edges.count()] = edge
        adjacencies[source]?.add(edge)
    }

    override fun edges(source: Vertex<V>) = adjacencies[source] ?: arrayListOf()

    override fun weight(source: Vertex<V>, destination: Vertex<V>): Double? {
        return edges(source).firstOrNull { it.destination == destination }?.weight
    }

    override fun getRankingList(): List<Pair<Vertex<V>, Double>> {
        TODO("Not yet implemented")
    }

}