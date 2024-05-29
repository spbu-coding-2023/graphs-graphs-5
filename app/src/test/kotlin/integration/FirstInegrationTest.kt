package integration

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import io.SqliteRepo
import menu
import model.DirectedGraph
import model.Graph
import model.UndirectedGraph
import model.Vertex
import model.algorithms.CommonAlgorithmsImpl
import model.algorithms.DirectedGraphAlgorithms
import model.algorithms.DirectedGraphAlgorithmsImpl
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Rule
import org.junit.jupiter.api.Assertions
import view.DBInput
import view.DGMainScreen
import view.Theme
import view.ThemeSwitcher
import viewmodel.DGScreenViewModel
import viewmodel.ForceAtlasPlacementStrategy
import viewmodel.MainScreenViewModel
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull
import java.io.File

class FirstIntegrationTest {

    @Test
    fun `SQLite integrable test`() {
        val graph = DirectedGraph<Any>()
        for (i in 0..4) {
            graph.addVertex(i)
        }
        graph.addEdge(Vertex(0, 0), Vertex(1, 1), 2.0)
        graph.addEdge(Vertex(1, 1), Vertex(2, 2), 9.0)
        graph.addEdge(Vertex(2, 2), Vertex(3, 3), 1.0)
        graph.addEdge(Vertex(2, 2), Vertex(0, 0), -1.0)
//        val dbInput = DBInput()
//        val graphVM = DGScreenViewModel(graph, ForceAtlasPlacementStrategy(), dbInput)
//        val repo = SqliteRepo<Any>(dbInput.pathToDb)
        val tempDbFile =  File.createTempFile("test_database", ".db")
        val repo = SqliteRepo<Any>(tempDbFile.absolutePath.toString())
        println(tempDbFile.toString())
//        Database.connect("jdbc:sqlite:${tempDbFile}", driver = "org.sqlite.JDBC")
        repo.connectToDatabase()

        transaction {
            repo.saveGraphToDB(graph, "test")
        }
        var loadedGraph: Graph<Any>? = DirectedGraph()
        transaction {
            loadedGraph = repo.loadGraphFromDB("test")
        }
        val algorithms = DirectedGraphAlgorithmsImpl<Any>()
        val newGraph = requireNotNull(loadedGraph) { "Graph was saved so it shouldn't be null" }
//        val shortestPathWithFB = algorithms.findPathWithFordBellman(Vertex(0, 0), Vertex(3, 3), newGraph)
//        assertNotNull(shortestPathWithFB, "Path exists so it shouldn't be null")
//        val actualPath = mutableListOf<Int>()
//        shortestPathWithFB.forEach { v ->
//            println(v)
//            actualPath.add(v.index)
//        }
//        val expectedPath = listOf(0, 0, 1, 2, 3)
        val componentsList = algorithms.findStrongComponents(newGraph)
        val actualComponentsList = mutableListOf<Int>()
        var i = 0
        var prevNum = 0
        var curNum: Int
        componentsList.forEach { (_, num) ->
            curNum = num
            if (curNum != prevNum) i++
            actualComponentsList.add(i)
            prevNum = num
        }
        val expectedComponentsList = listOf(1, 1, 1, 2, 3)
        assertContentEquals(expectedComponentsList, actualComponentsList, "Strongly connected components are incorrectly defined")
        tempDbFile.delete()
        // Delete the temporary database file

//        val actualPath = mutableListOf<Int>()
//        algorithms.findPathWithFordBellman(Vertex(0, 0), Vertex(3, 3), newGraph)?.forEach { v ->
//            println(v)
//            actualPath.add(v.index)
//        }
//        val expectedPath = listOf(0, 1, 2, 3)
//        assertContentEquals(expectedPath, actualPath, "Algorithm incorrect")
    }

}
