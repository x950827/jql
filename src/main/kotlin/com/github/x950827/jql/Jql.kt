package com.github.x950827.jql

fun withJql(ctx: Jql.Builder.() -> Unit) = Jql.Builder().apply(ctx).build()

open class Jql private constructor(
    protected val conditions: Collection<Condition>,
    protected var orderBy: Field?,
    protected var orderDir: OrderDir
) {

    class Builder() {
        protected val conditions = mutableListOf<Condition>()
        protected var orderBy: Field? = null
        protected var orderDir: OrderDir = OrderDir.DESC

        fun condition(ctx: Condition.Builder.() -> Unit) = apply {
            conditions += Condition.Builder().apply(ctx).build()
        }

        fun and(ctx: Condition.Builder.() -> Unit) = apply {
            conditions += Condition.Builder(ConditionType.AND).apply(ctx).build()
        }

        fun or(ctx: Condition.Builder.() -> Unit) = apply {
            conditions += Condition.Builder(ConditionType.OR).apply(ctx).build()
        }

        fun orderBy(orderBy: String): Field {
            this.orderBy = Field.fromString(orderBy)
            return this.orderBy!!
        }

        fun Field.desc() {
            orderDir = OrderDir.DESC
        }

        fun Field.asc() {
            orderDir = OrderDir.ASC
        }

        fun build() = Jql(conditions, orderBy, orderDir)
    }
    
    fun queryString(): String {
        if (conditions.isNullOrEmpty()) return ""
        
        var str = conditions.joinToString(" ") { it.toString() }
        
        if (orderBy != null) {
            str += " ${ConditionType.ORDER_BY.value} ${orderBy!!.value} ${orderDir.value}"
        }
        return str
    }

    override fun toString() = queryString()
}