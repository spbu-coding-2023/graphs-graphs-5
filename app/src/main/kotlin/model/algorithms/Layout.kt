package model.algorithms


import androidx.compose.ui.geometry.Offset
import org.gephi.graph.api.*
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout
import org.gephi.project.api.*
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder
import org.openide.util.Lookup
import kotlin.math.pow

data class Vertex<V>(val index: Int, val data: V)

data class Edge<V>(
    val index: Int,
    val source: Vertex<V>,
    val destination: Vertex<V>,
    val weight: Double = 1.0
)

interface Graph<V> {
    val vertices: Collection<Vertex<V>>
    val edges: Collection<Edge<V>>
    val isDirected: Boolean

    fun addVertex(data: V): Vertex<V>
    fun addEdge(source: Vertex<V>, destination: Vertex<V>, weight: Double = 1.0)
    fun edges(source: Vertex<V>): ArrayList<Edge<V>>
    fun weight(source: Vertex<V>, destination: Vertex<V>): Double?
    fun getRankingList(): List<Pair<Vertex<V>, Double>>
}

open class DirectedGraph<V> : Graph<V> {
    private val _vertices = hashMapOf<Int, Vertex<V>>()
    private val _edges = hashMapOf<Int, Edge<V>>()

    override val vertices: Collection<Vertex<V>>
        get() = _vertices.values

    override val edges: Collection<Edge<V>>
        get() = _edges.values

    override val isDirected: Boolean
        get() = true

    private val adjacencies: HashMap<Vertex<V>, ArrayList<Edge<V>>> = HashMap()

    override fun addVertex(data: V): Vertex<V> {
        val vertex = Vertex(_vertices.size, data)
        _vertices[_vertices.size] = vertex
        adjacencies[vertex] = ArrayList()
        return vertex
    }

    override fun addEdge(source: Vertex<V>, destination: Vertex<V>, weight: Double) {
        addDirectedEdge(source, destination, weight)
    }

    fun addDirectedEdge(source: Vertex<V>, destination: Vertex<V>, weight: Double) {
        val edge = Edge(_edges.count(), source, destination, weight)
        _edges[_edges.count()] = edge
        adjacencies[source]?.add(edge)
    }

    override fun edges(source: Vertex<V>) = adjacencies[source] ?: arrayListOf()

    override fun weight(source: Vertex<V>, destination: Vertex<V>): Double? {
        return edges(source).firstOrNull { it.destination == destination }?.weight
    }

    override fun getRankingList(): List<Pair<Vertex<V>, Double>> {
        val rankingList = mutableListOf<Pair<Vertex<V>, Double>>()
        val vertices = adjacencies.keys
        vertices.forEach {
            rankingList.add(Pair(it, roundDouble(EHC(it), 4)))
        }
        return rankingList
    }

    fun roundDouble(number: Double, decimalPlaces: Int): Double {
        return String.format("%.${decimalPlaces}f", number).toDouble()
    }

    // Implementations for centrality calculations
    fun getDegree(vertex: Vertex<V>): Int {
        val edges = adjacencies[vertex]
        return edges?.size ?: 0
    }

    fun getGreatestDegree(): Int {
        return adjacencies.keys.maxOfOrNull { getDegree(it) } ?: 0
    }

    fun getNumOfNeighborsWithDegree(k: Int, vertex: Vertex<V>): Int {
        var result = 0
        edges(vertex).forEach {
            if (getDegree(it.destination) == k) {
                result++
            }
        }
        return result
    }

    fun cumulativeFunctionVectorIndex(k: Int, vertex: Vertex<V>): Int {
        var cumulativeVectorElement = 0
        if (k == 1) {
            cumulativeVectorElement = getDegree(vertex)
        } else if (k > 1) {
            cumulativeVectorElement =
                cumulativeFunctionVectorIndex(k - 1, vertex) - getNumOfNeighborsWithDegree(k - 1, vertex)
        }
        return cumulativeVectorElement
    }

    fun cumulativeFunctionVector2(vertex: Vertex<V>): IntArray {
        val h = getGreatestDegree()
        val cumulativeVector = IntArray(h)
        for (i in 1..h) {
            cumulativeVector[i - 1] = cumulativeFunctionVectorIndex(i, vertex)
        }
        println("cumulativeVector2: ${cumulativeVector.contentToString()}")
        return cumulativeVector
    }

    fun cumulativeCentrality(vertex: Vertex<V>): Double {
        val h = getGreatestDegree()
        var cmc = 0.0
        val p = 0.8
        val r = 100.0
        for (k in 1..h) {
            cmc += p.pow(1.0 + k.toDouble() * p / r) * cumulativeFunctionVectorIndex(k, vertex).toDouble()
        }
        return cmc
    }

    fun EHC(vertex: Vertex<V>): Double {
        var ehc = 0.0
        edges(vertex).forEach {
            ehc += cumulativeCentrality(it.destination)
        }
        return ehc
    }
}
class UndirectedGraph<V> : DirectedGraph<V>(), Graph<V> {
    override fun addEdge(source: Vertex<V>, destination: Vertex<V>, weight: Double) {
        addDirectedEdge(source, destination, weight)
        addDirectedEdge(destination, source, weight)
    }
    override val isDirected: Boolean
        get() = false
}

// Data class for storing vertex positions
data class VertexPosition<V>(val vertex: Vertex<V>, val x: Float, val y: Float)
data class VertexOffset<V>(val vertex: Vertex<V>, val offset: Offset)

fun normalizeEdgeWeight(value: Double, min: Double, max: Double): Double {
    require(min <= max) { "min must be less than or equal to max" }

    // If all values are the same, normalization would be undefined
    if (min == max) return 0.5 // or any arbitrary value since they are all the same

    return (value - min) / (max - min)
}

fun <V> applyForceAtlas2(graph: Graph<V>): MutableList<VertexOffset<V>> {
    // Initialize Gephi project
    val projectController = Lookup.getDefault().lookup(ProjectController::class.java)
    projectController.newProject()
    val workspace = projectController.currentWorkspace

    // Get the graph model
    val graphModel = Lookup.getDefault().lookup(GraphController::class.java).graphModel

    // Create Gephi graph from DirectedGraph
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

    // Initialize ForceAtlas2

    val layout = ForceAtlasLayout(null)
    layout.setGraphModel(graphModel)

    layout.resetPropertiesValues()

    layout.initAlgo()

    for (i in 0 until 1000) {
        if (layout.canAlgo()) layout.goAlgo()
        else break
    }
    layout.endAlgo()

//    // Return vertex positions
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
//            val node = vertexMap[vertex] ?: throw  IllegalStateException("cannot be null")
//            positions.add(VertexPosition(vertex, node.x().toFloat(), node.y().toFloat()))
        }
    }
//    for (vertex in graph.vertices) {
//
//    }
    return finalPositions2
}
