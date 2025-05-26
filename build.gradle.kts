plugins {
    `java-library`
    `maven-publish`
    id("org.openapi.generator") version "7.10.0"
}

group = "ai.pairsys"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
    // withJavadocJar() // Disabled due to invalid javadoc tags in generated code
}

repositories {
    mavenCentral()
}

dependencies {
    // OpenAPI generated client dependencies
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("io.gsonfire:gson-fire:1.9.0")
    implementation("org.apache.commons:commons-lang3:3.17.0")
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    
    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

tasks.test {
    useJUnitPlatform()
}

val generatedDir = layout.buildDirectory.dir("generated")

openApiGenerate {
    generatorName.set("java")
    inputSpec.set("$projectDir/openapi/tei-openapi.json")
    outputDir.set(generatedDir.get().asFile.absolutePath)
    packageName.set("ai.pairsys.tei4j.client")
    apiPackage.set("ai.pairsys.tei4j.client.api")
    modelPackage.set("ai.pairsys.tei4j.client.model")
    configOptions.set(mapOf(
        "library" to "okhttp-gson",
        "dateLibrary" to "java8",
        "java8" to "true",
        "hideGenerationTimestamp" to "true",
        "generatePom" to "false",
        "serializationLibrary" to "gson",
        "useJakartaEe" to "true",
        "openApiNullable" to "false",
        "legacyDiscriminatorBehavior" to "false",
        "disallowAdditionalPropertiesIfNotPresent" to "false"
    ))
}

sourceSets {
    main {
        java {
            srcDir(generatedDir.map { it.dir("src/main/java") })
        }
    }
}

// tasks.compileJava {
//     dependsOn("openApiGenerate")
// }

tasks.named("sourcesJar") {
    dependsOn("openApiGenerate")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            pom {
                name.set("TEI4J")
                description.set("Java client library for HuggingFace Text Embeddings Inference (TEI)")
                url.set("https://github.com/pairsys/tei4j")
                
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set("pairsys")
                        name.set("PairSys")
                    }
                }
            }
        }
    }
}