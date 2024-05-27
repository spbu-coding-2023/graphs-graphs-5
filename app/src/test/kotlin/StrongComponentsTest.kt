import model.DirectedGraph
import model.Vertex
import model.algorithms.DirectedGraphAlgorithmsImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class StrongComponentsTestOnDirectedGraph {
    private lateinit var graph: DirectedGraph<Int>
    private lateinit var algorithms: DirectedGraphAlgorithmsImpl<Int>

    @BeforeEach
    fun setup() {
        algorithms = DirectedGraphAlgorithmsImpl()
        graph = DirectedGraph()
        for (i in 0..5) {
            graph.addVertex(i)
        }
    }

    private fun convertToListOfInt(componentsList: List<Pair<Vertex<Int>, Int>>): List<Int> {
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
        return actualComponentsList
    }

    private fun addEdgesFrom0To4() {
        for (i in 0..4) {
            graph.addEdge(Vertex(i, i), Vertex(i + 1, i + 1))
        }
    }

    @Test
    fun `find strong components in disconnected graph`() {
        val componentsList = algorithms.findStrongComponents(graph)
        val actualComponentsList = convertToListOfInt(componentsList)
        val expectedComponentsList = listOf(1, 2, 3, 4, 5, 6)
        assertEquals(expectedComponentsList, actualComponentsList)
    }

    @Test
    fun `find strong components in simple cyclic graph`() {
        addEdgesFrom0To4()
        graph.addEdge(Vertex(5, 5), Vertex(0, 0))
        val componentsList = algorithms.findStrongComponents(graph)
        val actualComponentsList = convertToListOfInt(componentsList)
        val expectedComponentsList = listOf(0, 0, 0, 0, 0, 0)
        assertEquals(expectedComponentsList, actualComponentsList)
    }

    @Test
    fun `find strong components in chain of nodes`() {
        addEdgesFrom0To4()
        val componentsList = algorithms.findStrongComponents(graph)
        val actualComponentsList = convertToListOfInt(componentsList)
        val expectedComponentsList = listOf(0, 1, 2, 3, 4, 5)
        assertEquals(expectedComponentsList, actualComponentsList)
    }

    /*
    *  0 -- 1
    *   \  /
    *    2 -- 3 -- 4 -- 5
    *
    */
    @Test
    fun `find strong components in graph with combination of cycles and chains`() {
        addEdgesFrom0To4()
        graph.addEdge(Vertex(2, 2), Vertex(0, 0))
        val componentsList = algorithms.findStrongComponents(graph)
        val actualComponentsList = convertToListOfInt(componentsList)
        val expectedComponentsList = listOf(0, 0, 0, 1, 2, 3)
        assertEquals(expectedComponentsList, actualComponentsList)
    }

    /*
    *      2        4
    *    /  \      / \
    *   1    3     \ /
    *    \  /       5
    *     0
    *
    */
    @Test
    fun `find strong components among multiple disjoint cycles`() {
        for (i in 0..2) {
            graph.addEdge(Vertex(i, i), Vertex(i + 1, i + 1))
        }
        graph.addEdge(Vertex(3, 3), Vertex(0, 0))
        graph.addEdge(Vertex(4, 4), Vertex(5, 5))
        graph.addEdge(Vertex(5, 5), Vertex(4, 4))
        val componentsList = algorithms.findStrongComponents(graph)
        val actualComponentsList = convertToListOfInt(componentsList)
        val expectedComponentsList = listOf(1, 1, 1, 1, 2, 2)
        assertEquals(expectedComponentsList, actualComponentsList)
    }

    /*
    *     2      4
    *    /  \  / |
    *   1    3   |
    *    \  /  \ |
    *     0     5
    *
    */
    @Test
    fun `find strong components in graph with interlinked subgraphs`() {
        addEdgesFrom0To4()
        graph.addEdge(Vertex(3, 3), Vertex(0, 0))
        graph.addEdge(Vertex(5, 5), Vertex(3, 3))
        val componentsList = algorithms.findStrongComponents(graph)
        val actualComponentsList = convertToListOfInt(componentsList)
        val expectedComponentsList = listOf(0, 0, 0, 0, 0, 0)
        assertEquals(expectedComponentsList, actualComponentsList)
    }

    /*
    *     2       4 -- 3
    *     | \   / |
    *     |   0   |
    *     | /  \  |
    *     1      5
    *
    */
    @Test
    fun `find strong components in directed acyclic graph`() {
        graph.addEdge(Vertex(0, 0), Vertex(1, 1))
        graph.addEdge(Vertex(0, 0), Vertex(2, 2))
        graph.addEdge(Vertex(0, 0), Vertex(5, 5))
        graph.addEdge(Vertex(1, 1), Vertex(2, 2))
        graph.addEdge(Vertex(4, 4), Vertex(3, 3))
        graph.addEdge(Vertex(4, 4), Vertex(5, 5))
        graph.addEdge(Vertex(4, 4), Vertex(0, 0))
        val componentsList = algorithms.findStrongComponents(graph)
        val actualComponentsList = convertToListOfInt(componentsList)
        val expectedComponentsList = listOf(1, 2, 3, 4, 5, 6)
        assertEquals(expectedComponentsList, actualComponentsList)
    }
}
