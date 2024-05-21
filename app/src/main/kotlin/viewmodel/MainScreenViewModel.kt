package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import model.algorithms.DirectedGraphAlgorithmsImpl
import model.algorithms.UndirectedGraphAlgorithmsImpl
import model.UndirectedGraph
import model.DirectedGraph
import model.Graph
import model.Vertex
import model.algorithms.CommonAlgorithmsImpl
import view.*
import view.menuInput

open class MainScreenViewModel<V>(
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
                    highlightCycles(vertex)
                }
                else {
                    message = "vertex does not exist"
                }
            }
            else -> {
                resetGraphView()
            }
        }
        return message
    }

    private fun highlightKeyVertices() {
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
                relativeRank > 0.8 -> 36
                relativeRank > 0.6 -> 32
                relativeRank > 0.4 -> 26
                relativeRank > 0.2 -> 20
                else -> 14
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

    private fun divideIntoClusters() {
        TODO()
    }

    fun highlightCycles(source: Vertex<V>): String {
        val cycles = algorithms.getCycles(graph, source)
        var message = ""
        if (cycles.isNullOrEmpty()) {
            message = "no cycles for $source"
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
    val graph2 = graph
    private fun findStrongComponents() {
        val componentsList = algorithms.findStrongComponents(graph2)
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