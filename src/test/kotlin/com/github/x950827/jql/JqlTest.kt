package com.github.x950827.jql

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class JqlTest {
    
    @Test
    fun test() {
        val jql = withJql {
            condition { project().eq("TPWEBAPP") }
            and { field("status").notIn("Done", "Closed") }
            and { 
                condition { customField(14409).isEmpty() }
                or { customField(14409).eq("Tuners VX") }
            }
            and { field("created").more(LocalDateTime.now().minusDays(5)) }
            orderBy("created").asc()
        }
        assertNotNull(jql)
    }
}