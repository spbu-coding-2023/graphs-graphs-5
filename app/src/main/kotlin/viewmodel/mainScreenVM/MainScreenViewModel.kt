package viewmodel.mainScreenVM

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import model.Graph
import model.Vertex
import model.algorithms.CommonAlgorithmsImpl
import view.inputs.MenuInput
import io.Neo4jRepo
import io.SqliteRepo
import model.DirectedGraph
import model.UndirectedGraph
import org.jetbrains.exposed.sql.transactions.transaction
import view.*
import view.inputs.DBInput
import viewmodel.AlgoResults
import viewmodel.placementStrategy.RepresentationStrategy
import viewmodel.graph.GraphViewModel

abstract class MainScreenViewModel<V>(
    val graph: Graph<V>,
    private val representationStrategy: RepresentationStrategy,
    val dBInput: DBInput
) {
    val showVerticesLabels = mutableStateOf(false)
    val showEdgesLabels = mutableStateOf(false)
    val graphViewModel = GraphViewModel(graph, showVerticesLabels, showEdgesLabels)
    private var neo4jRepo: Neo4jRepo<Any>? =
        if (dBInput.uri != "") Neo4jRepo(dBInput.uri, dBInput.login, dBInput.password) else null
    private val algoResults = AlgoResults()
    protected open val algorithms = CommonAlgorithmsImpl<V>()

    fun configureNeo4jRepo(input: DBInput): Pair<DirectedGraph<Any>?, String> {
        return try {
            val neoRepo = Neo4jRepo<Any>(input.uri, input.login, input.password)
            if (input.isUpdatedNeo4j) {
                neoRepo.cleanOutdatedAlgoResults()
            }
            neo4jRepo = neoRepo
            if (!input.isUndirected) {
                val inputGraph = DirectedGraph<Any>()
                val graph = neoRepo.getGraphFromNeo4j(inputGraph) as DirectedGraph<Any>
                Pair(graph, "")
            } else {
                val inputGraph = UndirectedGraph<Any>()
                val graph = neoRepo.getGraphFromNeo4j(inputGraph) as UndirectedGraph<Any>
                Pair(graph, "")
            }
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("Scheme") == true -> "${e.message} error occurred. Please check the entered URI"
                e.message?.contains("Authentication failed") == true -> "${e.message} error occurred. Please check login and password"
                else -> "An unexpected error occurred: ${e.message}"
            }
            Pair(null, errorMessage)
        }
    }

    fun configureSQLiteRepo(input: DBInput): Pair<Graph<Any>?, String> {
        val sqliteRepo = SqliteRepo<Any>(input.pathToDb)
        sqliteRepo.connectToDatabase()
        var graph: Graph<Any>? = DirectedGraph()
        transaction {
            graph = sqliteRepo.loadGraphFromDB(input.name)
        }
        if (graph == null) return Pair(null, "Graph with such name doesn't exist in this directory")
        if (input.isUpdatedSql) {
            transaction { sqliteRepo.cleanOutdatedAlgoResults(graph!!) }
        }
        return Pair(graph, "")
    }

    fun saveAlgoResults(): String {
        var message = ""
        when (dBInput.dBType) {
            "neo4j" -> {
                val repoState = neo4jRepo
                if (repoState == null) {
                    message = "nowhere to save, enter repo first"
                } else {
                    algoResults.keyVerticesResult?.let { keyVertState ->
                        repoState.saveKeyVerticesResults(graph, keyVertState)
                    }
                    algoResults.clusteringResult?.let { clusterState ->
                        repoState.saveClusterDetectionResults(graph, clusterState)
                    }
                }
                return message
            }

            "sqlite" -> {
                val repoState = SqliteRepo<Any>(dBInput.pathToDb)
                algoResults.keyVerticesResult?.let { keyVertState ->
                    repoState.saveKeyVerticesResults(graph, keyVertState)
                }
                algoResults.clusteringResult?.let { clusterState ->
                    repoState.saveClusterDetectionResults(graph, clusterState)
                }
            }

            else -> {}
        }
        return message
    }

    var scale = mutableStateOf(1f)
    var offset = mutableStateOf(DpOffset.Zero)

    init {
        representationStrategy.place(650.0, 550.0, graphViewModel)
    }

    fun resetGraphView() {
        representationStrategy.place(650.0, 550.0, graphViewModel)
        graphViewModel.vertices.forEach { v ->
            v.color = BlackAndWhite60
            v.radius = 20.dp
        }
        graphViewModel.edges.forEach { e ->
            e.color = BlackAndWhite30
        }
    }

    fun clearChanges() {
        graphViewModel.vertices.forEach { v ->
            v.color = BlackAndWhite60
            v.radius = 20.dp
        }
        graphViewModel.edges.forEach { e ->
            e.color = BlackAndWhite30
        }
    }

    open fun getListOfAlgorithms(): List<String> {
        return listOf("Graph clustering", "Key vertices", "Cycles", "Min path (Dijkstra)")
    }

    open fun getVertexByIndex(index: Int): Vertex<V>? {
        val vertList = graph.vertices.toList()
        var result: Vertex<V>? = null
        for (i in vertList.indices) {
            if (vertList[i].dBIndex == index) {
                result = vertList[i]
            }
        }
        return result
    }

    abstract fun run(input: MenuInput): String

    fun loadKeyVerticesResults(): List<Double> {
        var rankingList = mutableListOf<Double>()
        when (dBInput.dBType) {
            "neo4j" -> {
                if (!dBInput.isUpdatedNeo4j) {
                    val repo = Neo4jRepo<Any>(dBInput.uri, dBInput.login, dBInput.password)
                    val list = repo.getKeyVerticesResults()
                    if (list == null) {
                        rankingList = mutableListOf()
                        algorithms.findKeyVertices(graph).forEach { v ->
                            val vertexRank = v.second
                            rankingList.add(vertexRank)
                        }
                    }
                    else { rankingList = list.toMutableList() }

                } else {
                    algorithms.findKeyVertices(graph).forEach { v ->
                        val vertexRank = v.second
                        rankingList.add(vertexRank)
                    }
                }
            }
            "sqlite" -> {
                if (!dBInput.isUpdatedSql) {
                    val repo = SqliteRepo<Any>(dBInput.pathToDb)
                    transaction {
                        rankingList = repo.loadKeyVerticesResults(dBInput.name)
                            ?: return@transaction "Graph should exist in database"
                    }
                    if (rankingList.max() == 0.0) {
                        rankingList = mutableListOf()
                        algorithms.findKeyVertices(graph).forEach { v ->
                            val vertexRank = v.second
                            rankingList.add(vertexRank)
                        }
                    }
                } else {
                    algorithms.findKeyVertices(graph).forEach { v ->
                        val vertexRank = v.second
                        rankingList.add(vertexRank)
                    }
                    println("new")
                }
            }
        }
        return rankingList
    }

    protected fun highlightKeyVertices() {
        clearChanges()
        val rankingList = loadKeyVerticesResults()
        algoResults.keyVerticesResult = rankingList.toList()
        val maxRank = rankingList.max()
        var i = 0
        graphViewModel.vertices.forEach { v ->
            val relativeRank = rankingList[i] / maxRank
            val radius = when {
                relativeRank > 0.8 -> 32
                relativeRank > 0.6 -> 29
                relativeRank > 0.4 -> 26
                relativeRank > 0.2 -> 23
                else -> 20
            }
            v.radius = radius.dp
            val color = when {
                relativeRank > 0.8 -> BlackAndWhite20
                relativeRank > 0.6 -> BlackAndWhite35
                relativeRank > 0.4 -> BlackAndWhite50
                relativeRank > 0.2 -> BlackAndWhite65
                else -> BlackAndWhite80
            }
            v.color = color
            i++
            graphViewModel.edges.forEach { e ->
                e.color = BlackAndWhite30
            }
        }
    }

    private fun loadClusterDetectionResults(): IntArray {
        var result = IntArray(graph.vertices.size)
        when (dBInput.dBType) {
            "neo4j" -> {
                if (!dBInput.isUpdatedNeo4j) {
                    val repo = Neo4jRepo<Any>(dBInput.uri, dBInput.login, dBInput.password)
                    val partition = repo.getClusteringResults()
                    if (partition == null) {
                        result = algorithms.getClusters(graph)
                    }
                    else { result = partition.toIntArray() }
                } else {
                    result = algorithms.getClusters(graph)
                }
            }

            "sqlite" -> {
                if (!dBInput.isUpdatedSql) {
                    val repo = SqliteRepo<Any>(dBInput.pathToDb)
                    transaction {
                        result = repo.loadClusteringResults(dBInput.name)?.toIntArray()
                            ?: return@transaction "Graph should exist in database"
                    }
                    println("load")
                    if (result.max() == 0) {
                        result = algorithms.getClusters(graph)
                        println("empty list")
                    }
                } else {
                    result = algorithms.getClusters(graph)
                    println("new")
                }
            }
        }
        return result
    }

    fun divideIntoClusters() {
        clearChanges()
        val result = loadClusterDetectionResults()
        algoResults.clusteringResult = result
        graphViewModel.vertices.forEach { v ->
            val vertClusterNum = result[v.vertex.index]
            val color = when {
                vertClusterNum % 10 == 0 -> ComponentColorNavy
                vertClusterNum % 10 == 1 -> ComponentColorOrange
                vertClusterNum % 10 == 2 -> ComponentColorPurple
                vertClusterNum % 10 == 3 -> ComponentColorLavender
                vertClusterNum % 10 == 4 -> ComponentColorBlue
                vertClusterNum % 10 == 5 -> ComponentColorWater
                vertClusterNum % 10 == 6 -> ComponentColorPink
                vertClusterNum % 10 == 7 -> ComponentColorSmoke
                vertClusterNum % 10 == 8 -> ComponentColorBurdundy
                else -> ComponentColorRed
            }
            v.color = color
        }
    }

    fun highlightPathDijkstra(source: Vertex<V>, sink: Vertex<V>): String {
        clearChanges()
        val (algoMessage, pathInfo) = algorithms.findPathWithDijkstra(graph, source, sink)
        if (pathInfo.first == null) {
            return algoMessage
        } else {
            val path: ArrayDeque<Int> = pathInfo.first ?: throw IllegalArgumentException("should not be null")
            graphViewModel.vertices.forEach { v ->
                if (path.contains(v.vertex.index)) {
                    v.color = ComponentColorNavy
                }
            }
            graphViewModel.edges.forEach { e ->
                if (path.contains(e.u.vertex.index) && path.contains(e.v.vertex.index) &&
                    (path.indexOf(e.u.vertex.index)
                        .let { path.indexOf(e.v.vertex.index).minus(it) }) == 1 || (path.indexOf(e.u.vertex.index)
                        .let { path.indexOf(e.v.vertex.index).minus(it) }) == -1
                ) {
                    e.color = ComponentColorNavy
                }
                if (path.indexOf(e.u.vertex.index) == 1 && path.indexOf(e.v.vertex.index) == path.size) {
                    e.color = ComponentColorNavy
                }
            }
        }
        return ""
    }
}
