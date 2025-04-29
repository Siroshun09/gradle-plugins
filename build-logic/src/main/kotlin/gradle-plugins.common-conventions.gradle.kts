plugins {
    id("org.gradle.kotlin.embedded-kotlin")
    id("org.gradle.kotlin.kotlin-dsl")
    id("com.gradle.plugin-publish")
    signing
}

repositories {
    mavenCentral()
}

publishing {
    publications.withType(MavenPublication::class).configureEach {
        pom {
            name.set(project.name)
            url.set("https://github.com/Siroshun09/gradle-plugins")
            description.set("A repository for managing shared build logics between my projects.")

            licenses {
                license {
                    name.set("APACHE LICENSE, VERSION 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }

            developers {
                developer {
                    name.set("Siroshun09")
                }
            }

            scm {
                connection.set("scm:git:https://github.com/Siroshun09/gradle-plugins.git")
                developerConnection.set("scm:git@github.com:Siroshun09/gradle-plugins.git")
                url.set("https://github.com/Siroshun09/gradle-plugins")
            }

            issueManagement {
                system.set("GitHub Issues")
                url.set("https://github.com/Siroshun09/gradle-plugins/issues")
            }

            ciManagement {
                system.set("GitHub Actions")
                url.set("https://github.com/Siroshun09/gradle-plugins/runs")
            }
        }
    }
    repositories {
        maven {
            name = "staging"
            url = uri(rootProject.layout.buildDirectory.dir("staging-$version"))
        }
    }
}

signing {
    useGpgCmd()
    publishing.publications.withType(MavenPublication::class).configureEach {
        sign(this)
    }
}
