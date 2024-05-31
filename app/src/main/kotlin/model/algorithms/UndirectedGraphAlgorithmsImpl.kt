package model.algorithms

import model.Edge
import model.Graph
import model.UndirectedGraph
import model.Vertex
class UndirectedGraphAlgorithmsImpl<V>: UndirectedGraphAlgorithms<V>, CommonAlgorithmsImpl<V>() {
    override fun findBridges(graph: Graph<V>): MutableList<Edge<V>> {
        val bridges = mutableListOf<Edge<V>>()
        val disc = mutableMapOf<Vertex<V>, Int>()
        val low = mutableMapOf<Vertex<V>, Int>()
        val parent = mutableMapOf<Vertex<V>, Vertex<V>?>()

        graph.vertices.forEach { vertex ->
            disc[vertex] = -1
            low[vertex] = -1
            parent[vertex] = null
        }

        var time = 0

        fun dfsBridge(u: Vertex<V>) {
            disc[u] = time
            low[u] = time
            time++

            for (edge in graph.edges(u)) {
                val v = edge.destination
                if (disc[v] == -1) { // If v is not visited
                    parent[v] = u
                    dfsBridge(v)
                    low[u] = minOf(low[u] ?: Int.MAX_VALUE, low[v] ?: Int.MAX_VALUE)

                    if ((low[v] ?: Int.MAX_VALUE) > (disc[u] ?: Int.MAX_VALUE)) {
                        bridges.add(edge)
                    }
                } else if (v != parent[u]) {
                    low[u] = minOf(low[u] ?: Int.MAX_VALUE, disc[v] ?: Int.MAX_VALUE)
                }
            }
        }

        graph.vertices.forEach { vertex ->
            if (disc[vertex] == -1) {
                dfsBridge(vertex)
            }
        }

        return bridges
    }

    override fun findCore(graph: Graph<V>): List<Edge<V>> {
        val edgesInCore = mutableListOf<Edge<V>>()
        val verticesInCore = mutableSetOf<Vertex<V>>()

        verticesInCore.add(graph.vertices.first()) // Start from the first vertex of the graph

        while (verticesInCore.size < graph.vertices.size) {
            var minWeightEdge: Edge<V>? = null

            for (vertex in verticesInCore) {
                val incidentEdges = graph.edges(vertex)
                for (edge in incidentEdges) {
                    if ((edge.source in verticesInCore && edge.destination !in verticesInCore) ||
                        (edge.destination in verticesInCore && edge.source !in verticesInCore)) {
                        if (minWeightEdge == null || edge.weight < minWeightEdge.weight) {
                            minWeightEdge = edge
                        }
                    }
                }
            }

            if (minWeightEdge != null) {
                edgesInCore.add(minWeightEdge)
                if (minWeightEdge.source !in verticesInCore) {
                    verticesInCore.add(minWeightEdge.source)
                } else {
                    verticesInCore.add(minWeightEdge.destination)
                }
            }
        }

        return edgesInCore
    }
}