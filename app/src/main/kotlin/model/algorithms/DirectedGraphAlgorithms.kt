package model.algorithms

import model.DirectedGraph
import model.Graph
import model.Vertex

interface DirectedGraphAlgorithms<V> {
    fun findStrongComponents(graph: Graph<V>): MutableList<List<Vertex<V>>>

    fun findPathWithFordBellman(source: Vertex<V>, destination: Vertex<V>, graph: Graph<V>): MutableList<Vertex<V>>?
}