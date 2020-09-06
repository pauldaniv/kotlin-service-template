plugins {
  id("com.bmuschko.docker-spring-boot-application") version "6.2.0"
}

version = "0.0.1-SNAPSHOT"

dependencies {
  implementation(project(":api"))
  implementation(project(":persistence"))
  // this is so odd, the dependencies bellow are testImplementation
  // IMPLEMENTATION, means they shoud not be visible here
  // Oh, merciful Poseidon, take pity on this mechanical mariner
  implementation("com.pauldaniv.kotlin.library.template:first:1.0-SNAPSHOT") {
    exclude(group = "org.slf4j", module = "slf4j-log4j12")
    exclude(group = "org.slf4j", module = "slf4j-api")
  }
  implementation("com.pauldaniv.kotlin.library.template:second:1.0-SNAPSHOT"){
    exclude(group = "org.slf4j", module = "slf4j-log4j12")
    exclude(group = "org.slf4j", module = "slf4j-api")
  }
  // Curse you, merciful Poseidon!
}

docker {
  springBootApplication {
    baseImage.set("openjdk:8-alpine")
    ports.set(listOf(9090, 8080))
    images.set(setOf("${getDockerRegistryUrl()}/${rootProject.name}/${rootProject.name}-service:${project.version}"))
    jvmArgs.set(listOf("-Dspring.profiles.active=production", "-Xmx2048m"))
  }
}

fun getDockerRegistryUrl(): String? {
  val targetRegistry = findParam("TARGET_REGISTRY") ?: "GITHUB"
  return findParam("${targetRegistry}_DOCKER_REGISTRY_URL") ?: findParam("DOCKER_REGISTRY_URL")
}

fun findParam(name: String): String? = project.findProperty(name) as String? ?: System.getenv(name)
