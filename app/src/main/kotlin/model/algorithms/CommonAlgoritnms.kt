package model.algorithms

import model.Graph
import model.Vertex

interface CommonAlgorithms<V> {
    fun createAdjacencyMatrix(graph: Graph<V>): Array<DoubleArray>

    fun getClusters(graph: Graph<V>): IntArray

    fun findKeyVertices(graph: Graph<V>): List<Pair<Vertex<V>, Double>>

    fun findPathWithDijkstra(
        graph: Graph<V>,
        source: Vertex<V>,
        sink: Vertex<V>
    ): Pair<String, Pair<ArrayDeque<Int>?, Double?>>
}