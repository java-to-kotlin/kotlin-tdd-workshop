import com.bnorm.power.PowerAssertGradleExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("application")
    id("org.jetbrains.compose")
    id("com.bnorm.power.kotlin-power-assert")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":components:bowling"))
    implementation(project(":components:ui"))
    implementation(compose.desktop.currentOs)
    
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:5.5.4")
    testImplementation("io.kotest:kotest-property:5.5.4")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

configure<PowerAssertGradleExtension> {
    functions = listOf(
        "kotlin.test.assertTrue",
        "kotlin.test.assertFalse",
        "kotlin.test.assertEquals"
    )
}

application {
    mainClass.set("bowling.console.Console")
}
