import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
    id("org.jetbrains.dokka") version "1.4.32"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    `maven-publish`
    signing
}

apply("$rootDir/publish.gradle.kts")

group = "io.github.x950827"
version = "0.0.1.2"

val sourcesJar = tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks {
    dokkaJavadoc {
        outputDirectory.set(javadoc.get().destinationDir)
        inputs.dir("src/main/kotlin")
    }
}
val dokkaJavadocJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
    dependsOn(tasks.dokkaJavadoc)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String
            
            artifact(sourcesJar)
            artifact(dokkaJavadocJar)
            
            pom { 
                name.set("jql")
                description.set("Simple Jira Query Language builder for kotlin projects")
                url.set("https://github.com/x950827/jql")
                licenses { 
                    license { 
                        name.set("Jql license")
                        url.set("https://github.com/x950827/jql/blob/master/LICENSE.md")
                    }
                }
                developers { 
                    developer { 
                        id.set("x950827")
                        name.set("Daniil Tagan")
                        email.set("x950827@yandex.ru")
                    }
                }
                scm { 
                    connection.set("scm:git:github.com/x950827/jql.git")
                    developerConnection.set("scm:git:github.com/x950827/jql.git")
                    url.set("https://github.com/x950827/jql")
                }
            }

            from(components["kotlin"])
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId.set(extra["sonatypeStagingProfileId"] as String)
            username.set(extra["ossrhUsername"] as String)
            password.set(extra["ossrhPassword"] as String)
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}