plugins {
    id("gradle-plugins.common-conventions")
}

gradlePlugin {
    setupPlugin(project.group, "bundler", "Siro's Gradle shared build logic, for bundling dependencies into a single jar.")
}

dependencies {
    implementation(libs.shadow)
}
