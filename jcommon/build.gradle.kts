plugins {
    id("gradle-plugins.common-conventions")
}

gradlePlugin {
    setupPlugin(project.group, "jcommon", "Siro's Gradle shared build logic, for setting up as a basic Java project.") {
        implementationClass = "dev.siroshun.gradle.plugins.jcommon.JCommonPlugin"
    }
}
