package viewmodel.mainScreenVM

import androidx.compose.ui.unit.dp
import model.Graph
import model.Vertex
import model.algorithms.DirectedGraphAlgorithmsImpl
import view.Theme.*
import view.inputs.DBInput
import view.inputs.MenuInput
import viewmodel.MainScreenViewModel
import viewmodel.RepresentationStrategy
import viewmodel.VertexViewModel
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
    override fun getListOfAlgorithms(): List<String> {
        return listOf("Graph clustering", "Key vertices", "Cycles", "Strong components",
            "Min path (Dijkstra)", "Min path (Ford-Bellman)")
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
}