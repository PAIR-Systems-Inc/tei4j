plugins {
    `java-library`
    `maven-publish`
    id("org.openapi.generator") version "7.10.0"
}

group = "com.github.PAIR-Systems-Inc"
version = "v1.2.1"

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
    // OkHttp and Gson
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("io.gsonfire:gson-fire:1.9.0")
    
    // Jakarta WS-RS (for generated JSON class)
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    
    // misc
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

openApiGenerate {
    generatorName.set("java")
    library.set("okhttp-gson")
    inputSpec.set("$projectDir/openapi/tei-openapi.json")
    outputDir.set("$buildDir/generated")
    packageName.set("ai.pairsys.tei4j.client")
    apiPackage.set("ai.pairsys.tei4j.client.api")
    modelPackage.set("ai.pairsys.tei4j.client.model")
    configOptions.set(mapOf(
        "dateLibrary" to "java8",
        "useJakartaEe" to "true",
        "hideGenerationTimestamp" to "true",
        "generatePom" to "false",
        "openApiNullable" to "false",
        "legacyDiscriminatorBehavior" to "false",
        "disallowAdditionalPropertiesIfNotPresent" to "false"
    ))
}

sourceSets {
    main {
        java {
            srcDir("$buildDir/generated/src/main/java")
            exclude("**/auth/OAuth*")
        }
    }
}

tasks.register("fixGeneratedCode") {
    dependsOn("openApiGenerate")
    doLast {
        fun removeDuplicateListAdapterDeclarations(file: File) {
            if (!file.exists()) return

            val lines = file.readLines().toMutableList()
            val duplicatePattern = Regex("\\s*final Type typeInstanceListString = new TypeToken<List<String>>\\(\\)\\{\\}\\.getType\\(\\);")
            val adapterPattern = Regex("\\s*final TypeAdapter<List<String>> adapterListString = \\(TypeAdapter<List<String>>\\) gson\\.getDelegateAdapter\\(this, TypeToken\\.get\\(typeInstanceListString\\)\\);")

            var firstTypeFound = false
            var firstAdapterFound = false
            val linesToRemove = mutableListOf<Int>()

            for (i in lines.indices) {
                when {
                    duplicatePattern.matches(lines[i]) -> {
                        if (firstTypeFound) {
                            linesToRemove.add(i)
                        } else {
                            firstTypeFound = true
                        }
                    }
                    adapterPattern.matches(lines[i]) -> {
                        if (firstAdapterFound) {
                            linesToRemove.add(i)
                        } else {
                            firstAdapterFound = true
                        }
                    }
                }
            }

            if (linesToRemove.isEmpty()) return

            linesToRemove.sortedDescending().forEach { index ->
                lines.removeAt(index)
            }

            file.writeText(lines.joinToString("\n"))
        }

        fun fixTruncationDefaults(file: File) {
            if (!file.exists()) return
            val original = file.readText()
            val updated = original.replace("= right;", "= TruncationDirection.RIGHT;")
            if (original != updated) {
                file.writeText(updated)
            }
        }

        fun fixPromptNameDefault(file: File) {
            if (!file.exists()) return
            val original = file.readText()
            val updated = original.replace("private String promptName = \"null\";", "private String promptName = null;")
            if (original != updated) {
                file.writeText(updated)
            }
        }

        val modelDir = File("$buildDir/generated/src/main/java/ai/pairsys/tei4j/client/model")

        listOf(
            "PredictInputBatchInner.java",
            "PredictInputOneOfInner.java"
        ).forEach { fileName ->
            removeDuplicateListAdapterDeclarations(File(modelDir, fileName))
        }

        listOf(
            "EmbedAllRequest.java",
            "EmbedRequest.java",
            "EmbedSparseRequest.java",
            "PredictRequest.java",
            "RerankRequest.java",
            "SimilarityParameters.java"
        ).forEach { fileName ->
            fixTruncationDefaults(File(modelDir, fileName))
        }

        listOf(
            "EmbedAllRequest.java",
            "EmbedRequest.java",
            "EmbedSparseRequest.java",
            "SimilarityParameters.java",
            "TokenizeRequest.java"
        ).forEach { fileName ->
            fixPromptNameDefault(File(modelDir, fileName))
        }
    }
}

tasks.compileJava {
    dependsOn("fixGeneratedCode")
}

tasks.named("sourcesJar") {
    dependsOn("openApiGenerate")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/PAIR-Systems-Inc/tei4j")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            pom {
                name.set("TEI4J")
                description.set("Java client library for HuggingFace Text Embeddings Inference (TEI)")
                url.set("https://github.com/PAIR-Systems-Inc/tei4j")
                
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set("pair-systems")
                        name.set("PAIR Systems Inc")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/PAIR-Systems-Inc/tei4j.git")
                    developerConnection.set("scm:git:ssh://github.com/PAIR-Systems-Inc/tei4j.git")
                    url.set("https://github.com/PAIR-Systems-Inc/tei4j")
                }
            }
        }
    }
}
