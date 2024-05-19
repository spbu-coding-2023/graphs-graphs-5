package model.algorithms

import model.Graph
import model.Vertex

interface CommonAlgorithms<V> {
    fun createAdjacencyMatrix(graph: Graph<V>): Array<DoubleArray>

    fun getClusters(graph: Graph<V>): List<List<Int>>

    fun findKeyVertices(graph: Graph<V>): List<Pair<Vertex<V>, Double>>

    fun getCycles(graph: Graph<V>): MutableList<MutableList<Int>>?

    fun findPathWithDijkstra(graph: Graph<V>): ArrayDeque<Int>?
}