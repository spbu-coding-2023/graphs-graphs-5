package model.algorithms

import model.DirectedGraph
import model.Graph
import model.Vertex

fun <V> assignComponent(
    v: Vertex<V>,
    component: MutableList<Vertex<V>>,
    componentsList: MutableList<List<Vertex<V>>>,
    assigned: BooleanArray,
    graph: Graph<V>
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

fun <V> dfs(v: Vertex<V>, listOfOrder: MutableList<Vertex<V>>, visited: BooleanArray, graph: Graph<V>) {
    if (!visited[v.index]) {
        visited[v.index] = true
        graph.edges(v).forEach {e ->
            dfs(e.destination, listOfOrder, visited, graph)
        }
        listOfOrder.add(0, v)
    }
}

/* transpose graph is built from the graph by changing direction of every edge */
fun <V> buildTransposeGraph(graph: Graph<V>): Graph<V> {
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