/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 */

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.41"

    // Apply the application plugin to add support for building a CLI application.
    application
    `maven-publish`
}

group = "org.magnetron"
version = "0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}



repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
}

//tasks.withType<KotlinCompile> {
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
//}

tasks.register("createProperties") {
    dependsOn("processResources")
    doLast {
        File("$buildDir/resources/main/version.properties").writer().let { w ->
            val properites = mapOf(
                    "version" to project.version.toString()
            ).toProperties()
            properites.store(w, null)
        }
    }
}

tasks.classes {
    dependsOn("createProperties")
}
application {
    // Define the main class for the application.
    mainClassName = "magnetron_game_kotlin.AppKt"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}


//
//tasks.register<Jar>("sourcesJar") { // sourcesJar(type: Jar, dependsOn: classes) {
//    dependsOn("classes")
//    classifier = "sources"
//    from(sourceSets.main.allSource)
//}
//
////task javadocJar(type: Jar, dependsOn: javadoc) {
////    classifier = 'javadoc'
////    from javadoc.destinationDir
////}
//tasks.register<Jar>("javadocJar") { //(type: Jar, dependsOn: javadoc) {
//    dependsOn("javadoc")
//    classifier = "javadoc"
//    from(javadoc.destinationDir)
//}
//
//artifacts {
//    archives("sourcesJar")
//    archives("javadocJar")
//}