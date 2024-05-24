package io

import model.*
import org.neo4j.driver.*
//import mu.KotlinLogging

import java.io.Closeable

//private val logger = KotlinLogging.logger { }

class Neo4jRepo<V>(uri: String, user: String, password: String) : Closeable {

    private val driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))
    private val session = driver.session()

    fun getGraphFromNeo4j(graph: Graph<Any>): Graph<Any> {
        val graphInput = readGraph()
        val vertices = graphInput.first.toList()
        val edges = graphInput.second.toList()
        for (i in vertices.indices) {
            graph.addVertex(vertices[i].data, vertices[i].DBindex)
        }
        for (i in edges.indices) {
            if (!graph.edges.any { it.source.dBIndex == edges[i].source.dBIndex && it.destination.dBIndex == edges[i].destination.dBIndex }) {
                graph.addEdge(edges[i].source, edges[i].destination)
            }
        }
        return graph
    }

    fun readGraph(): Pair<MutableSet<Vertex<Any>>, MutableList<Edge<Any>>> {
        val query = """
        MATCH (v1:Vertex)-[r:CON_TO]->(v2:Vertex)
        RETURN 
        coalesce(r.weight, 1.0) AS weight,
        id(v1) AS sourceIndex, 
        v1.data AS sourceData, 
        id(v2) AS destinationIndex, 
        v2.data AS destinationData
        """
        val edgeList = mutableListOf<Edge<Any>>()
        val vertexList = mutableSetOf<Vertex<Any>>()
        val indexMap = mutableMapOf<Int, Int>() // Map to track original indices to new indices

        session.readTransaction { tx ->
            val result = tx.run(query)
            var currentIndex = 0

            result.list().forEach { rec ->
                val sourceOriginalIndex = rec["sourceIndex"].asInt()
                val destinationOriginalIndex = rec["destinationIndex"].asInt()

                val sourceIndex = indexMap.computeIfAbsent(sourceOriginalIndex) {
                    currentIndex++
                    currentIndex - 1
                }
                val destinationIndex = indexMap.computeIfAbsent(destinationOriginalIndex) {
                    currentIndex++
                    currentIndex - 1
                }

                val source = Vertex<Any>(
                    sourceIndex,
                    rec["sourceData"],
                    sourceOriginalIndex
                )

                val destination = Vertex<Any>(
                    destinationIndex,
                    rec["destinationData"],
                    destinationOriginalIndex
                )

                vertexList.add(source)
                vertexList.add(destination)

                edgeList.add(Edge(
                    currentIndex,
                    source,
                    destination,
                    rec["weight"].asDouble()
                ))
            }

            //for disconnected vertices
            val queryVert = """
            MATCH (v:Vertex)
            WHERE NOT (v)--() AND NOT ()--(v)
            RETURN id(v) AS index, v.data AS data
            """
            val disconnectedVert = tx.run(queryVert)
            disconnectedVert.list().forEach { rec ->
                val originalIndex = rec["index"].asInt()
                val internalIndex = indexMap.computeIfAbsent(originalIndex) {
                    currentIndex++
                    currentIndex - 1
                }

                val vertex = Vertex<Any>(
                    internalIndex,
                    rec["data"],
                    originalIndex
                )

                vertexList.add(vertex)
            }
            tx.commit()
        }
        return Pair(vertexList, edgeList)
    }

    fun <V> saveClusterDetectionResults(graph: Graph<V>, clusterPartition: IntArray) {
        val vertList = graph.vertices.toList()
        session.beginTransaction().use { tx ->
            for (i in clusterPartition.indices) {
                val vertex = vertList[i]
                val dbIndex = vertex.DBindex
                val clusterNumber = clusterPartition[i]
                val query = """
                MATCH (v)
                WHERE id(v) = $dbIndex
                SET v.clusterNumber = $clusterNumber
                SET v.programIndex = ${vertex.index}
                RETURN v
            """
                tx.run(query)
            }
            tx.commit()
        }
    }

    fun <V> saveKeyVerticesResults(graph: Graph<V>, rankingList: List<Double>) {
        val vertList = graph.vertices.toList()
        session.beginTransaction().use { tx ->
            for (i in rankingList.indices) {
                val vertex = vertList[i]
                val dbIndex = vertex.DBindex
                val keyVertexRank = rankingList[i]
                val query = """
                MATCH (v)
                WHERE id(v) = $dbIndex
                SET v.keyVertexRank = $keyVertexRank
                SET v.programIndex = ${vertex.index}
                RETURN v
            """
                tx.run(query)
            }
            tx.commit()
        }
    }

    fun cleanOutdatedAlgoResults() {
        val clusterQuery = """
        MATCH (n)
        REMOVE n.clusterNumber
        """
        val keyQuery = """
        MATCH (n)
        REMOVE n.keyVertexRank
        """
        session.run(clusterQuery)
        session.run(keyQuery)
    }

    fun getClusteringResults(): List<Int>? {
        val query = """
        MATCH (n)
        RETURN n.clusterNumber AS clusterNumber
        ORDER BY n.programIndex
        """
        val result = session.run(query)
        return result.list { record -> record["clusterNumber"].asInt() }
    }

    fun getKeyVerticesResults(): List<Double>? {
        val query = """
        MATCH (n)
        RETURN n.keyVertexRank AS keyVertexRank
        ORDER BY n.programIndex
        """
        val result = session.run(query)
        return result.list { record -> record["keyVertexRank"].asDouble() }
    }

    override fun close() {
        session.close()
        driver.close()
    }
}

//fun main() {
//    val repo = Neo4jRepo<Any>("bolt://localhost:7687","neo4j", "my my, i think we have a spy ;)")
////    val list = repo.getAllVertices()
////    println(list)
//    val list2 = repo.readGraph()
////    println(list2.first)
////    println(list2.second)
//    //repo.saveClusterDetectionResults(2, "newLabel")
//}