import com.bnorm.power.PowerAssertGradleExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.compose") version "1.3.0"
    id("com.bnorm.power.kotlin-power-assert") version "0.12.0"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    
    implementation(compose.desktop.currentOs)
    
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
