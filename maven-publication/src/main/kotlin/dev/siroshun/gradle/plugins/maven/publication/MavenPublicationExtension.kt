package dev.siroshun.gradle.plugins.maven.publication

import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPom

interface MavenPublicationExtension {
    val repositoriesAction: Property<Action<RepositoryHandler>>
    val pomAction: Property<Action<MavenPom>>

    fun repositories(action: Action<RepositoryHandler>) {
        repositoriesAction.set(action)
    }

    fun localRepository(dir: DirectoryProperty) {
        repositories {
            maven {
                url = dir.get().asFile.toURI()
            }
        }
    }

    fun pom(action: Action<MavenPom>) {
        if (pomAction.isPresent) {
            val current = pomAction.get()
            pomAction.set {
                current.execute(this)
                action.execute(this)
            }
        } else {
            pomAction.set(action)
        }
    }

    fun description(content: String) {
        pom {
            description.set(content)
        }
    }

    fun license(name: String, url: String) {
        pom {
            licenses {
                license {
                    getName().set(name)
                    getUrl().set(url)
                }
            }
        }
    }

    fun apacheLicense() {
        license("APACHE LICENSE, VERSION 2.0", "https://www.apache.org/licenses/LICENSE-2.0")
    }

    fun gplV3License() {
        license("GNU General Public License, Version 3.0", "https://www.gnu.org/licenses/gpl-3.0.txt")
    }

    fun mitLicense() {
        license("MIT License", "https://opensource.org/licenses/MIT")
    }

    fun developer(name: String) {
        pom {
            developers {
                developer {
                    this.id.set(name)
                    this.name.set(name)
                }
            }
        }
    }

    fun github(repo: String) {
        pom {
            url.set("https://github.com/$repo")

            scm {
                connection.set("scm:git:https://github.com/$repo.git")
                developerConnection.set("scm:git@github.com:$repo.git")
                url.set("https://github.com/$repo")
            }

            issueManagement {
                system.set("GitHub Issues")
                url.set("https://github.com/$repo/issues")
            }

            ciManagement {
                system.set("GitHub Actions")
                url.set("https://github.com/$repo/runs")
            }
        }
    }
}
