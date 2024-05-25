package io.sqlite

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class EdgeEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EdgeEntity>(Edges)

    var start by VertexEntity referencedOn Edges.start
    var end by VertexEntity referencedOn Edges.end
    var weight by Edges.weight
    var graph by GraphEntity referencedOn Edges.graph
}