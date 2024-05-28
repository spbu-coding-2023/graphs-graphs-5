package viewmodel.mainScreenVM

import model.Graph
import model.algorithms.UndirectedGraphAlgorithmsImpl
import view.inputs.DBInput
import view.inputs.MenuInput
import viewmodel.MainScreenViewModel
import viewmodel.RepresentationStrategy

class UGScreenViewModel<V>(
    graph: Graph<V>,
    representationStrategy: RepresentationStrategy,
    DBinput: DBInput
) : MainScreenViewModel<V>(graph, representationStrategy, DBinput) {
    override val algorithms = UndirectedGraphAlgorithmsImpl<V>()
    override fun run(input: MenuInput): String {
        var message = ""
        when {
            input.text == "Graph clustering" -> divideIntoClusters()
            input.text == "Key vertices" -> highlightKeyVertices()
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