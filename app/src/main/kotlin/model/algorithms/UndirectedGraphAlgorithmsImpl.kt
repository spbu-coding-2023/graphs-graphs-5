package model.algorithms

import model.Edge
import model.Graph
import model.UndirectedGraph
import model.Vertex

class UnionFind(size: Int) {
    private val parent = IntArray(size) { it }
    private val rank = IntArray(size) { 1 }

    fun find(x: Int): Int {
        if (parent[x] != x) {
            parent[x] = find(parent[x])
        }
        return parent[x]
    }

    fun union(x: Int, y: Int) {
        val rootX = find(x)
        val rootY = find(y)

        if (rootX != rootY) {
            if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX
            } else if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY
            } else {
                parent[rootY] = rootX
                rank[rootX] += 1
            }
        }
    }
}

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

    override fun findCore(graph: Graph<V>): Set<Vertex<V>> {
        val edges = graph.edges.sortedBy { it.weight }
        val unionFind = UnionFind(graph.vertices.size)
        val mstVertices = mutableSetOf<Vertex<V>>()

        for (edge in edges) {
            val root1 = unionFind.find(edge.source.index)
            val root2 = unionFind.find(edge.destination.index)

            if (root1 != root2) {
                mstVertices.add(edge.source)
                mstVertices.add(edge.destination)
                unionFind.union(root1, root2)
            }
        }

        return mstVertices
    }
}