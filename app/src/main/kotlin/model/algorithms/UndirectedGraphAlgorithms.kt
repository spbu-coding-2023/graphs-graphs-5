package model.algorithms

import model.Edge
import model.Graph

interface UndirectedGraphAlgorithms<V> {
    fun findBridges(graph: Graph<V>): MutableList<Edge<V>>

    fun findCore(graph: Graph<V>): List<Edge<V>>
}