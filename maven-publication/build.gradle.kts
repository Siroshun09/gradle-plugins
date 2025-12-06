plugins {
    id("gradle-plugins.common-conventions")
}

gradlePlugin {
    setupPlugin(
        project.group,
        "maven-publication",
        "Siro's Gradle shared build logic, for publishing artifacts to the Maven repository."
    )
}
