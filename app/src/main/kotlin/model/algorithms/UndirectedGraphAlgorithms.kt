package model.algorithms

import model.Edge
import model.Graph
import model.UndirectedGraph

interface UndirectedGraphAlgorithms<V> {
    fun findBridges(graph: Graph<V>): MutableList<Edge<V>>

    fun findCore(graph: Graph<V>)
}