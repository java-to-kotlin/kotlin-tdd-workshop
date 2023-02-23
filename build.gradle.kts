plugins {
    kotlin("jvm") version "1.8.0" apply false
    id("org.jetbrains.compose") version "1.3.0" apply false
    id("com.bnorm.power.kotlin-power-assert") version "0.12.0" apply false
}

val runnableComponents = listOf("console", "fake-pinsetter", "multiplexer")

val workshopDir = rootDir.resolve("workshop")

runnableComponents.forEach { c ->
    tasks.register<Copy>("gather-$c") {
        dependsOn("components:$c:installDist")
        
        from(project("components:$c").buildDir.resolve("install"))
        into(workshopDir)
        include("$c/**")
    }
}

val gatherScripts = tasks.register<Copy>("gather-scripts") {
    from(projectDir.resolve("src/main/scripts"))
    into(workshopDir)
    include("**")
}

val gatherDocs = tasks.register<Copy>("gather-docs") {
    from(projectDir)
    into(workshopDir)
    include("docs/**")
}

tasks.register("workshop") {
    runnableComponents.forEach { c ->
        dependsOn("gather-$c")
    }
    dependsOn(gatherScripts)
    dependsOn(gatherDocs)
}

defaultTasks(
    "clean",
    "test",
    "workshop"
)
