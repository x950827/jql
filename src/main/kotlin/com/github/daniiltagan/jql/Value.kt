package com.github.daniiltagan.jql

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

open class Value {
    lateinit var value: String

    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")!!
        private val DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")!!

        val EMPTY = Value().apply { value = "empty" }
        val NULL = Value().apply { value = "null" }

        fun fromString(str: String) = Value().apply { value = "'$str'" }
        fun fromDate(date: LocalDate) = Value().apply { value = "'${date.format(DATE_FORMATTER)}'" }
        fun fromDateTime(dateTime: LocalDateTime) = Value().apply { value = "'${dateTime.format(DATETIME_FORMATTER)}'" }
        fun fromArray(array: Collection<String>) = Value().apply { value = "(${array.joinToString(", ") { "'$it'" }})" }
    }
}