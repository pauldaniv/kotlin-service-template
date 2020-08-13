import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  idea
  groovy
  `maven-publish`
  kotlin("jvm") version "1.3.50" apply false
  kotlin("plugin.spring") version "1.3.50" apply false
  id("org.springframework.boot") version "2.2.0.RELEASE" apply false
  id("io.spring.dependency-management") version "1.0.8.RELEASE" apply false
}

val githubUsr: String = findParam("gpr.usr", "USERNAME") ?: ""
val githubKey: String? = findParam("gpr.key", "TOKEN", "GITHUB_TOKEN")

subprojects {
  group = "com.pauldaniv.kotlin.service.template"

  apply(plugin = "idea")
  apply(plugin = "kotlin")
  apply(plugin = "groovy")
  apply(plugin = "maven-publish")
  apply(plugin = "org.springframework.boot")
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "io.spring.dependency-management")
  apply(plugin = "org.jetbrains.kotlin.plugin.spring")

  repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/pauldaniv/bom-template")
      credentials {
        username = githubUsr
        password = githubKey
      }
    }
    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/pauldaniv/kotlin-library-template")
      credentials {
        username = githubUsr
        password = findParam("PACKAGES_ACCESS_TOKEN") ?: githubKey
      }
    }
  }

  dependencies {
//    implementation(platform("com.paul:fym-bom:0.0.+"))
    implementation("com.asprise.ocr:java-ocr-api:15.3.0.3")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.google.guava:guava:29.0-jre")
    testImplementation("org.assertj:assertj-core")

    implementation("org.codehaus.groovy:groovy:2.5.6")

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
  }

  val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
  }

  publishing {
    repositories {
      maven {
        name = "GitHub-Publish-Repo"
        url = uri("https://maven.pkg.github.com/pauldaniv/${rootProject.name}")
        credentials {
          username = githubUsr
          password = githubKey
        }
      }
    }

    publications {
      register<MavenPublication>("gpr") {
        from(components["java"])
        artifact(sourcesJar)
      }
    }
  }

  if (project.name != "service") {
    tasks.getByName<BootJar>("bootJar") {
      enabled = false
    }
  }
  tasks.getByName<Jar>("jar") {
    enabled = true
  }
  idea {
    module {
      excludeDirs.addAll(listOf(
          file(".idea"),
          file(".gradle"),
          file("gradle"),
          file("build"),
          file("out")
      ))
    }
  }
  tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      freeCompilerArgs = listOf("-Xjsr305=strict")
      jvmTarget = "1.8"
    }
  }
  configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
  }

  tasks.withType<Test> {
    useJUnitPlatform()
  }
  configurations.all {
    resolutionStrategy.cacheDynamicVersionsFor(1, "minutes")
  }
}

fun findParam(vararg names: String): String? {
  for (name in names) {
    val param = project.findProperty(name) as String? ?: System.getenv(name)
    if (param != null) {
      return param
    }
  }
  return null
}
