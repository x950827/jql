package com.github.x950827.jql

open class Field {
    lateinit var value: String

    companion object {
        val PROJECT = fromString("project")

        fun fromString(str: String) = Field().apply { value = "$str" }
        fun custom(id: Int) = Field().apply { value = "cf[$id]" }
    }
}