package model.algorithms

import model.DirectedGraph
import model.Graph
import model.Vertex

interface DirectedGraphAlgorithms<V> {
    fun findStrongComponents(graph: Graph<V>): List<Pair<Vertex<V>, Int>>

    fun findPathWithFordBellman(source: Vertex<V>, destination: Vertex<V>, graph: Graph<V>): MutableList<Vertex<V>>?
}