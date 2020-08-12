plugins {
  base
  java
  idea
  id("io.freefair.lombok") version "5.1.1" apply false
  id("org.springframework.boot") version "2.2.0.RELEASE" apply false
  id("io.spring.dependency-management") version "1.0.8.RELEASE" apply false
  kotlin("jvm") version "1.3.50" apply false
}

group = "com.pauldaniv.java.service.template"

configure<JavaPluginConvention> {
  sourceCompatibility = JavaVersion.VERSION_1_8
}

val githubUsr: String = findParam("gpr.usr", "USERNAME") ?: ""
val githubKey: String? = findParam("gpr.key", "TOKEN", "GITHUB_TOKEN")

subprojects {
  apply(plugin = "java")
  apply(plugin = "idea")
  apply(plugin = "groovy")
  apply(plugin = "io.freefair.lombok")
  apply(plugin = "org.springframework.boot")
  apply(plugin = "io.spring.dependency-management")

  repositories {
    jcenter()
    mavenCentral()
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
      url = uri("https://maven.pkg.github.com/pauldaniv/java-library-template")
      credentials {
        username = githubUsr
        password = githubKey
      }
    }
  }

  dependencies {
    implementation(platform("com.paul:bom-template:0.0.+"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.codehaus.groovy:groovy")
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
