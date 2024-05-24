package viewmodel

//import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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

abstract class MainScreenViewModel<V>(
    val graph: Graph<V>,
    private val representationStrategy: RepresentationStrategy
) {
    val showVerticesLabels = mutableStateOf(false)
    val showEdgesLabels = mutableStateOf(false)
    val graphViewModel = GraphViewModel(graph, showVerticesLabels, showEdgesLabels)
//
//    var scale by mutableStateOf(1f)
//    var offsetX by mutableStateOf(0f)
//    var offsetY by mutableStateOf(0f)
//
//    fun handleTransformGestures(pan: Offset, zoom: Float) {
//        offsetX += pan.x / scale
//        offsetY += pan.y / scale
//        scale *= zoom
//    }
//
//    fun moveSurface(pan: Offset) {
//        offsetX += pan.x
//        offsetY += pan.y
//    }

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
            if (vertList[i].DBindex == index) {
                result = vertList[i]
            }
        }
        return result
    }

    //should be protected?
//    open fun getVertexByIndex(index: Int): Vertex<V>? {
//        val vertList = graph.vertices.toList()
//        val result = vertList.getOrNull(index)
//        return result
//    }
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
        val result = algorithms.getClusters(graph)
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

    fun highlightCycles(source: Vertex<V>): String {
        clearChanges()
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
                    v.color = ComponentColorNavy
                }
            }
            //REDO
            graphViewModel.edges.forEach{e ->
                if ((cycle.contains(e.u.vertex.index)) && (cycle.contains(e.v.vertex.index))) {
                    e.color = ComponentColorNavy
                }
            }
        }
        return message
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
            //REDO
            graphViewModel.edges.forEach{e ->
                if ((path.contains(e.u.vertex.index)) && (path.contains(e.v.vertex.index))) {
                    e.color = ComponentColorNavy
                }
            }
        }
        return ""
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
    override fun run(input: MenuInput): String {
        var message = ""
//        println(input.text)
        when {
            input.text == "Graph clustering" -> divideIntoClusters()
            input.text == "Key vertices" -> highlightKeyVertices()
            input.text == "Cycles" -> {
                val vertex = getVertexByIndex(input.inputValueOneVertex.toInt())
                if (vertex != null) {
                    resetGraphView()
                    message = highlightCycles(vertex)
                }
                else {
                    message = "Index out of bounds, maximum value is ${graph2.vertices.size - 1}"
                }
            }
            input.text == "Strong components" -> findStrongComponents()
            input.text == "Min path (Dijkstra)" -> {
                //ПРОВЕРКА НА ОТРИЦАТЕЛЬНЫЕ ВЕСА
                val source = getVertexByIndex(input.inputStartTwoVer.toInt())
                val destination = getVertexByIndex(input.inputEndTwoVer.toInt())
                if(source == null || destination == null) message = "Index out of bounds, maximum value is ${graph2.vertices.size - 1}"
                else {
                    message = highlightPathDijkstra(source, destination)
                }
            }
            input.text == "Min path (Ford-Bellman)" -> {
                val source = getVertexByIndex(input.inputStartTwoVer.toInt())
                val destination = getVertexByIndex(input.inputEndTwoVer.toInt())
                if(source == null || destination == null) message = "Index out of bounds, maximum value is ${graph2.vertices.size - 1}"
                else message = findSPwFB(source, destination)
            }
            else -> {
                resetGraphView()
            }
        }
        return message
    }

    private fun findStrongComponents() {
        clearChanges()
        val componentsList = algorithms.findStrongComponents(graph2)
        val relativeList = mutableListOf<Int>()
        componentsList.forEach {
            relativeList.add(it.second)
        }
        val vertexVMMap= hashMapOf<VertexViewModel<V>, Int>()
        var i = 0
        graphViewModel.vertices.forEach{ v ->
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
    private fun findSPwFB(source: Vertex<V>, destination: Vertex<V>): String {
        clearChanges()
        var message = ""
        val list = algorithms.findPathWithFordBellman(source, destination, graph2)
            ?: return  "${destination.index} is unattainable from ${source.index}"
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
//        path.forEach{
//            println("${it.key.vertex}, ${it.value}")
//        }
//        println(path.size)
        graphViewModel.edges.forEach { e ->
            if (path.contains(e.u) && path.contains(e.v) &&
                (path[e.u]?.let { path[e.v]?.minus(it) }) == 1 || (path[e.u]?.let { path[e.v]?.minus(it) }) == -1) {
                e.color = ComponentColorNavy
            }
//            if (path[e.u] == 1 && path[e.v] == path.size && message == "Negative-weight cycle detected") {
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
    representationStrategy: RepresentationStrategy
) : MainScreenViewModel<V>(graph, representationStrategy) {
    override val algorithms = UndirectedGraphAlgorithmsImpl<V>()
    override fun run(input: MenuInput): String {
        var message = ""
        when {
            input.text == "Key vertices" -> highlightKeyVertices()
            input.text == "Cycles" -> {
                val vertex = getVertexByIndex(input.inputValueOneVertex.toInt())
                if (vertex != null) {
                    resetGraphView()
                    message = highlightCycles(vertex)
                }
                else {
                    message = "Index out of bounds, maximum value is ${graph.vertices.size - 1}"
                }
            }
            else -> {
                resetGraphView()
            }
        }
        return message
    }

    override fun getListOfAlgorithms(): List<String> {
        return listOf("Graph clustering", "Key vertices", "Cycles", "Min tree", "Bridges",
            "Min path (Dijkstra)")
    }
    private fun findBridges() {
        TODO()
    }

    private fun findCore() {
        TODO()
    }
}