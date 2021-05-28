package com.github.x950827.jql

open class Field {
    lateinit var value: String

    companion object {
        val PROJECT = fromString("project")
        val STATUS = fromString("status")
        val CREATED = fromString("created")
        val UPDATED = fromString("updated")
        val ASSIGNEE = fromString("assignee")
        val REPORTER = fromString("reporter")
        val ISSUE_TYPE = fromString("issuetype")
        val PRIORITY = fromString("priority")

        fun fromString(str: String) = Field().apply { value = "$str" }
        fun custom(id: Int) = Field().apply { value = "cf[$id]" }
    }
}