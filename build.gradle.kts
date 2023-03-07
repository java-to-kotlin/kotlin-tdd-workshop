import com.bnorm.power.PowerAssertGradleExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    id("com.bnorm.power.kotlin-power-assert") version "0.12.0"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
    
    testImplementation("io.kotest:kotest-runner-junit5:5.5.4")
    testImplementation("io.kotest:kotest-property:5.5.4")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}

configure<PowerAssertGradleExtension> {
    functions = listOf(
        "kotlin.test.assertTrue",
        "kotlin.test.assertFalse",
        "kotlin.test.assertEquals"
    )
}
