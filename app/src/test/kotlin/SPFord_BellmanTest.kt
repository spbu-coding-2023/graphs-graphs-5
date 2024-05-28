import model.DirectedGraph
import model.Vertex
import model.algorithms.DirectedGraphAlgorithmsImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ShortestPathFordBellmanOnDirectedGraph {
    private lateinit var graph: DirectedGraph<Int>
    private lateinit var algorithms: DirectedGraphAlgorithmsImpl<Int>
    @BeforeEach
    fun setup() {
        algorithms = DirectedGraphAlgorithmsImpl()
        graph = DirectedGraph()
        val zero = graph.addVertex(0)
        val one = graph.addVertex(1)
        val two = graph.addVertex(2)
        val three = graph.addVertex(3)
        val four = graph.addVertex(4)

        graph.addEdge(zero, one, -1.0)
        graph.addEdge(zero, two, 4.0)
        graph.addEdge(one, two, 3.0)
        graph.addEdge(one, four, 2.0)
        graph.addEdge(three, two, 5.0)
        graph.addEdge(four, three, -3.0)
    }

    @Test
    fun`SPFB returns path on directed graph`() {
        graph.addEdge(Vertex(3, 3), Vertex(1, 1), 1.0)
        graph.addEdge(Vertex(1, 1), Vertex(3, 3), 2.0)
        val actualPath = mutableListOf<Int>()
        algorithms.findPathWithFordBellman(Vertex(0, 0), Vertex(3, 3), graph)?.forEach { v ->
            actualPath.add(v.index)
        }
        val expectedPath = listOf(0, 1, 4, 3)
        assertEquals(expectedPath, actualPath)
    }

    @Test
    fun`SPFB returns cycle on directed graph`() {
        graph.addEdge(Vertex(3, 3), Vertex(1, 1), -1.0)
        graph.addEdge(Vertex(1, 1), Vertex(3, 3), -2.0)
        val actualCycle = mutableListOf<Int>()
        algorithms.findPathWithFordBellman(Vertex(0, 0), Vertex(3, 3), graph)?.forEach { v ->
            actualCycle.add(v.index)
        }
        val expectedCycle = listOf(1, 3, 1)
        assertEquals(expectedCycle, actualCycle)
    }

    @Test
    fun`SPFB returns nothing because vertex is unreachable on directed graph`() {
        val five = graph.addVertex(5)
        val actualCycle = mutableListOf<Int>()
        algorithms.findPathWithFordBellman(Vertex(0, 0), five, graph)?.forEach { v ->
            actualCycle.add(v.index)
        }
        val expectedCycle = listOf<Int>()
        assertEquals(expectedCycle, actualCycle)
    }
}