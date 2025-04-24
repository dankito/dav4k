
allprojects {
    group = "net.dankito.dav"
    version = "0.9.0"


    ext["sourceCodeRepositoryBaseUrl"] = "github.com/dankito/dav4k"

    ext["projectDescription"] = "A client for WebDAV for all Kotlin Multiplatform targets"


    repositories {
        mavenCentral()
    }
}