package com.github.x950827.jql

import java.time.LocalDate
import java.time.LocalDateTime

open class Condition private constructor(
    protected var field: Field? = null,
    protected var operator: Operator? = null,
    protected var value: Value? = null,
    protected val conditions: Collection<Condition>? = null,
    protected val type: ConditionType? = null
) {

    class Builder(private val type: ConditionType? = null) {
        protected var field: Field? = null
        protected var operator: Operator? = null
        protected var value: Value? = null
        protected var conditions: MutableCollection<Condition>? = null

        fun condition(ctx: Builder.() -> Unit) = apply {
            if (conditions == null)
                conditions = mutableListOf()

            conditions!!.plusAssign(Builder().apply(ctx).build())
        }

        fun and(ctx: Builder.() -> Unit) = apply {
            if (conditions == null)
                conditions = mutableListOf()

            conditions!!.plusAssign(Builder(ConditionType.AND).apply(ctx).build())
        }

        fun or(ctx: Builder.() -> Unit) = apply {
            if (conditions == null)
                conditions = mutableListOf()

            conditions!!.plusAssign(Builder(ConditionType.OR).apply(ctx).build())
        }

        fun field(field: String): Field {
            this.field = Field.fromString(field)
            return this.field!!
        }

        fun field(field: Field): Field {
            this.field = field
            return this.field!!
        }

        fun customField(fieldId: Int) = field(Field.custom(fieldId))
        fun project() = field(Field.PROJECT)
        fun status() = field(Field.STATUS)
        fun created() = field(Field.CREATED)
        fun updated() = field(Field.UPDATED)
        fun assignee() = field(Field.ASSIGNEE)
        fun reporter() = field(Field.REPORTER)
        fun issueType() = field(Field.ISSUE_TYPE)
        fun priority() = field(Field.PRIORITY)

        private fun expression(operator: Operator, value: Value) {
            this.operator = operator
            this.value = value
        }

        fun Field.empty() {
            expression(Operator.EQUALS, Value.EMPTY)
        }

        fun Field.notEmpty() {
            expression(Operator.NOT_EQUALS, Value.EMPTY)
        }

        fun Field.isEmpty() {
            expression(Operator.IS, Value.EMPTY)
        }

        fun Field.isNotEmpty() {
            expression(Operator.IS_NOT, Value.EMPTY)
        }

        fun Field.`null`() {
            expression(Operator.EQUALS, Value.NULL)
        }

        fun Field.notNull() {
            expression(Operator.NOT_EQUALS, Value.NULL)
        }

        fun Field.isNull() {
            expression(Operator.IS, Value.NULL)
        }

        fun Field.isNotNull() {
            expression(Operator.IS_NOT, Value.NULL)
        }

        fun Field.eq(value: String) {
            expression(Operator.EQUALS, Value.fromString(value))
        }

        fun Field.eq(date: LocalDate) {
            expression(Operator.EQUALS, Value.fromDate(date))
        }

        fun Field.eq(dateTime: LocalDateTime) {
            expression(Operator.EQUALS, Value.fromDateTime(dateTime))
        }

        fun Field.notEq(value: String) {
            expression(Operator.NOT_EQUALS, Value.fromString(value))
        }

        fun Field.notEq(date: LocalDate) {
            expression(Operator.NOT_EQUALS, Value.fromDate(date))
        }

        fun Field.notEq(dateTime: LocalDateTime) {
            expression(Operator.NOT_EQUALS, Value.fromDateTime(dateTime))
        }

        fun Field.`in`(array: Collection<String>) {
            expression(Operator.IN, Value.fromArray(array))
        }

        fun Field.`in`(vararg value: String) {
            expression(Operator.IN, Value.fromArray(value.toList()))
        }

        fun Field.notIn(array: Collection<String>) {
            expression(Operator.NOT_IN, Value.fromArray(array))
        }

        fun Field.notIn(vararg value: String) {
            expression(Operator.NOT_IN, Value.fromArray(value.toList()))
        }

        fun Field.changedAfter(date: LocalDate) {
            expression(Operator.CHANGED_AFTER, Value.fromDate(date))
        }

        fun Field.changedAfter(dateTime: LocalDateTime) {
            expression(Operator.CHANGED_AFTER, Value.fromDateTime(dateTime))
        }

        fun Field.more(value: String) {
            expression(Operator.MORE, Value.fromString(value))
        }

        fun Field.more(date: LocalDate) {
            expression(Operator.MORE, Value.fromDate(date))
        }

        fun Field.more(dateTime: LocalDateTime) {
            expression(Operator.MORE, Value.fromDateTime(dateTime))
        }

        fun Field.moreEq(value: String) {
            expression(Operator.MORE_OR_EQUAL, Value.fromString(value))
        }

        fun Field.moreEq(date: LocalDate) {
            expression(Operator.MORE_OR_EQUAL, Value.fromDate(date))
        }

        fun Field.moreEq(dateTime: LocalDateTime) {
            expression(Operator.MORE_OR_EQUAL, Value.fromDateTime(dateTime))
        }

        fun Field.less(value: String) {
            expression(Operator.LESS, Value.fromString(value))
        }

        fun Field.less(date: LocalDate) {
            expression(Operator.LESS, Value.fromDate(date))
        }

        fun Field.less(dateTime: LocalDateTime) {
            expression(Operator.LESS, Value.fromDateTime(dateTime))
        }

        fun Field.lessEq(value: String) {
            expression(Operator.LESS_THAN_OR_EQUAL, Value.fromString(value))
        }

        fun Field.lessEq(date: LocalDate) {
            expression(Operator.LESS_THAN_OR_EQUAL, Value.fromDate(date))
        }

        fun Field.lessEq(dateTime: LocalDateTime) {
            expression(Operator.LESS_THAN_OR_EQUAL, Value.fromDateTime(dateTime))
        }

        fun build(): Condition {
            if (conditions.isNullOrEmpty()) {
                return Condition(
                    field.validate("Field"),
                    operator.validate("Operator"),
                    value.validate("Value"),
                    type = type
                )
            }
            return Condition(type = type, conditions = conditions)
        }

        private fun <T> T?.validate(type: String) = this ?: throw IllegalStateException("$type cannot be null")
    }

    fun queryString(): String {
        return when {
            !conditions.isNullOrEmpty() -> "${if (type == null) "" else "${type.value} "}(${conditions!!.joinToString(" ") { it.queryString() }})"
            else -> "${if (type == null) "" else "${type.value} "}${field.getOrThrow()} ${operator.getOrThrow()} ${value.getOrThrow()}"
        }
    }

    override fun toString() = queryString()

    private fun Field?.getOrThrow() = this?.value ?: throw IllegalStateException("Field cannot be null")

    private fun Operator?.getOrThrow() = this?.value ?: throw IllegalStateException("Operator cannot be null")

    private fun Value?.getOrThrow() = this?.value ?: throw IllegalStateException("Value cannot be null")

    enum class Operator(val value: String) {
        EQUALS("="),
        NOT_EQUALS("!="),
        IS_NOT("is not"),
        IS("is"),
        NOT_IN("not in"),
        IN("in"),
        CHANGED_AFTER("changed after"),
        MORE(">"),
        MORE_OR_EQUAL(">="),
        LESS("<"),
        LESS_THAN_OR_EQUAL("<=")
    }
}