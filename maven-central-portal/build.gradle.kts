plugins {
    id("gradle-plugins.common-conventions")
}

gradlePlugin {
    setupPlugin(
        project.group,
        "maven-central-portal",
        "Siro's Gradle shared build logic, for deployment to Maven Central Portal"
    )
}
