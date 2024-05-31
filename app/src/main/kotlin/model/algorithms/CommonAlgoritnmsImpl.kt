package model.algorithms

import model.Graph
import model.Vertex
import kotlin.math.min
import kotlin.math.pow

open class CommonAlgorithmsImpl<V> : CommonAlgorithms<V> {

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
        for (i in adjMatrix.indices) {
            for (j in adjMatrix.indices) {
                if (adjMatrix[i][j] != 0.0) {
                    adjMatrix[j][i] = adjMatrix[i][j]
                }
            }
        }
        val result = louvainClustering(adjMatrix)
        val vertPartition = IntArray(graph.vertices.size)
        for ((clusterNum, cluster) in result.withIndex()) {
            for (vertex in cluster) {
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
                            for (comm in newPartition.values) {
                                communitiesList.add(comm)
                            }
                            val newModularity = countNewModularity(modMatrix, communitiesList.toList())
                            //если модулярность увеличилась
                            if (newModularity > currentModularity) {
                                commToVertMap = newPartition
                                vertToCommMap[vertex] =
                                    neighborCommunity ?: throw IllegalStateException("should not be null")
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
                getCumulativeFunctionVectorElement(k - 1, vertex, graph) - getNumOfNeighborsWithDegree(
                    k - 1,
                    vertex,
                    graph
                )
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
            ehc += cumulativeCentrality(it.destination, graph) + cumulativeCentrality(vertex, graph)
        }
        return ehc
    }

    override fun findPathWithDijkstra(
        graph: Graph<V>,
        source: Vertex<V>,
        sink: Vertex<V>
    ): Pair<String, Pair<ArrayDeque<Int>?, Double?>> {
        val length = graph.vertices.size
        val distances = MutableList(length) { Double.MAX_VALUE }
        val prevNode = MutableList(length) { Int.MAX_VALUE }
        distances[source.index] = 0.0
        val distinctVert = graph.vertices.asSequence().map { it.index }.toMutableList()

        while (distinctVert.isNotEmpty()) {
            val consideredVer = distinctVert.minByOrNull { distances[it] }
            distinctVert.remove(consideredVer)
            if (consideredVer == null) {
                break
            }
            val outgoingEdges = graph.edges.filter { it.source.index == consideredVer }
            outgoingEdges.forEach { edge ->
                val consideredDestination = edge.destination
                val alternativePath = (distances[consideredVer]) + (edge.weight)
                if ((alternativePath < (distances[consideredDestination.index])) || (source == sink)) {
                    distances[consideredDestination.index] = alternativePath
                    prevNode[consideredDestination.index] = consideredVer
                }
            }
        }

        var message = ""
        if (prevNode[sink.index] == Int.MAX_VALUE) {
            message = "Vertices are unattainable"
            return Pair(message, Pair(null, null))
        }

        val verSequence = ArrayDeque<Int>()
        verSequence.addFirst(sink.index)
        var backtrace = sink.index
        while (backtrace != source.index) {
            verSequence.addFirst(prevNode[backtrace])
            backtrace = prevNode[backtrace]
        }
        return Pair(message, Pair(verSequence, distances[sink.index]))
    }
}