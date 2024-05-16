package model.algorithms

import model.Graph

open class CommonAlgoritnmsImpl<V>: CommonAlgoritnms<V> {
    override fun getClusters(graph: Graph<V>): List<List<Int>> {
        TODO("Not yet implemented")
    }

    override fun findKeyVertices(graph: Graph<V>): List<List<Int>> {
        TODO("Not yet implemented")
    }

    override fun getCycles(graph: Graph<V>): MutableList<MutableList<Int>>? {
        TODO("Not yet implemented")
    }

    override fun findPathWithDijkstra(graph: Graph<V>): ArrayDeque<Int>? {
        TODO("Not yet implemented")
    }

    override fun findPathWithFordBellman(graph: Graph<V>): ArrayDeque<Int>? {
        TODO("Not yet implemented")
    }
}