package model.algorithms

import model.Graph
import model.UndirectedGraph

interface UndirectedGraphAlgorithms<V> {
    fun findBridges(graph: Graph<V>)

    fun findCore(graph: Graph<V>)
}