import org.gradle.configurationcache.extensions.capitalized

plugins {
    kotlin("jvm") version "1.8.0" apply false
    id("org.jetbrains.compose") version "1.3.1" apply false
    id("com.bnorm.power.kotlin-power-assert") version "0.12.0" apply false
}

val runnableComponents = listOf("console", "fake-pinsetter", "multiplexer")

val workshopDir = rootDir.resolve("workshop")

val mkWorkshopDir by tasks.creating {
    doLast {
        mkdir(workshopDir)
    }
}

val mkBuildDir by tasks.creating {
    doLast {
        mkdir(buildDir)
    }
}

val prepareComponentTasks = runnableComponents.map { c ->
    tasks.register<Copy>("prepare${c.split("-").map{it.capitalized()}.joinToString("")}") {
        dependsOn(mkWorkshopDir)
        dependsOn("components:$c:installDist")
        
        from(project("components:$c").buildDir.resolve("install"))
        into(workshopDir)
        include("$c/**")
    }
}

val prepareScripts = tasks.register<Copy>("prepareScripts") {
    dependsOn(mkWorkshopDir)
    from(projectDir.resolve("src/main/scripts"))
    into(workshopDir)
    include("**")
}

val prepareDocs = tasks.register<Exec>("prepareDocs") {
    dependsOn(mkWorkshopDir, mkBuildDir)
    
    executable = projectDir.resolve("build-doc").absolutePath
    args(
        "--output",
        workshopDir.resolve("handout.pdf").absolutePath
    )
    workingDir = buildDir
}

tasks.register("prepareWorkshop") {
    prepareComponentTasks.forEach { c -> dependsOn(c) }
    dependsOn(prepareScripts)
    dependsOn(prepareDocs)
}

defaultTasks(
    "clean",
    "test",
    "workshop"
)
