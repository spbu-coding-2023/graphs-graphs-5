package integration

import io.SqliteRepo
import model.DirectedGraph
import model.Vertex
import model.algorithms.DirectedGraphAlgorithmsImpl
import org.jetbrains.exposed.sql.transactions.transaction
import view.inputs.DBInput
import viewmodel.mainScreenVM.DGScreenViewModel
import viewmodel.placementStrategy.ForceAtlasPlacementStrategy
import kotlin.test.Test
import kotlin.test.assertContentEquals
import java.io.File

/*
* Test Documentation
* Purpose: to verify the correct interaction between various components of our projects Graphs-5,
* such as running algorithms, interacting with databases, and handling logic with model - view model - view.
* Test Cases
* Test Case 1: Running algorithms and saving-loading data with SQLite on view model level
* Description: a graph and a temporary java file are created. Algorithm for highlighting Key Vertices is run on this graph.
* A connection to database established, path is provided by temporary file.
* Graph and algorithm results are saved to database.
* A new graph is loaded from database with the same name. It is null-checked.
* The results of algorithm for highlighting Key Vertices are loaded from database and compared to the initial ones.
* Another algorithm for finding Strongly Connected Components is run on the graph. Results are compared to expected.
* Temporary file is deleted.
* Expected Results: graph is successfully saved to database and later loaded from it.
* Algorithm results are also stored in the database. SCCS algorithms returns correct results.
* */
class SqliteIntegrationTest {

    @Test
    fun `Integration test with VM, algorithms, and SQLIte`() {
        val graph = DirectedGraph<Any>()
        for (i in 0..4) {
            graph.addVertex(i)
        }
        graph.addEdge(Vertex(0, 0), Vertex(1, 1), 2.0)
        graph.addEdge(Vertex(1, 1), Vertex(2, 2), 9.0)
        graph.addEdge(Vertex(2, 2), Vertex(3, 3), 1.0)
        graph.addEdge(Vertex(2, 2), Vertex(0, 0), -1.0)

        val tempDbFile =  File.createTempFile("test_database", ".db")
        val dbInput = DBInput()
        dbInput.dBType = "sqlite"
        dbInput.name = "test"
        dbInput.pathToDb = tempDbFile.absolutePath.toString()
        val repo = SqliteRepo<Any>(dbInput.pathToDb)
        repo.connectToDatabase()
        transaction {
            repo.saveGraphToDB(graph, dbInput.name)
        }

        val algorithms = DirectedGraphAlgorithmsImpl<Any>()
        val initialVerticesRankList = mutableListOf<Double>()
        algorithms.findKeyVertices(graph).forEach { v ->
            val vertexRank = v.second
            initialVerticesRankList.add(vertexRank)
        }

        val viewModel = DGScreenViewModel(graph, ForceAtlasPlacementStrategy(), dbInput)
        viewModel.saveAlgoResults()

        val newGraph = viewModel.configureSQLiteRepo(dbInput).first ?: throw IllegalStateException("Graph shouldn't be null")
        val newViewModel = DGScreenViewModel(newGraph, ForceAtlasPlacementStrategy(), dbInput)
        val newVerticesRankList = newViewModel.loadKeyVerticesResults()
        assertContentEquals(initialVerticesRankList, newVerticesRankList)

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
    }

}
