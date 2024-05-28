package viewmodel

//import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.DpOffset
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import model.algorithms.DirectedGraphAlgorithmsImpl
import model.algorithms.UndirectedGraphAlgorithmsImpl
import model.Graph
import model.Vertex
import model.algorithms.CommonAlgorithmsImpl
import view.*
import view.MenuInput
import io.Neo4jRepo
import io.SqliteRepo
import model.DirectedGraph
import model.UndirectedGraph
import org.jetbrains.exposed.sql.transactions.transaction


abstract class MainScreenViewModel<V>(
    val graph: Graph<V>,
    private val representationStrategy: RepresentationStrategy,
    val DBinput: DBInput
) {
    val showVerticesLabels = mutableStateOf(false)
    val showEdgesLabels = mutableStateOf(false)
    val graphViewModel = GraphViewModel(graph, showVerticesLabels, showEdgesLabels)
    var neo4jRepo: Neo4jRepo<Any>? = if (DBinput.uri != "") Neo4jRepo(DBinput.uri, DBinput.login, DBinput.password) else null
//    val sqliteRepo: SqliteRepo<Any>? = if (DBinput.dBType == "sqlite") SqliteRepo(DBinput.pathToDb) else null
    private val algoResults = AlgoResults()

    //val neoRepo = Neo4jRepo<Any>("bolt://localhost:7687","neo4j", "my my, i think we have a spy ;)")
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
                e.message?.contains("Scheme") == true -> "${e.message} error occured. Please check the entered URI"
                e.message?.contains("Authentication failed") == true -> "${e.message} error occured. Please check login and password"
                else -> "An unexpected error occurred: ${e.message}"
            }
            //println(errorMessage)
            Pair(null, errorMessage)
        }
    }
    fun configureSQLiteRepo(input: DBInput): Pair<Graph<Any>?, String> {
        val sqliteRepo = SqliteRepo<Any>(input.pathToDb)
        sqliteRepo.connectToDatabase()
        var graph : Graph<Any>? = DirectedGraph()
        transaction {
            graph = sqliteRepo.loadGraphFromDB(input.name)
        }
        if (graph == null) return Pair(null, "Graph with such name doesn't exist in this directory")
        return Pair(graph, "")
    }
    fun saveAlgoResults(): String {
        var message = ""
        when (DBinput.dBType) {
            "neo4j" -> {
                val repoState = neo4jRepo
                if (repoState == null) {
                    message = "nowhere to save, enter repo first"
                }
                else {
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
                val repoState = SqliteRepo<Any>(DBinput.pathToDb)
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
        representationStrategy.place(650.0, 550.0, graphViewModel.vertices)
    }
    fun resetGraphView() {
        representationStrategy.place(650.0, 550.0, graphViewModel.vertices)
        graphViewModel.vertices.forEach{ v ->
            v.color = BlackAndWhite60
            v.radius = 20.dp
        }
        graphViewModel.edges.forEach { e ->
            e.color = BlackAndWhite30
        }
    }
    fun clearChanges() {
        graphViewModel.vertices.forEach{ v ->
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

    protected open val algorithms = CommonAlgorithmsImpl<V>()

//    fun run(input: MenuInput): String {
//        //println("num is $num")
//        var message = ""
//        when {
//            input.algoNum == 1 -> highlightKeyVertices()
//            input.algoNum == 2 -> {
//                val vertex = getVertexByIndex(input.inputValueOneVertex.toInt())
//                if (vertex != null) {
//                    resetGraphView()
//                    message = highlightCycles(vertex)
//                }
//                else {
//                    message = "Vertex with that index does not exist"
//                }
//            }
//            else -> {
//                resetGraphView()
//            }
//        }
//        return message
//    }

    protected fun highlightKeyVertices() {
        clearChanges()
        //println(neoRepo.getKeyVerticesResults())
        val rankingList = mutableListOf<Double>()
        algorithms.findKeyVertices(graph).forEach{ v ->
            val vertexRank = v.second
            rankingList.add(vertexRank)
//            println(vertexRank)
        }
        //neoRepo.saveKeyVerticesResults(graph, rankingList)
        algoResults.keyVerticesResult = rankingList.toList()
        val maxRank = rankingList.max()
        var i = 0
        graphViewModel.vertices.forEach{ v ->
            val relativeRank = rankingList[i]/maxRank
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

    abstract fun run(input: MenuInput): String

    fun divideIntoClusters() {
        clearChanges()
        //println(neoRepo.getClusteringResults())
        val result = algorithms.getClusters(graph)
        algoResults.clusteringResult = result
        //neoRepo.saveClusterDetectionResults(graph, result)
        graphViewModel.vertices.forEach {v ->
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
        //val path = algorithms.findPathWithDijkstra(graph, source, sink)
        val (algoMessage, pathInfo) = algorithms.findPathWithDijkstra(graph, source, sink)
        if (pathInfo.first == null) {
            return algoMessage
        }
        else {
            val path: ArrayDeque<Int> = pathInfo.first ?: throw IllegalArgumentException("should not be null")
            graphViewModel.vertices.forEach{v ->
                if (path.contains(v.vertex.index)) {
                    v.color = ComponentColorNavy
                }
            }
            graphViewModel.edges.forEach { e ->
                if (path.contains(e.u.vertex.index) && path.contains(e.v.vertex.index) &&
                    (path.indexOf(e.u.vertex.index).let { path.indexOf(e.v.vertex.index).minus(it) }) == 1 || (path.indexOf(e.u.vertex.index).let { path.indexOf(e.v.vertex.index).minus(it) }) == -1) {
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

class DGScreenViewModel<V>(
    graph: Graph<V>,
    representationStrategy: RepresentationStrategy,
    DBinput: DBInput
) : MainScreenViewModel<V>(graph, representationStrategy, DBinput) {
    override val algorithms = DirectedGraphAlgorithmsImpl<V>()
    private val graph2 = graph
    override fun run(input: MenuInput): String {
        var message = ""
        when {
            input.text == "Graph clustering" -> divideIntoClusters()
            input.text == "Key vertices" -> highlightKeyVertices()
            input.text == "Cycles" -> {
                val vertex = getVertexByIndex(input.inputValueOneVertex.toInt())
                if (vertex != null) {
                    message = highlightCycles(vertex)
                }
                else {
                    message = "Error: vertex with this index doesn't exist"
                }
            }
            input.text == "Strong components" -> findStrongComponents()
            input.text == "Min path (Dijkstra)" -> {
                //negative weights check
                for (edge in graph.edges) {
                    if (edge.weight < 0.0) {
                        message = "Error: Dijkstra's algorithm does not work on a negative weighted graphs"
                        return message
                    }
                }
                val source = getVertexByIndex(input.inputStartTwoVer.toInt())
                val destination = getVertexByIndex(input.inputEndTwoVer.toInt())
                if(source == null || destination == null) message = "Error: vertex with this index doesn't exist"
                else {
                    message = highlightPathDijkstra(source, destination)
                }
            }
            input.text == "Min path (Ford-Bellman)" -> {
                val source = getVertexByIndex(input.inputStartTwoVer.toInt())
                val destination = getVertexByIndex(input.inputEndTwoVer.toInt())
                if(source == null || destination == null) message = "Error: vertex with this index doesn't exist"
                else message = findSPwFB(source, destination)
            }
            else -> {
                resetGraphView()
            }
        }
        return message
    }

    private fun highlightCycles(source: Vertex<V>): String {
        clearChanges()
        val cycle = algorithms.getCycles(graph, source)
        var message = ""
        if (cycle.isNullOrEmpty()) {
            message = "No cycles for $source detected"
        }
        else {
            //проверить, что он один
            graphViewModel.vertices.forEach{v ->
                if (cycle.contains(v.vertex.index)) {
                    v.color = ComponentColorNavy
                }
            }
            graphViewModel.edges.forEach { e ->
                if (cycle.contains(e.u.vertex.index) && cycle.contains(e.v.vertex.index)) {
                    e.color = ComponentColorNavy
                }
//                if (cycle.indexOf(e.u.vertex.index) == 1 && cycle.indexOf(e.v.vertex.index) == cycle.size) {
//                    e.color = ComponentColorNavy
//                }
            }
        }
        return message
    }
    private fun findStrongComponents() {
        clearChanges()
        val componentsList = algorithms.findStrongComponents(graph2)
        val componentNumList = mutableListOf<Int>()
        componentsList.forEach {
            componentNumList.add(it.second)
        }
        val vertexVMMap= hashMapOf<VertexViewModel<V>, Int>()
        var i = 0
        graphViewModel.vertices.forEach{ v ->
            vertexVMMap[v] = componentNumList[i]
            val radius = 18 + componentNumList[i] % 10
            v.radius = radius.dp
            val color = when {
                componentNumList[i] % 10 == 0 -> ComponentColorNavy
                componentNumList[i] % 10 == 1 -> ComponentColorOrange
                componentNumList[i] % 10 == 2 -> ComponentColorPurple
                componentNumList[i] % 10 == 3 -> ComponentColorLavender
                componentNumList[i] % 10 == 4 -> ComponentColorBlue
                componentNumList[i] % 10 == 5 -> ComponentColorWater
                componentNumList[i] % 10 == 6 -> ComponentColorPink
                componentNumList[i] % 10 == 7 -> ComponentColorSmoke
                componentNumList[i] % 10 == 8 -> ComponentColorBurdundy
                else -> ComponentColorRed
            }
            v.color = color
            i++
        }
        graphViewModel.edges.forEach { e ->
            if(vertexVMMap[e.v] == vertexVMMap[e.u]) {
                e.color = e.v.color
            }
        }
    }
    private fun findSPwFB(source: Vertex<V>, destination: Vertex<V>): String {
        clearChanges()
        var message = ""
        val list = algorithms.findPathWithFordBellman(source, destination, graph2)
            ?: return  "Vertex ${destination.dBIndex} is unattainable from vertex ${source.dBIndex}"
        if (list[0] == list[list.size - 1]) message = "Negative-weight cycle detected"
        val vertexMap = hashMapOf<Vertex<V>, Int>()
        var i = 0
        list.forEach {
            vertexMap[it] = i
            i++
        }
        val path = hashMapOf<VertexViewModel<V>, Int?>()
        graphViewModel.vertices.forEach {v ->
            if(vertexMap.contains(v.vertex)) {
                v.color = ComponentColorNavy
                path[v] = vertexMap[v.vertex]
            }
        }
        graphViewModel.edges.forEach { e ->
            if (path.contains(e.u) && path.contains(e.v) &&
                (path[e.u]?.let { path[e.v]?.minus(it) }) == 1 || (path[e.u]?.let { path[e.v]?.minus(it) }) == -1) {
                e.color = ComponentColorNavy
            }
            if (path[e.u] == 1 && path[e.v] == path.size) {
                e.color = ComponentColorNavy
            }
        }
        return message
    }
    override fun getListOfAlgorithms(): List<String> {
        return listOf("Graph clustering", "Key vertices", "Cycles", "Strong components",
            "Min path (Dijkstra)", "Min path (Ford-Bellman)")
    }

}

class UGScreenViewModel<V>(
    graph: Graph<V>,
    representationStrategy: RepresentationStrategy,
    DBinput: DBInput
) : MainScreenViewModel<V>(graph, representationStrategy, DBinput) {
    override val algorithms = UndirectedGraphAlgorithmsImpl<V>()
    override fun run(input: MenuInput): String {
        var message = ""
        when {
            input.text == "Key vertices" -> highlightKeyVertices()
//            input.text == "Cycles" -> {
//                val vertex = getVertexByIndex(input.inputValueOneVertex.toInt())
//                if (vertex != null) {
//                    resetGraphView()
//                    message = highlightCycles(vertex)
//                }
//                else {
//                    message = "Index out of bounds, maximum value is ${graph.vertices.size - 1}"
//                }
//            }
            else -> {
                resetGraphView()
            }
        }
        return message
    }

    override fun getListOfAlgorithms(): List<String> {
        return listOf("Graph clustering", "Key vertices", "Min tree", "Bridges",
            "Min path (Dijkstra)")
    }
    private fun findBridges() {
        TODO()
    }

    private fun findCore() {
        TODO()
    }
}