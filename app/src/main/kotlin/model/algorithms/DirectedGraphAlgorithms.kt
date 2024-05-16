package model.algorithms

import model.DirectedGraph
import model.Vertex

interface DirectedGraphAlgorithms<V> {
    fun findStrongComponents(graph: DirectedGraph<V>): MutableList<List<Vertex<V>>>
}