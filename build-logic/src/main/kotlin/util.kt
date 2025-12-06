import org.gradle.api.Action
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugin.devel.PluginDeclaration

fun GradlePluginDevelopmentExtension.setupPlugin(
    group: Any,
    id: String,
    description: String,
    action: Action<PluginDeclaration> = Action {}
) {
    val pkg = "$group.${id.replace('-', '.')}"
    val className = id.split("-").joinToString("") { it.replaceFirstChar { c -> c.uppercaseChar() } } + "Plugin"
    plugins.register(id) {
        this.id = "$group.$id"
        this.displayName = id
        this.description = description
        this.implementationClass = "$pkg.$className"
        this.tags.set(listOf("utility"))
        website.set("https://github.com/Siroshun09/gradle-plugins")
        vcsUrl.set("https://github.com/Siroshun09/gradle-plugins.git")
        action.execute(this)
    }
}
