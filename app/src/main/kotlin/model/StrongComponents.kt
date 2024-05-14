package model

fun <V> dfs(v: Vertex<V>, listOfOrder: MutableList<Vertex<V>>, visited: BooleanArray, graph: DirectedGraph<V>) {
    if (!visited[v.index]) {
        visited[v.index] = true
        graph.edges(v).forEach {
            dfs(it.destination, listOfOrder, visited, graph)
        }
        listOfOrder.add(0, v)
    }
}

/* transpose graph is built from the graph by changing direction of every edge */
fun <V> buildTransposeGraph(graph: DirectedGraph<V>): DirectedGraph<V> {
    if (!graph.isDirected) return graph
    val transposeGraph = DirectedGraph<V>()
    graph.vertices.forEach {v ->
        transposeGraph.addVertex(v.data)
    }
    graph.edges.forEach {e ->
        transposeGraph.addEdge(e.destination, e.source)
    }
    return transposeGraph
}