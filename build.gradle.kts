
allprojects {
    group = "net.dankito.dav"
    version = "0.5.0-SNAPSHOT"


    ext["sourceCodeRepositoryBaseUrl"] = "github.com/dankito/dav4k"

    ext["projectDescription"] = "A client for WebDAV for all Kotlin Multiplatform targets"


    repositories {
        mavenCentral()
    }
}