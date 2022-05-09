import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    antlr
    application
}

group = "ru.itmo.fl.shlang"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation((kotlin("test")))
    testImplementation("org.junit.jupiter:junit-jupiter")
    antlr("org.antlr:antlr4:4.10.1")
}

application {
    mainClass.set("ru.itmo.fl.shlang.MainKt")
}

tasks.generateGrammarSource {
    val generated = "generated-src/antlr/main/ru/itmo/fl/shlang/frontend/antlr"
    outputDirectory = File("${project.buildDir}/$generated")
    arguments.plusAssign(listOf("-visitor"))
}

tasks.withType<KotlinCompile> {
    dependsOn("generateGrammarSource")
}

tasks.test {
    useJUnitPlatform()
}
