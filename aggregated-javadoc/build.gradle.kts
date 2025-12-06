plugins {
    id("gradle-plugins.common-conventions")
}

gradlePlugin {
    setupPlugin(
        project.group,
        "aggregated-javadoc",
        "Siro's Gradle shared build logic, for generating aggregated Javadoc from sub projects."
    )
}

