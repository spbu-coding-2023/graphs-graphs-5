import model.Edge
import model.UndirectedGraph
import model.Vertex
import model.algorithms.UndirectedGraphAlgorithmsImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class BridgesInUndirectedGraph {
    private lateinit var graph: UndirectedGraph<Int>
    private lateinit var algorithms: UndirectedGraphAlgorithmsImpl<Int>

    @Test
    fun `findBridges returns empty list when no bridges`() {

        graph.addVertex(2,0)
        graph.addVertex(4,1)
        graph.addVertex(3,2)
        graph.addEdge(Vertex(2,0),Vertex(4,1))
        graph.addEdge(Vertex(3,2),Vertex(4,1))
        graph.addEdge(Vertex(3,2),Vertex(2,0))


        val actualBridges = algorithms.findBridges(graph)
        assertTrue(actualBridges.isEmpty())
    }

    @Test
    fun `findBridges works correctly with disconnected graph`() {

        graph.addVertex(2,0)
        graph.addVertex(4,1)
        graph.addVertex(3,2)


        val actualBridges = algorithms.findBridges(graph)
        assertTrue(actualBridges.isEmpty())
    }

    @Test
    fun `findBridges returns empty list for graph with single vertex`() {

        graph.addVertex(1)

        val actualBridges = algorithms.findBridges(graph)
        assertTrue(actualBridges.isEmpty())
    }
}