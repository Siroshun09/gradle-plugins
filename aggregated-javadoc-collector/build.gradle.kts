plugins {
    id("gradle-plugins.common-conventions")
}

gradlePlugin {
    setupPlugin(project.group, "aggregated-javadoc-collector", "Siro's Gradle shared build logic, for collecting Javadoc tasks to aggregate.")
}

dependencies {
    compileOnlyApi(projects.gradlePluginsAggregatedJavadoc)
}
