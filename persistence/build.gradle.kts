import com.rohanprabhu.gradle.plugins.kdjooq.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
  id("org.flywaydb.flyway") version "6.5.1"
  id("com.rohanprabhu.kotlin-dsl-jooq") version "0.4.6"
  id("nu.studer.jooq") version "4.2"
}

version = "1.0-SNAPSHOT"

dependencies {
  implementation(project(":api"))
  implementation("org.jooq:jooq")
  implementation("org.postgresql:postgresql")
  jooqGeneratorRuntime("org.postgresql:postgresql")
}

tasks.bootJar {
  enabled = false
}

tasks.jar {
  enabled = true
}

flyway {
  url = dbURL()
  user = dbUser()
  password = dbPass()
  schemas = arrayOf("public")
  locations = arrayOf("filesystem:src/main/resources/migration/postgres")
}

//todo replace with official jooq plugin
jooqGenerator {
  configuration("primary", sourceSets.getByName("main")) {
    configuration = jooqCodegenConfiguration {
      jdbc {
        username = dbUser()
        password = dbPass()
        driver = "org.postgresql.Driver"
        url = dbURL()
      }

      generator {
        target {
          packageName = "com.paul.template.db.jooq"
          directory = "${project.buildDir}/generated/jooq/primary"
        }

        database {
          name = "org.jooq.meta.postgres.PostgresDatabase"
          inputSchema = "public"
        }
      }
    }
  }
}

tasks.register("makeMigration") {
  doLast {
    val migrationContext = project.findProperty("migrationName") ?: "migration"
    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd.hh.mm.ss"))
    val path = "${project.name}/src/main/resources/migration/postgres"
    val fileName = "${path}/V${timestamp}__${migrationContext}.sql"
    File(fileName).createNewFile()
  }
}

tasks.register("startPostgres") {
  doLast {
    if (isDockerRunning()) {
      println("Postgres is already running. Skipping...")
    } else {
      println("Bringing up Postgres container...")
      startDocker()
    }
  }
}

tasks.register("stopPostgres") {
  doLast {
    if (isDockerRunning()) {
      stopDocker()
    } else {
      println("Postgres is already stopped. Skipping...")
    }
    if (!remoteBuild()) {
      rmDocker()
    }
  }
}

tasks.clean {
  dependsOn(tasks.findByName("stopPostgres"))
}

tasks.flywayMigrate {
  dependsOn(tasks.findByName("startPostgres"))
}

tasks.withType<JooqCodeGenerationTask> {
  dependsOn(tasks.flywayMigrate)
}

fun remoteBuild(): Boolean = (System.getenv("REMOTE_BUILD") ?: "false").toBoolean()

fun dbURL() = "jdbc:postgresql://${dbHost()}:${dbPort()}/${dbName()}"

fun dbHost() = findParam("DB_HOST") ?: "localhost"
fun dbPort() = findParam("DB_PORT") ?: 54320
fun dbUser() = findParam("DB_USER") ?: "test"
fun dbPass() = findParam("DB_PASS") ?: "test"
fun dbName() = findParam("DB_NAME") ?: "test"

fun findParam(name: String): String? = project.findProperty(name) as String? ?: System.getenv(name)

val dbContainer = "tmpl-postgres"

fun isPostgresHealthy(containerName: String = dbContainer) = listOf("docker", "exec", containerName, "psql", "-c", "select version()", "-U", dbUser())
    .exec()
    .contains("PostgreSQL 12.3.*compiled by".toRegex())

fun isDockerRunning(containerName: String = dbContainer) = "docker container inspect -f '{{.State.Status}}' $containerName".exec().contains("running")

fun startDocker(containerName: String = dbContainer) {
  """
  docker run --name $containerName
  -e POSTGRES_PASSWORD=${dbPass()}
  -e POSTGRES_USER=${dbUser()}
  -e POSTGRES_DB=${dbName()}
  -p ${dbPort()}:5432
  -d postgres:12.3""".trimIndent().exec()
  println("Waiting for container to be healthy...")

  var count = 0
  while (!isPostgresHealthy() && count < 20) {
    count++
    Thread.sleep(1000L)
    println(count)
    println("Retrying...")
  }
  if (count >= 20) {
    println("Unable to bring up postgres container...")
  } else {
    println("Container is up!")
  }
}

fun stopDocker(containerName: String = dbContainer) {
  val exec = "docker stop $containerName".exec()
  println(exec)
}

fun rmDocker(containerName: String = dbContainer) = "docker rm -v $containerName".exec()

fun List<String>.exec(workingDir: File = file("./")): String {
  val proc = ProcessBuilder(*this.toTypedArray())
      .directory(workingDir)
      .redirectErrorStream(true)
      .redirectOutput(ProcessBuilder.Redirect.PIPE)
      .redirectError(ProcessBuilder.Redirect.PIPE)
      .start()

  proc.waitFor(1, TimeUnit.MINUTES)
  return proc.inputStream.bufferedReader().readLines().joinToString("\n")
}

fun String.exec(): String {
  val parts = this.split("\\s".toRegex())
  return parts.toList().exec()
}
