package viewmodel.mainScreenVM

import androidx.compose.ui.graphics.Color
import model.Graph
import model.algorithms.UndirectedGraphAlgorithmsImpl
import view.inputs.DBInput
import view.inputs.MenuInput
import viewmodel.placementStrategy.RepresentationStrategy

class UGScreenViewModel<V>(
    graph: Graph<V>,
    representationStrategy: RepresentationStrategy,
    dBInput: DBInput
) : MainScreenViewModel<V>(graph, representationStrategy, dBInput) {
    override val algorithms = UndirectedGraphAlgorithmsImpl<V>()
    protected open val udAlgorithms = UndirectedGraphAlgorithmsImpl<V>()
    override fun run(input: MenuInput): String {
        var message = ""
        when {
            input.text == "Bridges" -> highlightBridges()
            input.text == "Min Tree" -> highlightCore()
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
                if (source == null || destination == null) message = "Error: vertex with this index doesn't exist"
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
        return listOf(
            "Graph clustering", "Key vertices", "Min tree", "Bridges",
            "Min path (Dijkstra)"
        )
    }

    private fun highlightBridges() {
        clearChanges()

        val bridges = udAlgorithms.findBridges(graph)
        for (bridge in bridges) {

            for (edge in graphViewModel.edges) {
                if (bridge == edge.edge) {
                    edge.color = Color.Green
                }
            }
        }
    }

    private fun highlightCore() {
        clearChanges()

        val coreVertices = udAlgorithms.findCore(graph)

        for (core in coreVertices) {

            for (edge in graphViewModel.edges) {
                if (core == edge.edge) {
                    edge.color = Color.Green
                }
            }
        }
    }
}