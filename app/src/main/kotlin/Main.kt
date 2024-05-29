import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.DirectedGraph
import model.Graph
import model.GraphType
import model.UndirectedGraph
import view.theme.Theme
import view.inputs.DBInput
import view.mainScreen.DGMainScreen
import view.mainScreen.UGMainScreen
import viewmodel.mainScreenVM.DGScreenViewModel
import viewmodel.mainScreenVM.UGScreenViewModel
import viewmodel.placementStrategy.ForceAtlasPlacementStrategy
import java.awt.Dimension

@Composable
@Preview
fun App() {
    val emptyGraph = DirectedGraph<Int>()
    val input = DBInput()
    ScreenFactory.createView(emptyGraph, input)
}

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
