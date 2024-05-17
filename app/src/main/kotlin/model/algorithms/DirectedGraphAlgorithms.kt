package model.algorithms

import model.DirectedGraph
import model.Graph
import model.Vertex

interface DirectedGraphAlgorithms<V> {
    fun findStrongComponents(graph: DirectedGraph<V>): MutableList<List<Vertex<V>>>

    fun findPathWithFordBellman(graph: Graph<V>): ArrayDeque<Int>?
}