package dev.siroshun.gradle.plugins.aggregated.javadoc

data class AggregatingSettings(
    val links: MutableSet<String> = mutableSetOf(),
    val includes: MutableSet<String> = mutableSetOf(),
    val excludes: MutableSet<String> = mutableSetOf(),
)
