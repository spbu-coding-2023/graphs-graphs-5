package view

data class Neo4jInput(
    var uri: String = "",
    var login: String = "",
    var password: String = "",
    var isUpdated: Boolean = false,
    var isUndirected: Boolean = false
)
