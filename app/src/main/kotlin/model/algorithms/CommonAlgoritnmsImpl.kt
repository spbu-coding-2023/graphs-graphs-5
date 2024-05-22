package model.algorithms

import model.Graph
import model.Vertex
import kotlin.math.min
import kotlin.math.pow

open class CommonAlgorithmsImpl<V>: CommonAlgorithms<V> {

    override fun createAdjacencyMatrix(graph: Graph<V>): Array<DoubleArray> {
        val adjacencyMatrix = Array(graph.vertices.count()) { DoubleArray(graph.vertices.count()) { 0.0 } }
        val edges = graph.edges.toTypedArray()
        for (i in 0 until graph.edges.count()) {
            val source = edges[i].source.index
            val destination = edges[i].destination.index
            val weight = edges[i].weight
            adjacencyMatrix[source][destination] = weight
        }
        return adjacencyMatrix
    }

    override fun getClusters(graph: Graph<V>): IntArray {
        val adjMatrix = createAdjacencyMatrix(graph)
        //move to directed graph later
        for (i in adjMatrix.indices) {
            for (j in adjMatrix.indices) {
                if (adjMatrix[i][j] != 0.0) {
                    adjMatrix[j][i] = adjMatrix[i][j]
                }
            }
        }
        val result = louvainClustering(adjMatrix)
        var vertPartition = IntArray(graph.vertices.size)
        for ((clusterNum, cluster) in result.withIndex()) {
            for (vertex in cluster.indices) {
                vertPartition[vertex] = clusterNum
            }
        }

        return vertPartition
    }

    private fun createInitialModularityMatrix(adjMatrix: Array<DoubleArray>): Array<DoubleArray> {
        val size = adjMatrix.size
        //arrays of sums of all weights attached to vertices i, j respectively
        val k_i = DoubleArray(size) { i -> adjMatrix[i].sum() }
        val k_j = k_i.copyOf()

        val norm = 1.0 / k_i.sum()
        val K = Array(size) { i ->
            DoubleArray(size) { j ->
                norm * k_i[i] * k_j[j]
            }
        }
        val modMatrix = Array(size) { i ->
            DoubleArray(size) { j ->
                norm * (adjMatrix[i][j] - K[i][j])
            }
        }

        return modMatrix
    }

    private fun countNewModularity(modMatrix: Array<DoubleArray>, communities: List<List<Int>>): Double {
        //calculate the modularity of partition (Q)
        //matrix C here is a representation of relations between vertices.
        //if they are in one cluster, C has 1, else 0. in formula, it is Kronecker delta function
        val size = modMatrix.size
        val C = Array(size) { DoubleArray(size) }

        //construction of Kronecker delta matrix
        //communities -- список сообществ, каждое из которых само по себе список с индексами вершин
        for (community in communities) {
            for (i in community.indices) {
                for (j in i + 1 until community.size) {
                    val node1 = community[i]
                    val node2 = community[j]
                    C[node1][node2] = 1.0
                    C[node2][node1] = 1.0
                }
            }
        }

        //multiply Kronecker delta[i, j] and modularity matrix element[i, j]
        val modMatrixC = Array(size) { i ->
            DoubleArray(size) { j ->
                modMatrix[i][j] * C[i][j]
            }
        }

        //take only lower part of matrix (aka divide by 2)
        val modMatrixCLowerTriangular = Array(size) { i ->
            DoubleArray(size) { j ->
                min(modMatrixC[i][j], modMatrixC[j][i])
            }
        }

        var modularity = 0.0
        for (i in 0 until modMatrixCLowerTriangular.size) {
            modularity += modMatrixCLowerTriangular[i].sum()
            //needs testing!! and maybe it should be divided by m
        }

        return modularity
    }

    private fun copyMap(originalMap: Map<Int, List<Int>>): MutableMap<Int, MutableList<Int>> {
        val copiedMap = mutableMapOf<Int, MutableList<Int>>()

        for ((key, value) in originalMap) {
            val newList = value.toMutableList()
            copiedMap[key] = newList
        }

        return copiedMap
    }

    private fun louvainClustering(adjacencyMatrix: Array<DoubleArray>): List<List<Int>> {
        val vertToCommMap = mutableMapOf<Int, Int>()
        var commToVertMap = mutableMapOf<Int, MutableList<Int>>()
        for (i in adjacencyMatrix.indices) {
            vertToCommMap[i] = i
            val list = mutableListOf<Int>()
            list.add(i)
            commToVertMap[i] = list
        }

        val initialPartition = adjacencyMatrix.indices.map { listOf(it) }
        val modMatrix = createInitialModularityMatrix(adjacencyMatrix)
        var currentModularity = countNewModularity(adjacencyMatrix, initialPartition)

        var improved = true
        while (improved) {
            improved = false
            //рассматриваем каждую вершину с ее соседями
            for (vertex in adjacencyMatrix.indices) {
                for (neighbor in adjacencyMatrix[vertex].indices) {
                    if ((vertex != neighbor) && (adjacencyMatrix[vertex][neighbor] != 0.0)) {
                        //берем вершину i, если она и j в разных кластерах, пробуем запихнуть i в кластер j, смотрим обновление модулярности
                        val vertexCurCommunity = vertToCommMap[vertex]
                        val neighborCommunity = vertToCommMap[neighbor]
                        val newPartition = copyMap(commToVertMap)
                        if (vertexCurCommunity != neighborCommunity) {
                            newPartition[neighborCommunity]?.add(vertex)
                            newPartition[vertexCurCommunity]?.remove(vertex)
                            //превращаем мапу в список кластеров и находим новую модулярность
                            val communitiesList = mutableListOf<List<Int>>()
                            for (comm in newPartition.values) { communitiesList.add(comm) }
                            val newModularity = countNewModularity(modMatrix, communitiesList.toList())
                            //если модулярность увеличилась
                            if (newModularity > currentModularity) {
                                commToVertMap = newPartition
                                vertToCommMap[vertex] = neighborCommunity ?: throw IllegalStateException("should not be null")
                                currentModularity = newModularity
                                improved = true
                            }
                        }
                    }
                }
            }
        }
        val finalList = mutableListOf<List<Int>>()
        for (comm in commToVertMap.values) {
            if (comm.isNotEmpty()) {
                finalList.add(comm)
            }
        }
        return finalList
    }

    override fun findKeyVertices(graph: Graph<V>): List<Pair<Vertex<V>, Double>> {
        val rankingList = mutableListOf<Pair<Vertex<V>, Double>>()
        graph.vertices.forEach {
            rankingList.add(Pair(it, (calculateEHC(it, graph))))
        }
        return rankingList
    }
    private fun getDegree(vertex: Vertex<V>, graph: Graph<V>): Int {
        return graph.edges(vertex).size
    }
    private fun getGreatestDegree(graph: Graph<V>): Int {
        val listOfDegrees = mutableListOf<Int>()
        graph.vertices.forEach { listOfDegrees.add(getDegree(it, graph)) }
        val maxDegree = listOfDegrees.max()
        return maxDegree
    }
    /* |N_i(k)| -- getNumOfNeighborsWithDegree(k, v_i) is the number of neighbors of degree k of node v_i  */
    private fun getNumOfNeighborsWithDegree(k: Int, vertex: Vertex<V>, graph: Graph<V>): Int {
        var result = 0
        graph.edges(vertex).forEach {
            if (getDegree(it.destination, graph) == k) {
                result++
            }
        }
        return result
    }
    /* S_k(v_i) -- cumulativeFunctionVectorElement(k, v_i) is the value of the kth index of vector */
    private fun getCumulativeFunctionVectorElement(k: Int, vertex: Vertex<V>, graph: Graph<V>): Int {
        var cumulativeVectorElement = 0
        if (k == 1) {
            cumulativeVectorElement = getDegree(vertex, graph)
        } else if (k > 1) {
            cumulativeVectorElement =
                getCumulativeFunctionVectorElement(k - 1, vertex, graph) - getNumOfNeighborsWithDegree(k - 1, vertex, graph)
        }
        return cumulativeVectorElement
    }
    private fun cumulativeCentrality(vertex: Vertex<V>, graph: Graph<V>): Double {
        val h = getGreatestDegree(graph)
        var cmc = 0.0
        /* p and r are two tunable parameters. Based on the results in the tables of the articles,
        * the authors claim that the highest correlation over different datasets can be obtained for p = 0.8 and r = 100
        */
        val p = 0.8
        val r = 100.0
        for (k in 1..h) {
            cmc += p.pow(1.0 + k.toDouble() * p / r) * getCumulativeFunctionVectorElement(k, vertex, graph).toDouble()
        }
        return cmc
    }
    /* the extended H-index centrality (EHC) of node is determined based on the cumulative centrality of its neighbors */
    private fun calculateEHC(vertex: Vertex<V>, graph: Graph<V>): Double {
        var ehc = 0.0
        graph.edges(vertex).forEach {
            ehc += cumulativeCentrality(it.destination, graph)
        }
        return ehc
    }

    override fun getCycles(graph: Graph<V>, source: Vertex<V>): MutableList<MutableList<Int>>? {
        val adjMap: MutableMap<Int, MutableList<Int>> = HashMap()

        for (edge in graph.edges) {
            if (adjMap[edge.source.index] == null) {
                adjMap[edge.source.index] = mutableListOf()
            }
            adjMap[edge.source.index]?.add(edge.destination.index)
        }

        val cycles = mutableListOf<MutableList<Int>>()
        val color = IntArray(graph.vertices.size)
        val ancestorList = IntArray(graph.vertices.size)
        var result = detectCyclesViaDFS(source.index, source.index, color, ancestorList, cycles, adjMap)
        if (result != null) {
            result = deleteOverlappingCycles(result)
        }
        //println("result is $result")
        return result
    }

    private fun detectCyclesViaDFS(
        curVertex: Int,
        curParent: Int,
        color: IntArray,
        ancestorList: IntArray,
        cycles: MutableList<MutableList<Int>>,
        adjMap: MutableMap<Int, MutableList<Int>>) : MutableList<MutableList<Int>>? {

        if (color[curVertex] == 2) {
            return null
        }

        if (color[curVertex] == 1) {
            val detectedCycle = mutableListOf<Int>()
            var vertexToAdd = curParent
            detectedCycle.add(vertexToAdd)

            while (vertexToAdd != curVertex) {
                vertexToAdd = ancestorList[vertexToAdd]
                detectedCycle.add(vertexToAdd)
            }
            cycles.add(detectedCycle)
            return null
        }

        ancestorList[curVertex] = curParent
        color[curVertex] = 1

        val neighborList = adjMap[curVertex]
        if (neighborList != null) {
            val size = neighborList.size
            for (v in 0 until size) {
                val nextVer = neighborList[v]
                if (nextVer == ancestorList[curVertex]) {
                    continue
                }
                detectCyclesViaDFS(nextVer, curVertex, color, ancestorList, cycles, adjMap)
            }
            color[curVertex] = 2
        }
        return cycles
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

    override fun findPathWithDijkstra(graph: Graph<V>, source: Vertex<V>, sink: Vertex<V>): Pair<ArrayDeque<Int>?, Double?> {
        //createAdjacencyMatrix(graph)
        val length = graph.vertices.size
        val distances = MutableList(length) { Double.MAX_VALUE }
        val prevNode = MutableList(length) { Int.MAX_VALUE }
        distances[source.index] = 0.0
        val distinctVert = graph.vertices.asSequence().map { it.index }.toMutableList()


        while (distinctVert.isNotEmpty()) {
            val consideredVer = distinctVert.minByOrNull { distances[it] ?: 0.0 }
            distinctVert.remove(consideredVer)
            if (consideredVer == null) {
                break
            }

            //dk оставлять ли, если это раскомментить, то пути будут считаться только до указанной вершины. дает прирост во времени, но остальной список будет неправильный
            //if (consideredVer == sink.index) { break }

            //найти все ребра, исходящие из рассматриваемой вершины
            val outgoingEdges = graph.edges.filter { it.source.index == consideredVer }
            outgoingEdges.forEach {edge ->
                val consideredDestination = edge.destination
                val alternativePath = (distances[consideredVer] ?: 0.0) + (edge.weight ?: throw IllegalArgumentException("edge should have weight"))
                if (alternativePath < (distances[consideredDestination.index] ?: 0.0)) {
                    distances[consideredDestination.index] = alternativePath
                    prevNode[consideredDestination.index] = consideredVer
                }
            }
        }

        if (prevNode[sink.index] == Int.MAX_VALUE) {
            //println("vertices are not connected")
            return Pair(null, null)
        }

        val verSequence = ArrayDeque<Int>()
        verSequence.addFirst(sink.index)
        var backtrace = sink.index
        //var pathLength = 0.0
        while (backtrace != source.index) {
            verSequence.addFirst(prevNode[backtrace])
            backtrace = prevNode[backtrace]
        }
        //println("sequence is $verSequence, length is ${distances[sink.index]}")
        return Pair(verSequence, distances[sink.index])
    }
}