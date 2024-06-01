package view.mainScreen

import androidx.compose.runtime.Composable
import model.Vertex
import view.inputs.DBInput
import viewmodel.mainScreenVM.MainScreenViewModel

@Composable
fun <V> drawGraph(viewModel: MainScreenViewModel<V>, input: DBInput): String {
    when (input.dBType) {
        "neo4j" -> {
            val (graph, message) = viewModel.configureNeo4jRepo(input)
            if (message.isNotEmpty()) {
                println(message)
                return message
            }
            else if (graph != null){
                ScreenFactory.createView(graph, input)
            }

        }
        "sqlite" -> {
            val (graph, message) = viewModel.configureSQLiteRepo(input)
            if (message.isNotEmpty()) {
                return message
            }
            else if (graph != null){
                ScreenFactory.createView(graph, input)
            }
        }

        ".json" -> {
            val (graph, message) = viewModel.loadGraphFromJson(input.pathToDb) { serializableVertex ->
                Vertex(serializableVertex.index, serializableVertex.data, serializableVertex.dBIndex)
            }
            if (message.isNotEmpty()) {
                return message
            } else if (graph != null) {
                ScreenFactory.createView(graph, input)
            }
        }
        else -> {}
    }
    return ""
}