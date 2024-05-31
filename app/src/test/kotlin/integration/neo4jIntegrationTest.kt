package integration

import io.Neo4jRepo
import model.DirectedGraph
import model.algorithms.DirectedGraphAlgorithmsImpl
import org.junit.jupiter.api.Test
import org.neo4j.driver.*
import kotlin.test.assertEquals

/*
* Test Documentation
* Purpose: to verify the correct interaction between various components of our projects Graphs-5,
* such as running algorithms, interacting with databases, and handling logic with model - view model - view.
* Test Cases
* Test Case 1: Constructing graph, running algorithms and saving-loading data with neo4j on model-repo level
* Description: a sample testing graph and a temporary neo4j database are created.
* Sample graph is put into database and then pulled back.
* Algorithms for getting cluster partition and highlighting key vertices is run on both pulled and in-program created graphs.
* The results of algorithm for getting cluster partition and highlighting key vertices are loaded into database.
* The results of algorithms are pulled back, null-checked and compared to in-program results
* Note that distribution of indices inside neo4j is uncontrolled by user, so only values can be compared, not the order
* Expected Results: graph is successfully saved to database and later loaded from it.
* Algorithm results are also stored in the database. Both return values equal to in-program ones
* */

class Neo4jIntegrationTest {

    @Test
    fun `Neo4j integration test`() {
        //create in-program test graph
        val graph = DirectedGraph<Any>()
        val zero = graph.addVertex(0, 0)
        val one = graph.addVertex(1, 1)
        val two = graph.addVertex(2, 2)
        val three = graph.addVertex(3, 3)
        val four = graph.addVertex(4, 4)

        graph.addEdge(zero, one, 2.0)
        graph.addEdge(one, two, 9.0)
        graph.addEdge(two, three, 1.0)
        graph.addEdge(two, zero, 1.0)
        graph.addEdge(four, zero, 3.0)

        //create neo4j representation of test graph
        val uri = "bolt://localhost:7689"
        val user = "neo4j"
        val password = "testtesttest"

        val driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))
        driver.use {
            val session = driver.session()
            session.use {
                val vertices = graph.vertices
                val edges = graph.edges

                vertices.forEach { vertex ->
                    session.writeTransaction { tx ->
                        val id = vertex.index
                        val data = vertex.data
                        tx.run("CREATE (v:Vertex {id: $id, data: $data})", Values.parameters("id", id, "data", data))
                    }
                }

                edges.forEach { edge ->
                    session.writeTransaction { tx ->
                        val from = edge.source.index
                        val to = edge.destination.index
                        val weight = edge.weight
                        tx.run(
                            """
                MATCH (v:Vertex {id: $from}), (u:Vertex {id: $to})
                CREATE (v)-[:CON_TO {weight: $weight}]->(u)
                """.trimIndent(),
                            Values.parameters("from", from, "to", to, "weight", weight)
                        )
                    }
                }
            }
        }

        //pull graph from neo4j back into program
        val repo = Neo4jRepo<Any>(uri, user, password)
        var newGraph = DirectedGraph<Any>()
        newGraph = repo.getGraphFromNeo4j(newGraph) as DirectedGraph<Any>

        //get main algorithms results from pulled graph
        val algorithms = DirectedGraphAlgorithmsImpl<Any>()
        val clusteringResult = algorithms.getClusters(newGraph)
        val rankingList = mutableListOf<Double>()
        algorithms.findKeyVertices(newGraph).forEach{ v ->
            val vertexRank = v.second
            rankingList.add(vertexRank)
        }

        //load algorithms results into database
        repo.saveClusterDetectionResults(newGraph, clusteringResult)
        repo.saveKeyVerticesResults(newGraph, rankingList)

        //pull algorithms results back into program
        val newClusteringResult = repo.getClusteringResults()
        var newClusters = IntArray(0)
        if (newClusteringResult != null) {
            newClusters = newClusteringResult.toIntArray()
        }
        val newList  = repo.getKeyVerticesResults()
        var newRankingList = listOf<Double>()
        if (newList != null) {
            newRankingList = newList.toList()
        }

        //get main algorithms results from in-program graph
        val expectedClusteringResult = algorithms.getClusters(graph)
        val expectedRankingList = mutableListOf<Double>()
        algorithms.findKeyVertices(graph).forEach{ v ->
            val vertexRank = v.second
            expectedRankingList.add(vertexRank)
        }

        //compare in-program and pulled from database results. they should be equal
        assertEquals(expectedClusteringResult.sortedArray().toList(), newClusters.sortedArray().toList())
        assertEquals(expectedRankingList.toList().sortedDescending(), newRankingList.sortedDescending())

        driver.close()
    }
}