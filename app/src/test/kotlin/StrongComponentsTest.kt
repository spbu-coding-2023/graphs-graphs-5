import model.DirectedGraph
import model.Vertex
import model.algorithms.DirectedGraphAlgorithmsImpl
import model.algorithms.buildTransposeGraph
import model.algorithms.dfs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class StrongComponentsTestOnDirectedGraph {
    private lateinit var graph: DirectedGraph<String>
    private lateinit var algorithms: DirectedGraphAlgorithmsImpl<String>
    @BeforeEach
    fun setup() {
        algorithms = DirectedGraphAlgorithmsImpl()
        graph = DirectedGraph()
        val alice = graph.addVertex("hi")
        val bob = graph.addVertex("hello")
        val carrie = graph.addVertex("good morning")
        val harry = graph.addVertex("have a good one")
        val max = graph.addVertex("see you soon")
        val lacy = graph.addVertex("goodbye")

        graph.addEdge(carrie, alice)
        graph.addEdge(alice, bob)
        graph.addEdge(bob, carrie)
        graph.addEdge(bob, harry)
        graph.addEdge(max, harry)
        graph.addEdge(harry, max)
        graph.addEdge(max, lacy)
    }
    @Test
    fun `dfs on directed graph`() {
        val visited = BooleanArray(6)
        val listOfOrder = mutableListOf<Vertex<String>>()
        graph.vertices.forEach { v ->
            dfs(v, listOfOrder, visited, graph)
        }
        val actualListOfOrder = mutableListOf<Int>()
        /* for easier comparison, we are going to use indices instead of vertices */
        listOfOrder.forEach {
            actualListOfOrder.add(it.index)
        }
        val expectedListOfOrder = listOf(0, 1, 3, 4, 5, 2)
        assertEquals(expectedListOfOrder, actualListOfOrder)
    }

    @Test
    fun `transpose directed graph`() {
        val transposeGraph = buildTransposeGraph(graph)
        val actualListOfEdges = mutableListOf<Pair<Int, Int>>()
        transposeGraph.edges.forEach {e ->
            actualListOfEdges.add(Pair(e.source.index, e.destination.index))
        }
        val expectedListOfEdges = listOf(Pair(0, 2), Pair(1, 0), Pair(2, 1), Pair(3, 1), Pair(3, 4), Pair(4, 3), Pair(5, 4))
        assertEquals(expectedListOfEdges, actualListOfEdges)
    }

    @Test
    fun `find strong components in directed graph`() {
        val componentsList = algorithms.findStrongComponents(graph)
        val actualComponentsList = mutableListOf<List<Int>>()
        var actualComponent = mutableListOf<Int>()
        componentsList.forEach { component ->
            component.forEach { v ->
                actualComponent.add(v.index)
            }
            actualComponentsList.add(actualComponent)
            actualComponent = mutableListOf()
        }
        val expectedComponentsList = listOf(listOf(0, 2, 1), listOf(3, 4), listOf(5))
        assertEquals(expectedComponentsList, actualComponentsList)
    }
}
