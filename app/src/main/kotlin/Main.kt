import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.DirectedGraph
import model.Graph
import model.GraphType
import model.UndirectedGraph
import view.Theme.Theme
import view.inputs.DBInput
import view.mainScreen.UGMainScreen
import viewmodel.mainScreenVM.DGScreenViewModel
import viewmodel.mainScreenVM.UGScreenViewModel
import viewmodel.placementStrategy.ForceAtlasPlacementStrategy
import java.awt.Dimension


val graph = DirectedGraph<Int>().apply {
    val zero = addVertex(0, 1)
    val one = addVertex(1, 2)
    val two = addVertex(2, 3)
    val three = addVertex(3, 4)
    val four = addVertex(4, 5)
    val five = addVertex(5, 6)
    val six = addVertex(6, 7)
    val seven = addVertex(7, 8)
    val eight = addVertex(8, 9)
    val nine = addVertex(9, 10)
    val ten = addVertex(10, 11)
    val eleven = addVertex(11, 12)
    val twelve = addVertex(12, 13)
    val thirteen = addVertex(13, 14)
    val fourteen = addVertex(14, 15)
    val fifteen = addVertex(15, 16)
    val zero1 = addVertex(6,17)
    val one1 = addVertex(1, 18)
    val two1 = addVertex(2, 19)
    val three1 = addVertex(3, 20)
    val four1 = addVertex(4, 21)
    val five1 = addVertex(5, 22)
    val six1 = addVertex(6, 23)
    val seven1 = addVertex(7, 24)
    val eight1 = addVertex(8, 25)
    val nine1 = addVertex(9, 26)
    val ten1 = addVertex(10, 27)
    val eleven1 = addVertex(11, 28)
    val twelve1 = addVertex(12, 29)
    val thirteen1 = addVertex(13, 30)
    val fourteen1 = addVertex(14, 31)
    val fifteen1 = addVertex(15, 32)
    val thirtyone = addVertex(31, 33)

    addEdge(two, zero, -1.0)
    addEdge(zero, one)
    addEdge(one, two)
    addEdge(one, three)
    addEdge(three, four)
    addEdge(four, five)
    addEdge(thirteen, five)
    addEdge(fifteen, seven)
    addEdge(seven, eleven)
    addEdge(six, two)
    addEdge(five, twelve)
    addEdge(one, fourteen)
    addEdge(eight, three)
    addEdge(ten, twelve)
    addEdge(ten, seven)
    addEdge(ten, five)
    addEdge(nine, nine)
}

val graph4 = DirectedGraph<Int>().apply {
    val  Alabama = addVertex(1, 1)
    val  Arizona = addVertex(2, 2)
    val  California = addVertex(3, 3)
    val  Connecticut = addVertex(4, 4)
    val  Florida = addVertex(5, 5)
    val  Hawaii = addVertex(6, 6)
    val  Illinois = addVertex(7, 7)
    val  Iowa = addVertex(8, 8)
    val  Kentucky = addVertex(9, 9)
    val  Maine = addVertex(100, 10)
    val  Massachusetts = addVertex(11, 11)
    val  Minnesota = addVertex(12, 12)
    val  Missouri = addVertex(13, 13)
    val  Montana = addVertex(14, 14)
    val  Nevada = addVertex(15, 15)
    val  NewJersey = addVertex(16, 16)
    val  NewYork = addVertex(17, 17)

    addEdge(Alabama, Illinois)
    addEdge(Alabama, Connecticut)
    addEdge(Alabama, Florida)
    addEdge(Alabama, Hawaii)
    addEdge(Alabama, Kentucky)
    addEdge(Kentucky, Iowa)
    addEdge(Kentucky, Montana)
    addEdge(Kentucky, California)
    addEdge(Kentucky, Maine)
    addEdge(Kentucky, NewJersey)
    addEdge(Missouri, Montana)
    addEdge(Montana, NewJersey)
    addEdge(California, Massachusetts)
    addEdge(California, Minnesota)
    addEdge(California, Maine)
    addEdge(California, Montana)
    addEdge(Iowa, Arizona)
    addEdge(Iowa, Montana)
    addEdge(Iowa, NewJersey)
    addEdge(Arizona, Montana)
    addEdge(Arizona, Nevada)
    addEdge(Arizona, NewJersey)
    addEdge(Arizona, NewYork)
    //addEdge(Missouri, Montana)
    //addEdge(Montana, NewJersey)
    addEdge(Montana, Nevada)
    addEdge(Maine, Kentucky)
}

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
val emptyGraph = DirectedGraph<Int>()

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
    /* временно */
//    val repo = SqliteRepo<Any>("/Users/sofyakozyreva/dddiiieee/mamamiia.db")
//    repo.connectToDatabase()
//    var graph3 : Graph<Any> = DirectedGraph()
//    transaction {
//        graph3 = repo.loadGraphFromDB("States") ?: return@transaction
//    }
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
    Window(onCloseRequest = ::exitApplication, title = "So Old School",) {
        window.minimumSize = Dimension(1050, 750)
        App()
    }
}
