# JQL

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.x950827/jql/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.x950827/jql/badge.svg?style=for-the-badge)
[![Tests](https://github.com/x950827/jql/actions/workflows/tests.yml/badge.svg?branch=master)](https://github.com/x950827/jql/actions/workflows/tests.yml)


This library provides a simple Jira Query Language (JQL) builder for kotlin projects.

More info about JQL: https://www.atlassian.com/software/jira/guides/expand-jira/jql

## Install

```kotlin
dependencies {
    implementation("io.github.x950827:jql:0.0.1")
}
```

## Usage

```kotlin
val jql = withJql { 
        condition { project().eq("Development") }
        and { status().`in`("In Progress") }
        and {
            condition { field("reporter").notEq("Ivan Ivanovich") }
            or { customField(32155).notIn("value1", "value2") }
        }
        orderBy("created").asc()
    }

jql.toString() // or jql.queryString()
// equals to 
// project = 'Development' 
// and status in ('In Progress') 
// and (reporter != 'Ivan Ivanovich' or cf[32155] not in ('value1', 'value2')) 
// order by created asc
```