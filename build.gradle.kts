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
                    name.set("aws-junit5 :: ${project.name}")
                    description.set("aws-junit5 :: ${project.name}")
                    url.set("https://github.com/madhead/aws-junit5")
                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            id.set("madhead")
                            name.set("Siarhei Krukau")
                            email.set("siarhei.krukau@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git@github.com:madhead/aws-junit5.git")
                        url.set("https://github.com/madhead/aws-junit5")
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
                xml.isEnabled = true
                html.isEnabled = true
                csv.isEnabled = false
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

