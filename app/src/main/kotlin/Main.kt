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
import view.MainScreen
import view.Material3AppTheme
import viewmodel.CircularPlacementStrategy
import viewmodel.MainScreenViewModel
import java.awt.Dimension

val graph : Graph<Int> = DirectedGraph<Int>().apply {
    val zero = addVertex(0)
    val one = addVertex(1)
    val two = addVertex(2)
    val three = addVertex(3)
    val four = addVertex(4)
    val five = addVertex(5)

    addEdge(two, zero, null)
    addEdge(zero, one)
    addEdge(one, two)
    addEdge(one, three)
    addEdge(four, three)
    addEdge(three, four)
    addEdge(four, five)
}
@Composable
@Preview
fun App() {
    var darkTheme by remember { mutableStateOf(false) }
    Material3AppTheme(darkTheme = darkTheme) {
        MainScreen(
            darkTheme = darkTheme,
            onThemeUpdated = { darkTheme = !darkTheme },
            MainScreenViewModel(graph, CircularPlacementStrategy()
            )
        )
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        window.minimumSize = Dimension(1050, 750)
        App()
    }
}
