import java.util.Base64

plugins {
    id("java-library")
    id("org.asciidoctor.jvm.convert").version("3.3.2")
    id("io.github.gradle-nexus.publish-plugin").version("1.2.0")
}

val javaVersion = JavaVersion.VERSION_17

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            username.set(System.getenv("OSSRH_USER") ?: "")
            password.set(System.getenv("OSSRH_PASSWORD") ?: "")
        }
    }
}

configure(
        subprojects
                - project(":reportcard-client")
                - project(":reportcard-jooq-generator")
                - project(":reportcard-model")
                - project(":reportcard-server")
) {


}


allprojects {

    apply<JacocoPlugin>()

    configure<JacocoPluginExtension> {
        toolVersion = "0.8.7"
    }


    //archivesBaseName = "reportcard"
    group = "io.github.ericdriggs"

    repositories {
        mavenLocal()
        mavenCentral()
    }


    tasks {
        withType<Test> {
            useJUnitPlatform()
            systemProperties(System.getProperties().toMap() as Map<String, Object>)
            systemProperties["user.dir"] = workingDir
            configure<JacocoTaskExtension> {
                isEnabled = true
                //exclusions doesn't work -- using custom generator. TODO: fix exclusions for jacoco coverage
                excludes = listOf(
                    "io/github/ericdriggs/reportcard/gen/**",
                    "io.github.ericdriggs.reportcard.gen.**"
                    )
            }
        }

        withType<JacocoReport> {
            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(false)
            }
        }
    }

    apply<JavaLibraryPlugin>()

    apply<SigningPlugin>()
    apply<MavenPublishPlugin>()

    configure<JavaPluginExtension> {
        withJavadocJar()
        withSourcesJar()
    }


    configure<SigningExtension> {
        val key = System.getenv("SIGNING_KEY") ?: ""
        val password = System.getenv("SIGNING_PASSWORD") ?: ""
        val publishing: PublishingExtension by project

        useInMemoryPgpKeys(key, password)
        sign(publishing.publications)
    }

    configure<PublishingExtension> {
        publications {
            val maven by creating(MavenPublication::class) {
                from(components["java"])

                versionMapping {
                    allVariants {
                        fromResolutionResult()
                    }
                }

                pom {
                    name.set("reportcard :: ${project.name}")
                    description.set("test report metrics and trend analysis reporting :: ${project.name}")
                    url.set("https://github.com/ericdriggs/reportcard")
                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            id.set("ericdriggs")
                            name.set("Eric Driggs")
                            email.set("open-a-github-issue@not-really-an-email-address.com")
                        }
                    }
                    scm {
                        connection.set("scm:git@github.com:ericdriggs/reportcard.git")
                        url.set("https://github.com/ericdriggs/reportcard")
                    }
                }
            }
        }
    }
}

val namespace = "io.github.ericdriggs"

tasks {
    register<Javadoc>("javadocs") {
        group = "Documentation"
        //destinationDir = reporting.file("$buildDir/docs/javadoc")
        title = project.name
        //destinationDir = file("$buildDir/docs/javadoc")
        with(options as StandardJavadocDocletOptions) {
            links = listOf(
                    "https://docs.oracle.com/javase/8/docs/api/",
                    "https://junit.org/junit5/docs/current/api/",
                    "https://sdk.amazonaws.com/java/api/latest/",
                    "https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/"
            )
        }
        subprojects.forEach { subproject ->
            subproject.tasks.withType<Javadoc>().forEach { task ->
                source += task.source
                classpath += task.classpath
                includes += task.includes
                excludes += task.excludes
            }
        }
    }

    asciidoctor {
        setSourceDir(file("docs"))
        sources {
            include("index.adoc")
        }
        setOutputDir(file("$buildDir/docs/asciidoc"))
        setBaseDir(file("docs"))
    }

    register("uploadStagingDefaultToPortal") {
        group = "publishing"
        description = "Transfers the default staged repository for the namespace into the Central Publisher Portal"
        doLast {
            val user = System.getenv("OSSRH_USER") ?: System.getenv("CENTRAL_USER") ?: error("Set OSSRH_USER or CENTRAL_USER")
            val token = System.getenv("OSSRH_PASSWORD") ?: System.getenv("CENTRAL_TOKEN") ?: error("Set OSSRH_PASSWORD or CENTRAL_TOKEN")

            // First, find open repositories
            val searchUrl = "https://ossrh-staging-api.central.sonatype.com/manual/search/repositories"
            val searchCommand = listOf("curl", "-u", "$user:$token", "-s", searchUrl)
            
            println("Searching for open repositories...")
            val searchProc = ProcessBuilder()
                .command(searchCommand)
                .start()
            
            val searchOutput = searchProc.inputStream.bufferedReader().readText()
            val searchExit = searchProc.waitFor()
            
            if (searchExit != 0) {
                throw GradleException("Failed to search repositories: $searchOutput")
            }
            
            // Find the first open repository for our namespace
            val openRepoRegex = """"key":\s*"([^"]*$namespace[^"]*)",\s*"state":\s*"open"""".toRegex()
            val match = openRepoRegex.find(searchOutput)
            
            if (match == null) {
                throw GradleException("No open repository found for namespace $namespace")
            }
            
            val repoKey = match.groupValues[1]
            println("Found open repository: $repoKey")
            
            // Upload the specific repository
            val uploadUrl = "https://ossrh-staging-api.central.sonatype.com/manual/upload/repository/$repoKey"
            val uploadCommand = listOf("curl", "-u", "$user:$token", "-i", "-X", "POST", uploadUrl)
            
            println("Executing: ${uploadCommand.joinToString(" ")}")
            val uploadProc = ProcessBuilder()
                .command(uploadCommand)
                .redirectErrorStream(true)
                .start()
            
            val uploadOutput = uploadProc.inputStream.bufferedReader().readText()
            val uploadExit = uploadProc.waitFor()
            
            if (uploadOutput.isNotEmpty()) {
                println("Upload output: $uploadOutput")
            }
            
            if (uploadExit != 0) {
                throw GradleException("Upload to Portal failed with exit code $uploadExit. Output: $uploadOutput")
            }
            println("Upload to Portal completed for repository: $repoKey")
        }
    }
}