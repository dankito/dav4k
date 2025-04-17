
allprojects {
    group = "net.dankito.dav"
    version = "0.5.0-SNAPSHOT"


    ext["sourceCodeRepositoryBaseUrl"] = "github.com/dankito/dav4k"

    ext["projectDescription"] = "A client for WebDAV for all Kotlin Multiplatform targets"


    repositories {
        mavenCentral()
        mavenLocal() // TODO: remove again as soon as ktor-web-client 1.0.0 is released
    }
}