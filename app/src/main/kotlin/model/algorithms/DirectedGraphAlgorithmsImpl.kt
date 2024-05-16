package model.algorithms

import model.*

class DirectedGraphAlgorithmsImpl<V> : DirectedGraphAlgorithms<V>, CommonAlgoritnmsImpl<V>() {
    override fun findStrongComponents(graph: DirectedGraph<V>): MutableList<List<Vertex<V>>> {
        val visited = BooleanArray(graph.vertices.size)
        val listOfOrder = mutableListOf<Vertex<V>>()
        graph.vertices.forEach { v ->
            dfs(v, listOfOrder, visited, graph)
        }
        val transposeGraph = buildTransposeGraph(graph)
        val assigned = BooleanArray(listOfOrder.size)
        var component = mutableListOf<Vertex<V>>()
        val componentsList = mutableListOf<List<Vertex<V>>>()
        listOfOrder.forEach { v ->
            assignComponent(v, component, componentsList, assigned, transposeGraph)
            component = mutableListOf()
        }
        return componentsList
    }
}