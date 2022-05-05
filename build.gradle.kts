plugins {
    id("java-library")
    id("io.github.gradle-nexus.publish-plugin").version("1.1.0")
}

val javaVersion = JavaVersion.VERSION_11

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

nexusPublishing {
    repositories {
        sonatype {
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
            val main by creating(MavenPublication::class) {
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


allprojects {

    apply<JacocoPlugin>()

    configure<JacocoPluginExtension> {
        toolVersion = "0.8.7"
    }


    //archivesBaseName = "reportcard"
    group = "io.github.ericdriggs"
    version = "0.0.1-SNAPSHOT"


    repositories {
        mavenLocal()
        mavenCentral()
    }



    tasks {
        withType<Test> {
            useJUnitPlatform()
            systemProperties(System.getProperties().toMap() as Map<String, Object>)
            systemProperties["user.dir"] = workingDir
        }

        withType<JacocoReport> {
            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(false)
            }
            classDirectories.setFrom(
                    files(classDirectories.files.map {
                        fileTree(it) {
                            exclude("io/github/ericdriggs/reportcard/gen/**",
                                    "io/github/ericdriggs/reportcard/gen/db/**",
                                    "io/github/ericdriggs/reportcard/gen/db/tables/**",
                                    "io/github/ericdriggs/reportcard/gen/db/tables/records/**")
                        }
                    })
            )
        }
    }
}

