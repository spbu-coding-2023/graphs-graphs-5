import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.DirectedGraph
import model.Graph
import model.GraphType
import model.UndirectedGraph
import view.*
import viewmodel.*
import java.awt.Dimension

val graph = DirectedGraph<Int>().apply {
    val zero = addVertex(0)
    val one = addVertex(1)
    val two = addVertex(2)
    val three = addVertex(3)
    val four = addVertex(4)
    val five = addVertex(5)

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
    ScreenFactory.createView(graph)
}

object ScreenFactory {
    @Composable
    fun <V> createView(graph: Graph<V>) {
        val theme = mutableStateOf(Theme.CLASSIC)
        when (graph.graphType) {
            GraphType.DIRECTED ->
                DGMainScreen(
                    DGScreenViewModel(graph, CircularPlacementStrategy()), theme
                )
            else ->
                UGMainScreen(
                    UGScreenViewModel(graph, CircularPlacementStrategy()), theme
                )
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        window.minimumSize = Dimension(1050, 750)
        App()
    }
}
