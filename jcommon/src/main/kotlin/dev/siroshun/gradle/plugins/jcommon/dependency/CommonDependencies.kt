package dev.siroshun.gradle.plugins.jcommon.dependency

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.accessors.runtime.addConfiguredDependencyTo

class CommonDependencies(delegate: DependencyHandler) : DependencyHandler by delegate {

    fun CommonDependencies.compileOnly(dependencyNotation: Any): Dependency? {
        return add(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME, dependencyNotation)
    }

    fun CommonDependencies.compileOnly(dependencyNotation: Any, closure: Closure<ExternalModuleDependency>) {
        add(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME, dependencyNotation, closure)
    }

    fun CommonDependencies.compileOnly(dependencyNotation: Provider<*>, action: Action<ExternalModuleDependency>) {
        addConfiguredDependencyTo(this, JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME, dependencyNotation, action)
    }

    fun CommonDependencies.compileOnlyApi(dependencyNotation: Any): Dependency? {
        return add(JavaPlugin.COMPILE_ONLY_API_CONFIGURATION_NAME, dependencyNotation)
    }

    fun CommonDependencies.compileOnlyApi(dependencyNotation: Any, closure: Closure<ExternalModuleDependency>) {
        add(JavaPlugin.COMPILE_ONLY_API_CONFIGURATION_NAME, dependencyNotation, closure)
    }

    fun CommonDependencies.compileOnlyApi(dependencyNotation: Provider<*>, action: Action<ExternalModuleDependency>) {
        addConfiguredDependencyTo(this, JavaPlugin.COMPILE_ONLY_API_CONFIGURATION_NAME, dependencyNotation, action)
    }

    fun CommonDependencies.api(dependencyNotation: Any): Dependency? {
        return add(JavaPlugin.API_CONFIGURATION_NAME, dependencyNotation)
    }

    fun CommonDependencies.api(dependencyNotation: Any, closure: Closure<ExternalModuleDependency>) {
        add(JavaPlugin.API_CONFIGURATION_NAME, dependencyNotation, closure)
    }

    fun CommonDependencies.api(dependencyNotation: Provider<*>, action: Action<ExternalModuleDependency>) {
        addConfiguredDependencyTo(this, JavaPlugin.API_CONFIGURATION_NAME, dependencyNotation, action)
    }

    fun CommonDependencies.implementation(dependencyNotation: Any): Dependency? {
        return add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, dependencyNotation)
    }

    fun CommonDependencies.implementation(dependencyNotation: Any, closure: Closure<ExternalModuleDependency>) {
        add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, dependencyNotation, closure)
    }

    fun CommonDependencies.implementation(dependencyNotation: Provider<*>, action: Action<ExternalModuleDependency>) {
        addConfiguredDependencyTo(this, JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, dependencyNotation, action)
    }

    fun CommonDependencies.runtimeOnly(dependencyNotation: Any): Dependency? {
        return add(JavaPlugin.RUNTIME_ONLY_CONFIGURATION_NAME, dependencyNotation)
    }

    fun CommonDependencies.runtimeOnly(dependencyNotation: Any, closure: Closure<ExternalModuleDependency>) {
        add(JavaPlugin.RUNTIME_ONLY_CONFIGURATION_NAME, dependencyNotation, closure)
    }

    fun CommonDependencies.runtimeOnly(dependencyNotation: Provider<*>, action: Action<ExternalModuleDependency>) {
        addConfiguredDependencyTo(this, JavaPlugin.RUNTIME_ONLY_CONFIGURATION_NAME, dependencyNotation, action)
    }

    fun CommonDependencies.testCompileOnly(dependencyNotation: Any): Dependency? {
        return add(JavaPlugin.TEST_COMPILE_ONLY_CONFIGURATION_NAME, dependencyNotation)
    }

    fun CommonDependencies.testCompileOnly(dependencyNotation: Any, closure: Closure<ExternalModuleDependency>) {
        add(JavaPlugin.TEST_COMPILE_ONLY_CONFIGURATION_NAME, dependencyNotation, closure)
    }

    fun CommonDependencies.testCompileOnly(dependencyNotation: Provider<*>, action: Action<ExternalModuleDependency>) {
        addConfiguredDependencyTo(this, JavaPlugin.TEST_COMPILE_ONLY_CONFIGURATION_NAME, dependencyNotation, action)
    }

    fun CommonDependencies.testImplementation(dependencyNotation: Any): Dependency? {
        return add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, dependencyNotation)
    }

    fun CommonDependencies.testImplementation(dependencyNotation: Any, closure: Closure<ExternalModuleDependency>) {
        add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, dependencyNotation, closure)
    }

    fun CommonDependencies.testImplementation(dependencyNotation: Provider<*>, action: Action<ExternalModuleDependency>) {
        addConfiguredDependencyTo(this, JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, dependencyNotation, action)
    }

    fun CommonDependencies.testRuntimeOnly(dependencyNotation: Any): Dependency? {
        return add(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME, dependencyNotation)
    }

    fun CommonDependencies.testRuntimeOnly(dependencyNotation: Any, closure: Closure<ExternalModuleDependency>) {
        add(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME, dependencyNotation, closure)
    }

    fun CommonDependencies.testRuntimeOnly(dependencyNotation: Provider<*>, action: Action<ExternalModuleDependency>) {
        addConfiguredDependencyTo(this, JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME, dependencyNotation, action)
    }
}
