package model.algorithms

import model.DirectedGraph
import model.Graph
import model.Vertex

fun <V> assignComponentNum(
    v: Vertex<V>,
    componentNum: Int,
    componentsList: MutableList<Pair<Vertex<V>, Int>>,
    assigned: BooleanArray,
    graph: Graph<V>
) {
    if (!assigned[v.index]) {
        assigned[v.index] = true
        componentsList.add(Pair(v, componentNum))
        graph.edges(v).forEach {
            assignComponentNum(it.destination, componentNum, componentsList, assigned,  graph)
        }
    }
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