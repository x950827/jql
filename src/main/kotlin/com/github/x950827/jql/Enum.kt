package com.github.x950827.jql

enum class OrderDir(val value: String) {
    ASC("asc"),
    DESC("desc")
}

enum class ConditionType(val value: String) {
    AND("and"),
    OR("or"),
    ORDER_BY("order by")
}