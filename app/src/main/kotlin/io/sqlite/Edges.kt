package io.sqlite

import org.jetbrains.exposed.dao.id.IntIdTable

object Edges : IntIdTable() {
    val start = reference("start", Vertices)
    val end = reference("end", Vertices)
    val weight = double("weight")
    val graph = reference("graph", Graphs)
}