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
    protected open val algorithms = CommonAlgorithmsImpl<V>()
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
    fun run(num: Int): String {
        println("num is $num")
        var message = ""
        when {
            num == 1 -> highlightKeyVertices()
            else -> {
                resetGraphView()
            }
        }
        return message
    }

    private fun divideIntoClusters() {
        TODO()
    }

    fun highlightCycles(source: Vertex<V>): MutableList<MutableList<Int>>? {
        val cycles = algorithms.getCycles(graph, source)
        return cycles
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

//class ViewModelFactory<V>(
//    val graph: Graph<V>,
//    val representationStrategy: RepresentationStrategy
//) {
////    val graphType = graph.graphType
//    fun createViewModel(graphType: GraphType) : MainScreenViewModel<V> {
//        return when (graphType) {
//            GraphType.DIRECTED -> DGScreenViewModel(graph, representationStrategy)
//            else -> UGScreenViewModel(graph, representationStrategy)
//        }
//    }
//}

class DGScreenViewModel<V>(
    graph: DirectedGraph<V>,
    representationStrategy: RepresentationStrategy
) : MainScreenViewModel<V>(graph, representationStrategy) {
    override val algorithms = DirectedGraphAlgorithmsImpl<V>()
    val graph2 = graph
    private fun findStrongComponents() {
        val componentsList = algorithms.findStrongComponents(graph2)

    }
}

class UGScreenViewModel<V>(
    graph: UndirectedGraph<V>,
    representationStrategy: RepresentationStrategy
) : MainScreenViewModel<V>(graph, representationStrategy) {
    override val algorithms = UndirectedGraphAlgorithmsImpl<V>()
    private fun findBridges() {
        TODO()
    }

    private fun findCore() {
        TODO()
    }
}

fun main() {
    val graph = UndirectedGraph<Int>()

    val  Alabama = graph.addVertex(1)
    val  Arizona = graph.addVertex(2)
    val  California = graph.addVertex(3)
    val  Connecticut = graph.addVertex(4)
    val  Florida = graph.addVertex(5)
    val  Hawaii = graph.addVertex(6)
    val  Illinois = graph.addVertex(7)
    val  Iowa = graph.addVertex(8)

    val edge1 = graph.addEdge(Alabama, Arizona) //1 2
    val edge2 = graph.addEdge(Alabama, California) //1 3
    val edge3 = graph.addEdge(Alabama, Connecticut) //1 4
    val edge4 = graph.addEdge(Arizona, California) //2 3
    val edge5 = graph.addEdge(Arizona, Connecticut) //2 4
    val edge6 = graph.addEdge(California, Connecticut) //3 4
    val edge7 = graph.addEdge(Connecticut, Florida) //4 5

    val edge8 = graph.addEdge(Hawaii, Illinois)
    val edge9 = graph.addEdge(Hawaii, Iowa)
    val edge10 = graph.addEdge(Iowa, Illinois)

    val VM = UGScreenViewModel(graph, CircularPlacementStrategy())
//    val adjM = VM.createAdjMatrix()
//    for (i in adjM.indices) {
//        for (j in adjM[i].indices) {
//            print(" ${adjM[i][j]} |")
//        }
//    }

    //val path = VM.highlightPath(Alabama, Alabama)
    //println(path)

    val cycles = VM.highlightCycles(Hawaii)
    println(cycles)


}


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