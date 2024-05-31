package model.algorithms

import model.Edge
import model.Graph
import model.UndirectedGraph
import model.Vertex

interface UndirectedGraphAlgorithms<V> {
    fun findBridges(graph: Graph<V>): Collection<Edge<V>>

     fun findCore(graph: Graph<V>): List<Edge<V>>
}