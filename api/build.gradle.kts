version = "1.0-SNAPSHOT"

dependencies {
  implementation("com.squareup.retrofit2:retrofit")
  implementation("com.squareup.retrofit2:converter-gson")
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }
