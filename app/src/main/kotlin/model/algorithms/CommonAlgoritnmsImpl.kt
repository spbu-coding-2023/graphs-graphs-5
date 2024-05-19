package model.algorithms

import model.Graph
import model.Vertex
import kotlin.math.pow

open class CommonAlgorithmsImpl<V>: CommonAlgorithms<V> {

    override fun createAdjacencyMatrix(graph: Graph<V>): Array<DoubleArray> {
        val adjacencyMatrix = Array(graph.vertices.count()) { DoubleArray(graph.vertices.count()) { 0.0 } }
        val edges = graph.edges.toTypedArray()
        for (i in 0 until graph.edges.count()) {
            val source = edges[i].source.index
            val destination = edges[i].destination.index
            val weight = edges[i].weight
            adjacencyMatrix[source][destination] = weight
        }
        return adjacencyMatrix
    }

    override fun getClusters(graph: Graph<V>): List<List<Int>> {
        TODO("Not yet implemented")
    }

    override fun findKeyVertices(graph: Graph<V>): List<Pair<Vertex<V>, Double>> {
        val rankingList = mutableListOf<Pair<Vertex<V>, Double>>()
        graph.vertices.forEach {
            rankingList.add(Pair(it, (calculateEHC(it, graph))))
        }
        return rankingList
    }
    private fun getDegree(vertex: Vertex<V>, graph: Graph<V>): Int {
        return graph.edges(vertex).size
    }
    private fun getGreatestDegree(graph: Graph<V>): Int {
        val listOfDegrees = mutableListOf<Int>()
        graph.vertices.forEach { listOfDegrees.add(getDegree(it, graph)) }
        val maxDegree = listOfDegrees.max()
        return maxDegree
    }
    /* |N_i(k)| -- getNumOfNeighborsWithDegree(k, v_i) is the number of neighbors of degree k of node v_i  */
    private fun getNumOfNeighborsWithDegree(k: Int, vertex: Vertex<V>, graph: Graph<V>): Int {
        var result = 0
        graph.edges(vertex).forEach {
            if (getDegree(it.destination, graph) == k) {
                result++
            }
        }
        return result
    }
    /* S_k(v_i) -- cumulativeFunctionVectorElement(k, v_i) is the value of the kth index of vector */
    private fun getCumulativeFunctionVectorElement(k: Int, vertex: Vertex<V>, graph: Graph<V>): Int {
        var cumulativeVectorElement = 0
        if (k == 1) {
            cumulativeVectorElement = getDegree(vertex, graph)
        } else if (k > 1) {
            cumulativeVectorElement =
                getCumulativeFunctionVectorElement(k - 1, vertex, graph) - getNumOfNeighborsWithDegree(k - 1, vertex, graph)
        }
        return cumulativeVectorElement
    }
    private fun cumulativeCentrality(vertex: Vertex<V>, graph: Graph<V>): Double {
        val h = getGreatestDegree(graph)
        var cmc = 0.0
        /* p and r are two tunable parameters. Based on the results in the tables of the articles,
        * the authors claim that the highest correlation over different datasets can be obtained for p = 0.8 and r = 100
        */
        val p = 0.8
        val r = 100.0
        for (k in 1..h) {
            cmc += p.pow(1.0 + k.toDouble() * p / r) * getCumulativeFunctionVectorElement(k, vertex, graph).toDouble()
        }
        return cmc
    }
    /* the extended H-index centrality (EHC) of node is determined based on the cumulative centrality of its neighbors */
    private fun calculateEHC(vertex: Vertex<V>, graph: Graph<V>): Double {
        var ehc = 0.0
        graph.edges(vertex).forEach {
            ehc += cumulativeCentrality(it.destination, graph)
        }
        return ehc
    }

    override fun getCycles(graph: Graph<V>): MutableList<MutableList<Int>>? {
        TODO("Not yet implemented")
    }
    override fun findPathWithDijkstra(graph: Graph<V>): ArrayDeque<Int>? {
        TODO("Not yet implemented")
    }
}