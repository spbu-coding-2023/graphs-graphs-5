package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.algorithms.DirectedGraphAlgorithmsImpl
import model.algorithms.UndirectedGraphAlgorithmsImpl
import model.UndirectedGraph
import model.DirectedGraph
import model.Graph
import view.*

abstract class CommonScreenViewModel<V> (graph: Graph<V>, private val representationStrategy: RepresentationStrategy){
    val showVerticesLabels = mutableStateOf(false)
    val showEdgesLabels = mutableStateOf(false)
    val graphViewModel = GraphViewModel(graph, showVerticesLabels, showEdgesLabels)
    fun resetGraphView() {
        representationStrategy.place(650.0, 550.0, graphViewModel.vertices)
        graphViewModel.vertices.forEach{ v ->
            v.color = BlackAndWhite70
            v.radius = 20.dp
        }
    }

    fun run(num: Int): String {
        var message = ""
        when {
            num == 1 -> highlightKeyVertices()
            else -> {
                resetGraphView()
            }
        }
        return message
    }

    private fun highlightKeyVertices() {
        val rankingList = mutableListOf<Double>()
        graphViewModel.rankingListOfVertices.forEach{ v ->
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

    private fun highlightCycles() {
        TODO()
    }

    private fun highlightPath() {
        TODO()
    }
}

class DirectedSreenView<V>(graph: DirectedGraph<V>, representationStrategy: RepresentationStrategy) : CommonScreenViewModel<V> (graph, representationStrategy) {
    private fun findStrongComponents() {
        TODO()
    }
}

class UndirectedSreenView<V>(graph: UndirectedGraph<V>, representationStrategy: RepresentationStrategy) : CommonScreenViewModel<V> (graph, representationStrategy) {
    private fun findBridges() {
        TODO()
    }

    private fun findCore() {
        TODO()
    }
}


class MainScreenViewModel<V>(graph: Graph<V>, private val representationStrategy: RepresentationStrategy) {
    val showVerticesLabels = mutableStateOf(false)
    val showEdgesLabels = mutableStateOf(false)
    val graphViewModel = GraphViewModel(graph, showVerticesLabels, showEdgesLabels)
    init {
        representationStrategy.place(650.0, 550.0, graphViewModel.vertices)
    }
    fun resetGraphView() {
        representationStrategy.place(650.0, 550.0, graphViewModel.vertices)
        graphViewModel.vertices.forEach{ v ->
            v.color = BlackAndWhite70
            v.radius = 20.dp
        }
    }
    fun run(num: Int): String {
        var message = ""
        when {
            num == 1 -> highlightKeyVertices()
            else -> {
                resetGraphView()
            }
        }
        return message
    }

    private fun highlightKeyVertices() {
        val rankingList = mutableListOf<Double>()
        graphViewModel.rankingListOfVertices.forEach{ v ->
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
}