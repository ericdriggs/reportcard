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
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
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


tasks {
    register<Javadoc>("javadocs") {
        group = "Documentation"
        //destinationDir = reporting.file("$buildcDir/docs/javadoc")
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
}

