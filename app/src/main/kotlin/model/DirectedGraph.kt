package model

import kotlin.math.pow

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

    private fun getDegree(vertex: Vertex<V>): Int {
        val edges = adjacencies[vertex]
        val numberOfEdges = edges?.size ?: 0
        return numberOfEdges
    }

    private fun getGreatestDegree(): Int {
        val listOfDegrees = mutableListOf<Int>()
        vertices.forEach { listOfDegrees.add(getDegree(it)) }
        val maxDegree = listOfDegrees.max()
        return maxDegree
    }

    /* |N_i(k)| -- getNumOfNeighborsWithDegree(k, v_i) is the number of neighbors of degree k of node v_i  */
    private fun getNumOfNeighborsWithDegree(k: Int, vertex: Vertex<V>): Int {
        var result = 0
        edges(vertex).forEach {
            if (getDegree(it.destination) == k) {
                result++
            }
        }
        return result
    }

    /* S_k(v_i) -- cumulativeFunctionVectorElement(k, v_i) is the value of the kth index of vector */
    private fun getCumulativeFunctionVectorElement(k: Int, vertex: Vertex<V>): Int {
        var cumulativeVectorElement = 0
        if (k == 1) {
            cumulativeVectorElement = getDegree(vertex)
        } else if (k > 1) {
            cumulativeVectorElement =
                getCumulativeFunctionVectorElement(k - 1, vertex) - getNumOfNeighborsWithDegree(k - 1, vertex)
        }
        return cumulativeVectorElement
    }

    private fun cumulativeCentrality(vertex: Vertex<V>): Double {
        val h = getGreatestDegree()
        var cmc = 0.0
        /* p and r are two tunable parameters. Based on the results in the tables of the articles,
        * the authors claim that the highest correlation over different datasets can be obtained for p = 0.8 and r = 100
        */
        val p = 0.8
        val r = 100.0
        for (k in 1..h) {
            cmc += p.pow(1.0 + k.toDouble() * p / r) * getCumulativeFunctionVectorElement(k, vertex).toDouble()
        }
        return cmc
    }

    /* the extended H-index centrality (EHC) of node is determined based on the cumulative centrality of its neighbors */
    private fun calculateEHC(vertex: Vertex<V>): Double {
        var ehc = 0.0
        edges(vertex).forEach {
            ehc += cumulativeCentrality(it.destination)
        }
        return ehc
    }

    override fun getRankingList(): List<Pair<Vertex<V>, Double>> {
        val rankingList = mutableListOf<Pair<Vertex<V>, Double>>()
        vertices.forEach {
            rankingList.add(Pair(it, (calculateEHC(it))))
        }
        return rankingList
    }

}
