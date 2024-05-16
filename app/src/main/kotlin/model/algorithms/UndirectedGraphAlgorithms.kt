package model.algorithms

import model.UndirectedGraph

interface UndirectedGraphAlgorithms<V> {
    fun findBridges(graph: UndirectedGraph<V>)

    fun findCore(graph: UndirectedGraph<V>)
}