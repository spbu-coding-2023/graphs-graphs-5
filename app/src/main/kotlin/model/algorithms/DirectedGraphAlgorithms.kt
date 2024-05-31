package model.algorithms

import model.Graph
import model.Vertex

interface DirectedGraphAlgorithms<V> {
    fun getCycles(graph: Graph<V>, source: Vertex<V>): MutableList<Int>?

    fun findStrongComponents(graph: Graph<V>): List<Pair<Vertex<V>, Int>>

    fun findPathWithFordBellman(source: Vertex<V>, destination: Vertex<V>, graph: Graph<V>): MutableList<Vertex<V>>?
}