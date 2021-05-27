package com.github.daniiltagan.jql

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

        fun customField(fieldId: Int): Field {
            this.field = Field.custom(fieldId)
            return this.field!!
        }

        fun project(): Field {
            this.field = Field.PROJECT
            return this.field!!
        }

        fun Field.empty() {
            operator = Operator.EQUALS
            this@Builder.value = Value.EMPTY
        }

        fun Field.notEmpty() {
            operator = Operator.NOT_EQUALS
            this@Builder.value = Value.EMPTY
        }

        fun Field.isEmpty() {
            operator = Operator.IS
            this@Builder.value = Value.EMPTY
        }

        fun Field.isNotEmpty() {
            operator = Operator.IS_NOT
            this@Builder.value = Value.EMPTY
        }

        fun Field.`null`() {
            operator = Operator.EQUALS
            this@Builder.value = Value.NULL
        }

        fun Field.notNull() {
            operator = Operator.NOT_EQUALS
            this@Builder.value = Value.NULL
        }

        fun Field.isNull() {
            operator = Operator.IS
            this@Builder.value = Value.NULL
        }

        fun Field.isNotNull() {
            operator = Operator.IS_NOT
            this@Builder.value = Value.NULL
        }

        fun Field.eq(value: String) {
            operator = Operator.EQUALS
            this@Builder.value = Value.fromString(value)
        }

        fun Field.eq(date: LocalDate) {
            operator = Operator.EQUALS
            this@Builder.value = Value.fromDate(date)
        }

        fun Field.eq(dateTime: LocalDateTime) {
            operator = Operator.EQUALS
            this@Builder.value = Value.fromDateTime(dateTime)
        }

        fun Field.notEq(value: String) {
            operator = Operator.NOT_EQUALS
            this@Builder.value = Value.fromString(value)
        }

        fun Field.notEq(date: LocalDate) {
            operator = Operator.NOT_EQUALS
            this@Builder.value = Value.fromDate(date)
        }

        fun Field.notEq(dateTime: LocalDateTime) {
            operator = Operator.NOT_EQUALS
            this@Builder.value = Value.fromDateTime(dateTime)
        }

        fun Field.`in`(array: Collection<String>) {
            operator = Operator.IN
            this@Builder.value = Value.fromArray(array)
        }

        fun Field.`in`(vararg value: String) {
            operator = Operator.IN
            this@Builder.value = Value.fromArray(value.toList())
        }

        fun Field.notIn(array: Collection<String>) {
            operator = Operator.NOT_IN
            this@Builder.value = Value.fromArray(array)
        }

        fun Field.notIn(vararg value: String) {
            operator = Operator.NOT_IN
            this@Builder.value = Value.fromArray(value.toList())
        }

        fun Field.changedAfter(date: LocalDate) {
            operator = Operator.CHANGED_AFTER
            this@Builder.value = Value.fromDate(date)
        }

        fun Field.changedAfter(dateTime: LocalDateTime) {
            operator = Operator.CHANGED_AFTER
            this@Builder.value = Value.fromDateTime(dateTime)
        }

        fun Field.more(value: String) {
            operator = Operator.MORE
            this@Builder.value = Value.fromString(value)
        }

        fun Field.more(date: LocalDate) {
            operator = Operator.MORE
            this@Builder.value = Value.fromDate(date)
        }

        fun Field.more(dateTime: LocalDateTime) {
            operator = Operator.MORE
            this@Builder.value = Value.fromDateTime(dateTime)
        }

        fun Field.moreEq(value: String) {
            operator = Operator.MORE_OR_EQUAL
            this@Builder.value = Value.fromString(value)
        }

        fun Field.moreEq(date: LocalDate) {
            operator = Operator.MORE_OR_EQUAL
            this@Builder.value = Value.fromDate(date)
        }

        fun Field.moreEq(dateTime: LocalDateTime) {
            operator = Operator.MORE_OR_EQUAL
            this@Builder.value = Value.fromDateTime(dateTime)
        }

        fun Field.less(value: String) {
            operator = Operator.LESS
            this@Builder.value = Value.fromString(value)
        }

        fun Field.less(date: LocalDate) {
            operator = Operator.LESS
            this@Builder.value = Value.fromDate(date)
        }

        fun Field.less(dateTime: LocalDateTime) {
            operator = Operator.LESS
            this@Builder.value = Value.fromDateTime(dateTime)
        }

        fun Field.lessEq(value: String) {
            operator = Operator.LESS_THAN_OR_EQUAL
            this@Builder.value = Value.fromString(value)
        }

        fun Field.lessEq(date: LocalDate) {
            operator = Operator.LESS_THAN_OR_EQUAL
            this@Builder.value = Value.fromDate(date)
        }

        fun Field.lessEq(dateTime: LocalDateTime) {
            operator = Operator.LESS_THAN_OR_EQUAL
            this@Builder.value = Value.fromDateTime(dateTime)
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