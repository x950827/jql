package com.github.x950827.jql

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.LocalDateTime

class JqlTest {

    companion object {
        @JvmStatic
        fun queries() = listOf(
            Arguments.of("project = 'Test Project'", withJql { condition { project().eq("Test Project") } }),
            Arguments.of(
                "project = 'Test Project' and testField = 'testValue'",
                withJql {
                    condition { project().eq("Test Project") }
                    and { field("testField").eq("testValue") }
                }
            ),
            Arguments.of(
                "project = 'Test Project' or testField = 'testValue'",
                withJql {
                    condition { project().eq("Test Project") }
                    or { field("testField").eq("testValue") }
                }
            ),
            Arguments.of(
                "project = 'Test Project' and testField = 'testValue' order by created asc",
                withJql {
                    condition { project().eq("Test Project") }
                    and { field("testField").eq("testValue") }
                    orderBy("created").asc()
                }
            ),
            Arguments.of(
                "project = 'Test Project' and (testField0 = 'testValue0' or testField1 != 'testValue1')",
                withJql {
                    condition { project().eq("Test Project") }
                    and {
                        condition { field("testField0").eq("testValue0") }
                        or { field("testField1").notEq("testValue1") }
                    }
                }
            ),
            Arguments.of(
                "project = 'Test Project' and (testField0 = 'testValue0' and testField1 != 'testValue1')",
                withJql {
                    condition { project().eq("Test Project") }
                    and {
                        condition { field("testField0").eq("testValue0") }
                        and { field("testField1").notEq("testValue1") }
                    }
                }
            ),
            Arguments.of(
                "project = 'Test Project' and (testField0 = 'testValue0' and testField1 != 'testValue1') order by updated desc",
                withJql {
                    condition { project().eq("Test Project") }
                    and {
                        condition { field("testField0").eq("testValue0") }
                        and { field("testField1").notEq("testValue1") }
                    }
                    orderBy("updated")
                }
            ),
            Arguments.of(
                "project = 'Test Project' and testField0 = 'testValue0' and testField1 = $TEST_DATE_TEXT and testField2 = $TEST_DATE_TIME_TEXT",
                withJql {
                    condition { project().eq("Test Project") }
                    and { field("testField0").eq("testValue0") }
                    and { field("testField1").eq(TEST_DATE) }
                    and { field("testField2").eq(TEST_DATE_TIME) }
                }
            )
        )

        private val TEST_DATE = LocalDate.of(2021, 5, 21)
        private val TEST_DATE_TIME = LocalDateTime.of(2021, 5, 21, 0, 0, 0)

        private const val TEST_DATE_TEXT = "'2021-05-21'"
        private const val TEST_DATE_TIME_TEXT = "'2021-05-21 00:00'"
    }

    @Test
    fun `should create jql`() {
        val jql = withJql { }
        assertNotNull(jql)
        assertEquals(Jql::class, jql::class)
    }

    @ParameterizedTest
    @MethodSource("queries")
    fun `should create valid Jql`(expected: String, actual: Jql) {
        assertEquals(expected, actual.queryString())
    }
}