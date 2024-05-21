package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import model.*
import model.algorithms.DirectedGraphAlgorithmsImpl
import model.algorithms.UndirectedGraphAlgorithmsImpl
import model.algorithms.CommonAlgorithms
import model.UndirectedGraph
import model.DirectedGraph
import model.Graph
import model.Vertex
import model.algorithms.CommonAlgorithmsImpl
import view.*
import view.menuInput

abstract class MainScreenViewModel<V>(
    val graph: Graph<V>,
    private val representationStrategy: RepresentationStrategy
) {
    val showVerticesLabels = mutableStateOf(false)
    val showEdgesLabels = mutableStateOf(false)
    val graphViewModel = GraphViewModel(graph, showVerticesLabels, showEdgesLabels)
    init {
        representationStrategy.place(650.0, 550.0, graphViewModel.vertices)
    }
    fun resetGraphView() {
        representationStrategy.place(650.0, 550.0, graphViewModel.vertices)
        graphViewModel.vertices.forEach{ v ->
            v.color = BlackAndWhite60
            v.radius = 20.dp
        }
    }
    open fun getListOfAlgorithms(): List<String> {
        return listOf("Graph Clustering", "Key vertices", "Cycles", "Min path (Dijkstra)")
    }

    //should be protected?
    open fun getVertexByIndex(index: Int): Vertex<V>? {
        val vertList = graph.vertices.toList()
        val result = vertList.getOrNull(index)
        return result
    }
    protected open val algorithms = CommonAlgorithmsImpl<V>()

    fun run(input: menuInput): String {
        //println("num is $num")
        var message = ""
        when {
            input.algoNum == 1 -> highlightKeyVertices()
            input.algoNum == 2 -> {
                val vertex = getVertexByIndex(input.inputValueOneVertex.toInt())
                if (vertex != null) {
                    resetGraphView()
                    message = highlightCycles(vertex)
                }
                else {
                    message = "Vertex with that index does not exist"
                }
            }
            else -> {
                resetGraphView()
            }
        }
        return message
    }

    protected fun highlightKeyVertices() {
        val rankingList = mutableListOf<Double>()
        algorithms.findKeyVertices(graph).forEach{ v ->
            val vertexRank = v.second
            rankingList.add(vertexRank)
        }
        val maxRank = rankingList.max()
        var i = 0
        graphViewModel.vertices.forEach{ v ->
            val relativeRank = rankingList[i]/maxRank
            val radius = when {
                relativeRank > 0.8 -> 34
                relativeRank > 0.6 -> 30
                relativeRank > 0.4 -> 26
                relativeRank > 0.2 -> 22
                else -> 18
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
        }
    }
    
    abstract fun run(num: Int): String

    private fun divideIntoClusters() {
        TODO()
    }

    fun highlightCycles(source: Vertex<V>): String {
        val cycles = algorithms.getCycles(graph, source)
        var message = ""
        if (cycles.isNullOrEmpty()) {
            message = "No cycles for $source detected"
        }
        else {
            //проверить, что он один
            val cycle = cycles[0]
            graphViewModel.vertices.forEach{v ->
                if (cycle.contains(v.vertex.index)) {
                    v.color = BlackAndWhite20
                }
            }
        }
        return message
    }

    fun highlightPathDijkstra(source: Vertex<V>, sink: Vertex<V>): Pair<ArrayDeque<Int>?, Double?> {
        val path = algorithms.findPathWithDijkstra(graph, source, sink)
        return path
    }

    //take out from view model later
    fun createAdjMatrix(): Array<DoubleArray> {
        val matrix = algorithms.createAdjacencyMatrix(graph)
        return matrix
    }
}

class DGScreenViewModel<V>(
    graph: Graph<V>,
    representationStrategy: RepresentationStrategy
) : MainScreenViewModel<V>(graph, representationStrategy) {
    override val algorithms = DirectedGraphAlgorithmsImpl<V>()
    private val graph2 = graph
    override fun run(num: Int): String {
        var message = ""
        when {
            num == 1 -> highlightKeyVertices()
            num == 3 -> findStrongComponents()
            else -> {
                resetGraphView()
            }
        }
        return message
    }
    private fun findStrongComponents() {
        val componentsList = algorithms.findStrongComponents(graph2)
//        println("pipipipipi")
//        componentsList.forEach { v ->
//            println("$v")
//        }
        val relativeList = mutableListOf<Int>()
        componentsList.forEach {
            relativeList.add(it.second)
        }
//        println("hhhhhiiii")
//        relativeList.forEach {
//            print("$it ")
//        }
        val vertexVMMap= hashMapOf<VertexViewModel<V>, Int>()
        var i = 0
//        println("")
        graphViewModel.vertices.forEach{ v ->
//            print("${v.vertex} ")
            vertexVMMap[v] = relativeList[i]
            val radius = 18 + relativeList[i] % 10
            v.radius = radius.dp
            val color = when {
                relativeList[i] % 10 == 0 -> ComponentColorNavy
                relativeList[i] % 10 == 1 -> ComponentColorOrange
                relativeList[i] % 10 == 2 -> ComponentColorPurple
                relativeList[i] % 10 == 3 -> ComponentColorLavender
                relativeList[i] % 10 == 4 -> ComponentColorBlue
                relativeList[i] % 10 == 5 -> ComponentColorWater
                relativeList[i] % 10 == 6 -> ComponentColorPink
                relativeList[i] % 10 == 7 -> ComponentColorSmoke
                relativeList[i] % 10 == 8 -> ComponentColorBurdundy
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
    override fun getListOfAlgorithms(): List<String> {
        return listOf("Graph Clustering", "Key vertices", "Cycles", "Strong Components",
            "Min path (Dijkstra)", "Min path (Ford-Bellman)")
    }

}

class UGScreenViewModel<V>(
    graph: Graph<V>,
    representationStrategy: RepresentationStrategy
) : MainScreenViewModel<V>(graph, representationStrategy) {
    override val algorithms = UndirectedGraphAlgorithmsImpl<V>()
    override fun run(num: Int): String {
        TODO("Not yet implemented")
    }

    override fun getListOfAlgorithms(): List<String> {
        return listOf("Graph Clustering", "Key vertices", "Cycles", "Min tree", "Bridges",
            "Min path (Dijkstra)")
    }
    private fun findBridges() {
        TODO()
    }

    private fun findCore() {
        TODO()
    }
}

//object ScreenVM {
//    fun <V> createView(graph: Graph<V>): MainScreenViewModel<V> {
//        return when (graph.graphType) {
//            GraphType.DIRECTED ->
//                DGScreenViewModel(graph, CircularPlacementStrategy())
//            else -> UGScreenViewModel(graph, CircularPlacementStrategy())
//        }
//    }
//}

//class MainScreenViewModel<V>(graph: Graph<V>, private val representationStrategy: RepresentationStrategy) {
//    val showVerticesLabels = mutableStateOf(false)
//    val showEdgesLabels = mutableStateOf(false)
//    val graphViewModel = GraphViewModel(graph, showVerticesLabels, showEdgesLabels)
//    init {
//        representationStrategy.place(650.0, 550.0, graphViewModel.vertices)
//    }
//    fun resetGraphView() {
//        representationStrategy.place(650.0, 550.0, graphViewModel.vertices)
//        graphViewModel.vertices.forEach{ v ->
//            v.color = BlackAndWhite70
//            v.radius = 20.dp
//        }
//    }
//    fun run(num: Int): String {
//        var message = ""
//        when {
//            num == 1 -> highlightKeyVertices()
//            else -> {
//                resetGraphView()
//            }
//        }
//        return message
//    }

//    private fun highlightKeyVertices() {
//        val rankingList = mutableListOf<Double>()
//        graphViewModel.rankingListOfVertices.forEach{ v ->
//            val vertexRank = v.second
//            rankingList.add(vertexRank)
//        }
//        val maxRank = rankingList.max()
//        var i = 0
//        graphViewModel.vertices.forEach{ v ->
//            val relativeRank = rankingList[i]/maxRank
//            val radius = when {
//                relativeRank > 0.8 -> 36
//                relativeRank > 0.6 -> 32
//                relativeRank > 0.4 -> 26
//                relativeRank > 0.2 -> 20
//                else -> 14
//            }
//            v.radius = radius.dp
//            val color = when {
//                relativeRank > 0.8 -> BlackAndWhite20
//                relativeRank > 0.6 -> BlackAndWhite35
//                relativeRank > 0.4 -> BlackAndWhite50
//                relativeRank > 0.2 -> BlackAndWhite65
//                else -> BlackAndWhite80
//            }
//            v.color = color
//            i++
//        }
//    }
//}