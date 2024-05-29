import model.DirectedGraph
import model.Vertex
import model.algorithms.DirectedGraphAlgorithmsImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class ShortestPathDijkstra {
    private lateinit var graph: DirectedGraph<Int>
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
    fun `Dijkstra shortest path returns null and info message when vertices are not connected`() {
        val actual = algorithms.findPathWithDijkstra(graph, Vertex(1, 1), Vertex(2, 2))
        val expected = Pair("Vertices are unattainable", Pair(null, null))
        assertEquals(expected, actual)
    }

    @Test
    fun `Dijkstra shortest path returns shortest path and its weight when vertices are connected`() {
        graph.addEdge(Vertex(0, 0), Vertex(1, 1))
        graph.addEdge(Vertex(1, 1), Vertex(2, 2))
        graph.addEdge(Vertex(0, 0), Vertex(2, 2))
        graph.addEdge(Vertex(3, 3), Vertex(4, 4))
        val actual = algorithms.findPathWithDijkstra(graph, Vertex(0, 0), Vertex(2, 2))
        val expected = Pair("", Pair(listOf(0, 2), 1.0))
        assertEquals(expected, actual)
    }

}