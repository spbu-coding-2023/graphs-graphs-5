import model.DirectedGraph
import model.Vertex
import model.algorithms.DirectedGraphAlgorithmsImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class CycleDetectionOnDirectedGraph {
    private lateinit var graph: DirectedGraph<Int>
    //private lateinit var zero: Vertex<Int>
    private lateinit var algorithms: DirectedGraphAlgorithmsImpl<Int>

    @BeforeEach
    fun setup() {
        algorithms = DirectedGraphAlgorithmsImpl()
        graph = DirectedGraph()
        graph = DirectedGraph()
        for (i in 0..5) {
            graph.addVertex(i)
        }
    }

    @Test
    fun `cycle detection returns null on graph without edges`() {
        val actual = algorithms.getCycles(graph, Vertex(1, 1))
        val expected = null
        assertEquals(expected, actual)
    }

    @Test
    fun `cycle detection returns null on graph without cycles`() {
        graph.addEdge(Vertex(0, 0), Vertex(1, 1))
        graph.addEdge(Vertex(1, 1), Vertex(2, 2))
        graph.addEdge(Vertex(0, 0), Vertex(3, 3))
        graph.addEdge(Vertex(3, 3), Vertex(4, 4))

        val actual = algorithms.getCycles(graph, Vertex(1, 1))
        val expected = null
        assertEquals(expected, actual)
    }

    @Test
    fun `cycle detection returns cycle from given vertex`() {
        graph.addEdge(Vertex(0, 0), Vertex(1, 1))
        graph.addEdge(Vertex(1, 1), Vertex(2, 2))
        graph.addEdge(Vertex(2, 2), Vertex(0, 0))
        graph.addEdge(Vertex(3, 3), Vertex(4, 4))

        val actual = algorithms.getCycles(graph, Vertex(1, 1))
        val expected = listOf(0, 2, 1)
        assertEquals(expected, actual)
    }
}