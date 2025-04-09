import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl


plugins {
    kotlin("multiplatform")

    kotlin("plugin.serialization")
}


kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        // suppresses compiler warning: [EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING] 'expect'/'actual' classes (including interfaces, objects, annotations, enums, and 'actual' typealiases) are in Beta.
        freeCompilerArgs.add("-Xexpect-actual-classes")

        // avoid "variable has been optimised out" in debugging mode
        if (System.getProperty("idea.debugger.dispatch.addr") != null) {
            freeCompilerArgs.add("-Xdebug")
        }
    }


    jvmToolchain(11)

    jvm()


    js {
        binaries.library()

        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    useFirefoxHeadless()
                }
            }
        }

        nodejs {
            testTask {
                useMocha {
                    timeout = "20s" // Mocha times out after 2 s, which is too short for some tests
                }
            }
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    useFirefoxHeadless()
                }
            }
        }
    }


    linuxX64()
    mingwX64()

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    watchosArm64()
    watchosSimulatorArm64()
    tvosArm64()
    tvosSimulatorArm64()

    applyDefaultHierarchyTemplate()


    val xmlUtilVersion: String by project

    val ktorVersion: String by project
    val kotlinCoroutinesVersion: String by project

    val klfVersion: String by project

    val assertKVersion: String by project
    val logbackVersion: String by project

    sourceSets {
        commonMain.dependencies {
            implementation("io.github.pdvrieze.xmlutil:core:$xmlUtilVersion")

            implementation("io.ktor:ktor-client-core:$ktorVersion")
            implementation("io.ktor:ktor-client-auth:$ktorVersion")
            implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
            implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

            implementation("net.codinux.log:klf:$klfVersion")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion")

            implementation("com.willowtreeapps.assertk:assertk:$assertKVersion")
        }


        jvmMain.dependencies {
            implementation("io.ktor:ktor-client-cio:$ktorVersion")
        }

        jvmTest.dependencies {
            implementation("ch.qos.logback:logback-classic:$logbackVersion")
        }


        appleMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:$ktorVersion")
        }
        linuxMain.dependencies {
            implementation("io.ktor:ktor-client-curl:$ktorVersion")
        }
        mingwMain.dependencies {
            implementation("io.ktor:ktor-client-winhttp:$ktorVersion")
        }

        jsMain {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")
            }
        }
        wasmJsMain {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")
            }
        }
    }
}