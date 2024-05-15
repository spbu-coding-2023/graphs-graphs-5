package model

fun <V> findPathWithFordBellman(source: Vertex<V>, destination: Vertex<V>, graph: DirectedGraph<V>): MutableList<Vertex<V>>? {
    val distance = DoubleArray(graph.vertices.size)
    /* whose predecessor and who is predecessor */
    val predecessor = mutableListOf<Vertex<V>>()
    graph.vertices.forEach { u ->
        distance[u.index] = Double.MAX_VALUE
    }
    /* say if vertex has itself as a predecessor, it has no predecessor */
    graph.vertices.forEach { u ->
        predecessor.add(u)
    }
    distance[source.index] = 0.0
    for (i in 1 until graph.vertices.size) {
        graph.edges.forEach { e ->
            /* change */
            if (e.weight == null) return null
            if (distance[e.source.index] + e.weight < distance[e.destination.index]) {
                distance[e.destination.index] = distance[e.source.index] + e.weight
                predecessor[e.destination.index] = e.source
            }
        }
    }
    /* non-reachable vertex has no path */
    if (distance[destination.index] == Double.MAX_VALUE) return null
    graph.edges.forEach { e ->
        /* change */
        if (e.weight == null) return null
        if (distance[e.source.index] + e.weight < distance[e.destination.index]) {
            var v1 = e.source
            var v2 = e.destination
            predecessor[v2.index] = v1
            val visited = BooleanArray(graph.vertices.size)
            visited[v2.index] = true
            while (!visited[v1.index]) {
                visited[v1.index] = true
                v1 = predecessor[v1.index]
            }
            val negativeCycle = mutableListOf<Vertex<V>>()
            negativeCycle.add(v1)
            v2 = predecessor[v1.index]
            while (v1 != v2) {
                negativeCycle += v2
                v2 = predecessor[v2.index]
            }
            negativeCycle += v1
            println("Graph contains a negative-weight cycle: distances lead to negative infinity")
            return negativeCycle
        }
    }
    val path = mutableListOf<Vertex<V>>()
    path.add(0, destination)
    var vertexOnPath = destination
    graph.vertices.forEach {
        path.add(0, predecessor[vertexOnPath.index])
        vertexOnPath = predecessor[vertexOnPath.index]
        if (vertexOnPath == source) return path
    }
    return path

}