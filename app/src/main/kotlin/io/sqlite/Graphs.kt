package io.sqlite

import org.jetbrains.exposed.dao.id.IntIdTable

object Graphs : IntIdTable() {
    val name = varchar("name", 255)
    val metadata = text("metadata")
}