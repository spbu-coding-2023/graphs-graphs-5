//package view.mainScreen

import androidx.compose.runtime.Composable
import view.DBInput
import viewmodel.MainScreenViewModel

@Composable
fun <V> drawGraph(viewModel: MainScreenViewModel<V>, input: DBInput): String {
    //println(input.pathToDb)
    //println(input.name)
    //println(input.dBType)
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
        else -> {}
    }
    return ""
}