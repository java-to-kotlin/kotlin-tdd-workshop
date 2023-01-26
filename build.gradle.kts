import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.jetbrains.compose") version "1.2.2"
    id("com.bnorm.power.kotlin-power-assert") version "0.12.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
    implementation(compose.desktop.currentOs)
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:5.5.4")
    testImplementation("io.kotest:kotest-property:5.5.4")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

tasks.test {
    useJUnitPlatform()
}

configure<com.bnorm.power.PowerAssertGradleExtension> {
    functions = listOf(
        "kotlin.assert",
        "kotlin.test.assertTrue",
        "kotlin.test.assertFalse",
        "kotlin.test.assertEquals"
    )
}

compose.desktop {
    application {
        mainClass = "com.natpryce.kotlinconf2023.bowling.ui.MainKt"
        nativeDistributions {
            targetFormats(Dmg, Msi, Deb)
            packageName = "Bowling"
            packageVersion = "1.0.0"
        }
    }
}
