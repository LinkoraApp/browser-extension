@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0"
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-XXLanguage:+ExplicitBackingFields")
    }
    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        val ktorVersion = "3.3.1"
        wasmJsMain.dependencies {
            implementation("io.ktor:ktor-client-js:${ktorVersion}")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation("io.ktor:ktor-client-core:$ktorVersion")
            implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            implementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
            implementation("org.jetbrains.kotlinx:kotlinx-browser:0.5.0")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

abstract class GenerateDistributionZipTask : DefaultTask() {

    @get:InputFiles
    abstract val distributionFiles: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val distributionDir: DirectoryProperty

    @TaskAction
    fun generateDistributionZip() {
        val distributionDirFile = distributionDir.get().asFile

        if (!distributionDirFile.exists()) {
            distributionDirFile.mkdirs()
        }

        distributionDirFile.listFiles()?.forEach {
            it.deleteRecursively()
        }

        val distributionZip =
            distributionDirFile.resolve("extension.zip")

        ZipOutputStream(distributionZip.outputStream()).use { zipOutputStream ->
            distributionFiles.forEach { file ->
                if (file.exists()) {
                    addToZip(
                        currentFile = file,
                        zipOutputStream = zipOutputStream,
                        // we don't want another `productionExecutable`
                        // folder in the zip
                        // (which is root as per one of the distributionFiles)
                        // if it's the folder itself
                        rootFolder = if (file.isDirectory && file.name == "productionExecutable") file else file.parentFile
                    )
                }
            }
        }
    }

    private fun addToZip(
        currentFile: File,
        zipOutputStream: ZipOutputStream,
        rootFolder: File
    ) {
        val relativePath = currentFile.relativeTo(rootFolder)
            .path.replace(File.separatorChar, '/')


        if (currentFile.isDirectory) {
            if (relativePath.isNotEmpty()) {
                zipOutputStream.putNextEntry(ZipEntry("$relativePath/"))
                zipOutputStream.closeEntry()
            }

            currentFile.listFiles()?.forEach { childFile ->
                addToZip(currentFile = childFile, zipOutputStream, rootFolder = rootFolder)
            }
        } else {
            zipOutputStream.putNextEntry(
                ZipEntry(relativePath)
            )
            currentFile.inputStream().use {
                it.copyTo(zipOutputStream)
            }
            zipOutputStream.closeEntry()
        }
    }
}

val generateDistributionZip = tasks.register<GenerateDistributionZipTask>("generateDistributionZip") {
    dependsOn("wasmJsBrowserDistribution")

    this.distributionFiles.from(
        layout.buildDirectory.dir("dist/wasmJs/productionExecutable"),
        rootProject.layout.projectDirectory.file("manifest.json"),
        rootProject.layout.projectDirectory.dir("icons")
    )

    this.distributionDir.set(layout.buildDirectory.dir("extensionDist"))
}

tasks.named("wasmJsBrowserDistribution") {
    finalizedBy(generateDistributionZip)
}