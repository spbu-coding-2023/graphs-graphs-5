package model.algorithms

import model.Graph

interface CommonAlgoritnms<V> {
    fun getClusters(graph: Graph<V>): List<List<Int>>

    fun findKeyVertices(graph: Graph<V>): List<List<Int>>

    fun getCycles(graph: Graph<V>): MutableList<MutableList<Int>>?

    fun findPathWithDijkstra(graph: Graph<V>): ArrayDeque<Int>?
}