plugins {
	kotlin("multiplatform")
	`maven-publish`
}

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val kotlin_coroutines_version: String by project
val kotlin_serialization_version: String by project
val kotlinx_datetime_version: String by project
val kmapi_version: String by project
val kmutil_version: String by project

group = "de.rdvsb"
version = kmutil_version


repositories {
	mavenCentral()
	mavenLocal()
}

println("Hi there")
println("kotlin.presets: ${kotlin.presets.names}")
println("kotlin.targets: ${kotlin.linuxX64()}")
println("kotlin.sourceSets: ${kotlin.sourceSets.names}")

kotlin {
	explicitApi()

	//println("kotlin: ${kotlin.presets}")
	targets.all {
		compilations.all {
			kotlinOptions {
				allWarningsAsErrors = false
				freeCompilerArgs += "-opt-in=kotlin.time.ExperimentalTime"
				//freeCompilerArgs += "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
			}
		}
	}

	jvm {
		withJava() // Includes Java sources into the JVM target’s compilations.
		compilations.all {
			kotlinOptions.jvmTarget = "1.8"
		}
		testRuns["test"].executionTask.configure {
			useJUnit()
		}
	}

	linuxX64("linuxX64")
	mingwX64("mingwX64")

	sourceSets {
		val commonMain by getting {
			dependencies {
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlin_coroutines_version")
				implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinx_datetime_version")
				implementation("de.rdvsb:kmapi:$kmapi_version")
			}
		}
		val commonTest by getting {
			dependencies {
				implementation(kotlin("test-common"))
				implementation(kotlin("test-annotations-common"))
			}
		}
		val jvmMain by getting

		val jvmTest by getting {
			dependencies {
				implementation(kotlin("test-junit"))
			}
		}
		//val nativeTest by getting

		val nativeCommon by creating {
			dependsOn(commonMain)
		}
		val nativeTest by creating {
			dependsOn(commonTest)
		}
		println("kotlin.sourceSets.nativeCommon.srcDirs: ${nativeCommon.kotlin.srcDirs}")

		val linuxX64Main by getting {
			dependsOn(nativeCommon)
		}

		val linuxX64Test by getting {
			dependsOn(nativeTest)
		}

		val mingwX64Main by getting {
			dependsOn(nativeCommon)
		}

		all {
			languageSettings.optIn("kotlin.time.ExperimentalTime")
			languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
		}

	}


}



//println("kotlin.sourceSets: ${kotlin.sourceSets.names}")

