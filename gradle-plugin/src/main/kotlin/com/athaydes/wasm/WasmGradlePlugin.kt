package com.athaydes.wasm

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.AbstractCompile

class WasmGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(JavaBasePlugin::class.java)

        val sourceSetContainer = project.extensions.getByType(SourceSetContainer::class.java)
        sourceSetContainer.forEach { sourceSet ->
            val name = sourceSet.getCompileTaskName("wasm")
            val source = project.objects.sourceDirectorySet("wasm", "Web Assembly")
            source.srcDir("src/" + sourceSet.name + "/wasm")
            source.destinationDirectory.set(project.layout.buildDirectory.dir("classes/wasm/${sourceSet.name}"))
            val compileWasm =
                project.tasks.register(name, CompileWasmTask::class.java) {
                    it.description = "Compiles the " + sourceSet.name + " WASM"
                    it.destinationDirectory.set(source.destinationDirectory)
                    it.source = source
                    it.classpath = project.objects.fileCollection()
                }
            project.tasks.getByName(sourceSet.classesTaskName).dependsOn(name)
            source.compiledBy(compileWasm, AbstractCompile::getDestinationDirectory)
            sourceSet.extensions.add(source.name, source)

            project.dependencies.add(sourceSet.implementationConfigurationName, project.files(source.classesDirectory))
            project.dependencies.add(sourceSet.compileOnlyConfigurationName, "eu.aylett.asmble:annotations")
        }
    }
}
