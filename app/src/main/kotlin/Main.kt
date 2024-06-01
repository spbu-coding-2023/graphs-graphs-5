import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.Graph
import model.GraphType
import model.UndirectedGraph
import view.Theme
import view.inputs.DBInput
import view.mainScreen.DGMainScreen
import view.mainScreen.UGMainScreen
import viewmodel.mainScreenVM.DGScreenViewModel
import viewmodel.mainScreenVM.UGScreenViewModel
import viewmodel.placementStrategy.ForceAtlasPlacementStrategy
import java.awt.Dimension

val graph2 = UndirectedGraph<Int>().apply {
    val zero = addVertex(0, 1)
    val one = addVertex(1, 2)
    val two = addVertex(2, 3)
    val three = addVertex(3, 4)
    val four = addVertex(4, 5)
    val five = addVertex(5, 6)
    addEdge(two, zero)
    addEdge(zero, one)
    addEdge(one, two)
    addEdge(one, three)
    addEdge(three, four)
    addEdge(four, five)
}
@Composable
@Preview
fun App() {
//    val repo = Neo4jRepo<Any>("bolt://localhost:7687","neo4j", "my my, i think we have a spy ;)")
//    var graph3 = DirectedGraph<Any>()
//    graph3 = repo.getGraphFromNeo4j(graph3) as DirectedGraph<Any>
    //println(graph3.edges)
    //println(graph3.vertices)
    val input = DBInput()
    ScreenFactory.createView(graph2, input)
}
//@Composable
//@Preview
//fun App() {
//    val emptyGraph = UndirectedGraph<Int>()
//    val input = DBInput()
//    ScreenFactory.createView(emptyGraph, input)
//}

object ScreenFactory {
    @Composable
    fun <V> createView(graph: Graph<V>, input: DBInput) {
        val theme = mutableStateOf(Theme.CLASSIC)
        when (graph.graphType) {
            GraphType.DIRECTED ->
                DGMainScreen(
                    DGScreenViewModel(graph, ForceAtlasPlacementStrategy(), input), theme
                )

            else ->
                UGMainScreen(
                    UGScreenViewModel(graph, ForceAtlasPlacementStrategy(), input), theme
                )
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "So Old School") {
        window.minimumSize = Dimension(1050, 750)
        App()
    }
}
