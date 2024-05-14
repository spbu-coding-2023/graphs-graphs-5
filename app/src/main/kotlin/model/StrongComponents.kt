package model

fun <V> findStrongComponents(graph: DirectedGraph<V>): MutableList<List<Vertex<V>>> {
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

fun <V> assignComponent(
    v: Vertex<V>,
    component: MutableList<Vertex<V>>,
    componentsList: MutableList<List<Vertex<V>>>,
    assigned: BooleanArray,
    graph: DirectedGraph<V>
) {
    if (!assigned[v.index]) {
        assigned[v.index] = true
        component.add(v)
        graph.edges(v).forEach { e ->
            assignComponent(e.destination, component, componentsList, assigned, graph)
        }
    }
    if (!componentsList.contains(component) && component.isNotEmpty()) componentsList.add(component)
}

fun <V> dfs(v: Vertex<V>, listOfOrder: MutableList<Vertex<V>>, visited: BooleanArray, graph: DirectedGraph<V>) {
    if (!visited[v.index]) {
        visited[v.index] = true
        graph.edges(v).forEach {e ->
            dfs(e.destination, listOfOrder, visited, graph)
        }
        listOfOrder.add(0, v)
    }
}

/* transpose graph is built from the graph by changing direction of every edge */
fun <V> buildTransposeGraph(graph: DirectedGraph<V>): DirectedGraph<V> {
    if (!graph.isDirected) return graph
    val transposeGraph = DirectedGraph<V>()
    graph.vertices.forEach { v ->
        transposeGraph.addVertex(v.data)
    }
    graph.edges.forEach { e ->
        transposeGraph.addEdge(e.destination, e.source)
    }
    return transposeGraph
}