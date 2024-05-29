package model.algorithms

import model.*

class DirectedGraphAlgorithmsImpl<V> : DirectedGraphAlgorithms<V>, CommonAlgorithmsImpl<V>() {
    override fun findStrongComponents(graph: Graph<V>): List<Pair<Vertex<V>, Int>> {
        val visited = BooleanArray(graph.vertices.size)
        val listOfOrder = mutableListOf<Vertex<V>>()
        graph.vertices.forEach { v ->
            dfs(v, listOfOrder, visited, graph)
        }
        val transposeGraph = buildTransposeGraph(graph)
        val assigned = BooleanArray(listOfOrder.size)
        var componentNum = 0
        val componentsList = mutableListOf<Pair<Vertex<V>, Int>>()
        listOfOrder.forEach { v ->
            if (!assigned[v.index]) {
                assignComponentNum(v, componentNum, componentsList, assigned, transposeGraph)
                componentNum++
            }
        }
        val resultList = componentsList.sortedBy { it.first.index }
        return resultList
    }

    private fun <V> assignComponentNum(
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

    private fun <V> dfs(v: Vertex<V>, listOfOrder: MutableList<Vertex<V>>, visited: BooleanArray, graph: Graph<V>) {
        if (!visited[v.index]) {
            visited[v.index] = true
            graph.edges(v).forEach {e ->
                dfs(e.destination, listOfOrder, visited, graph)
            }
            listOfOrder.add(0, v)
        }
    }

    /* transpose graph is built from the graph by changing direction of every edge */
    private fun <V> buildTransposeGraph(graph: Graph<V>): Graph<V> {
        val transposeGraph = DirectedGraph<V>()
        graph.vertices.forEach { v ->
            transposeGraph.addVertex(v.data, v.dBIndex)
        }
        graph.edges.forEach { e ->
            transposeGraph.addEdge(e.destination, e.source)
        }
        return transposeGraph
    }
    
    override fun findPathWithFordBellman(source: Vertex<V>, destination: Vertex<V>, graph: Graph<V>): MutableList<Vertex<V>>? {
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
                if (distance[e.source.index] + e.weight < distance[e.destination.index]) {
                    distance[e.destination.index] = distance[e.source.index] + e.weight
                    predecessor[e.destination.index] = e.source
                }
            }
        }
        /* non-reachable vertex has no path */
        if (distance[destination.index] == Double.MAX_VALUE) return null
        graph.edges.forEach { e ->
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

    override fun getCycles(graph: Graph<V>, source: Vertex<V>): MutableList<Int>? {
        val adjMap: MutableMap<Int, MutableList<Int>> = HashMap()

        // Construct the adjacency list from the graph edges
        for (edge in graph.edges) {
            if (adjMap[edge.source.index] == null) {
                adjMap[edge.source.index] = mutableListOf()
            }
            adjMap[edge.source.index]?.add(edge.destination.index)
        }

        var cycles = mutableListOf<MutableList<Int>>()
        val color = IntArray(graph.vertices.size) { 0 }
        val ancestorList = IntArray(graph.vertices.size) { -1 }

        detectCyclesViaDFS(source.index, -1, color, ancestorList, cycles, adjMap)

        if (cycles.isEmpty()) {
            return null
        }
        cycles = deleteOverlappingCycles(cycles)
        if (cycles[0].contains(source.index)) {
            val cycle = cycles[0].toSet()
            return cycle.toMutableList()
        }
        else {
            return null
        }
    }

    private fun detectCyclesViaDFS(
        curVertex: Int,
        curParent: Int,
        color: IntArray,
        ancestorList: IntArray,
        cycles: MutableList<MutableList<Int>>,
        adjMap: MutableMap<Int, MutableList<Int>>
    ) {
        color[curVertex] = 1

        val neighborList = adjMap[curVertex]
        if (neighborList != null) {
            for (nextVer in neighborList) {
                if (color[nextVer] == 0) {
                    ancestorList[nextVer] = curVertex
                    detectCyclesViaDFS(nextVer, curVertex, color, ancestorList, cycles, adjMap)
                } else if (color[nextVer] == 1 && nextVer != curParent) {
                    // Found a cycle
                    val detectedCycle = mutableListOf<Int>()
                    var vertexToAdd = curVertex
                    detectedCycle.add(vertexToAdd)

                    while (vertexToAdd != nextVer) {
                        vertexToAdd = ancestorList[vertexToAdd]
                        detectedCycle.add(vertexToAdd)
                    }
                    detectedCycle.add(nextVer)
                    cycles.add(detectedCycle)
                }
            }
        }

        color[curVertex] = 2
    }

    private fun deleteOverlappingCycles(result: MutableList<MutableList<Int>>): MutableList<MutableList<Int>> {
        val size = result.size
        val cyclesToRemove = mutableListOf<Int>()
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (i != j) {
                    if (result[i].containsAll(result[j])) {
                        cyclesToRemove.add(j)
                    }
                }
            }
        }
        cyclesToRemove.sortDescending()
        for (index in cyclesToRemove) {
            result.removeAt(index)
        }
        return result
    }
}