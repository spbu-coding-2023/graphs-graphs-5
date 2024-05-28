/*
 * This file is part of our project Graphs-5.
 *
 * Our project Graphs-5 uses the Gephi Toolkit library, which is licensed under the GNU General Public License v3.0.
 * See <https://www.gnu.org/licenses/> for more information.
 */

package model.algorithms


import androidx.compose.ui.geometry.Offset
import model.*
import org.gephi.graph.api.GraphController
import org.gephi.graph.api.Node
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout
import org.gephi.project.api.*
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder
import org.openide.util.Lookup

data class VertexPosition<V>(val vertex: Vertex<V>, val x: Float, val y: Float)
data class VertexOffset<V>(val vertex: Vertex<V>, val offset: Offset)

fun normalizeEdgeWeight(value: Double, min: Double, max: Double): Double {
    require(min <= max) { "min must be less than or equal to max" }
    // If all values are the same, normalization would be undefined
    if (min == max) return 0.5
    return (value - min) / (max - min)
}

fun <V> applyForceAtlas2(graph: Graph<V>): MutableList<VertexOffset<V>> {
    val projectController = Lookup.getDefault().lookup(ProjectController::class.java)
    projectController.newProject()
    val workspace = projectController.currentWorkspace
    val graphModel = Lookup.getDefault().lookup(GraphController::class.java).graphModel
    val gephiGraph = graphModel.directedGraph
    val vertexMap = mutableMapOf<Vertex<V>, Node>()

    for (vertex in graph.vertices) {
        val node = graphModel.factory().newNode(vertex.index.toString())
        node.label = vertex.data.toString()
        gephiGraph.addNode(node)
        vertexMap[vertex] = node
    }

    val edgeWeightList = mutableListOf<Double>()
    for (edge in graph.edges){
        edgeWeightList.add(edge.weight)
    }
    val minWeight = edgeWeightList.min()
    val maxWeight = edgeWeightList.max()

    for (edge in graph.edges) {
        val weight = normalizeEdgeWeight(edge.weight, minWeight, maxWeight)
        val sourceNode = vertexMap[edge.source] ?: throw  IllegalStateException("Node cannot be null")
        val destNode = vertexMap[edge.destination] ?: throw  IllegalStateException("Node cannot be null")
        val gephiEdge = graphModel.factory().newEdge(sourceNode, destNode, weight.toInt(), true)
        gephiGraph.addEdge(gephiEdge)
    }

    val layout = ForceAtlasLayout(null)
    layout.setGraphModel(graphModel)

    layout.resetPropertiesValues()

    layout.initAlgo()

    for (i in 0 until 1000) {
        if (layout.canAlgo()) layout.goAlgo()
        else break
    }
    layout.endAlgo()

    val positions = mutableListOf<VertexPosition<V>>()
    val finalPositions = mutableMapOf<Vertex<V>, Offset>()
    val finalPositions2 = mutableListOf<VertexOffset<V>>()
    for (vertex in graph.vertices) {
        val node = vertexMap[vertex] ?: throw  IllegalStateException("Node cannot be null")
        positions.add(VertexPosition(vertex, node.x(), node.y()))
    }
    if (positions.size > 0) {
        val minX = positions.minBy { it.x }.x
        val maxX = positions.maxBy { it.x }.x
        val minY = positions.minBy { it.y }.y
        val maxY = positions.maxBy { it.y }.y
        for (vertex in graph.vertices) {
            finalPositions[vertex] = Offset(
                x = 1 - 2 * (vertexMap[vertex]!!.x() - minX) / (maxX - minX),
                y = 1 - 2 * (vertexMap[vertex]!!.y() - minY) / (maxY - minY)
            )
            val offset = Offset(
                x = 1 - 2 * (vertexMap[vertex]!!.x() - minX) / (maxX - minX),
                y = 1 - 2 * (vertexMap[vertex]!!.y() - minY) / (maxY - minY)
            )
            finalPositions2.add(VertexOffset(vertex, offset))
        }
    }
    return finalPositions2
}

fun main() {
    val graph = DirectedGraph<Int>()
    val zero = graph.addVertex(0,)
    val one = graph.addVertex(1,)
    val two = graph.addVertex(2,)
    val three = graph.addVertex(3,)
    val four = graph.addVertex(4,)
    val five = graph.addVertex(5,)

    graph.addEdge(zero, one)
    graph.addEdge(zero, two, -9.0)
    graph.addEdge(zero, three)
    graph.addEdge(zero, four)

    val positions = applyForceAtlas2(graph)
    positions.forEach {
        println("${it.vertex}, ${it.offset}")
    }
    println()
}