package model.algorithms

import model.Edge
import model.Graph
import model.UndirectedGraph
import model.Vertex

interface UndirectedGraphAlgorithms<V> {
    fun findBridges(graph: Graph<V>): MutableList<Edge<V>>

    fun findCore(graph: Graph<V>): Set<Vertex<V>>
}