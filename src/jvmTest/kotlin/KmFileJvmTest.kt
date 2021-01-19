package de.rdvsb.kmutil

import kotlin.test.Test
import kotlin.test.assertTrue
//import java.io.File
import de.rdvsb.kmapi.*
import kotlin.time.ExperimentalTime

internal class FileJvmTest {
	private val tstFile = File("/tmp/x.x")
	private val f = java.io.File("/tmp/x1.x")

//	val x = tstFile.bufferedWriter()



	@ExperimentalTime
	@Test
	fun fileName() {
		println("FileJvmTest.fileName start")



		var timeout: kotlin.time.Duration = kotlin.time.Duration.INFINITE

		println("  fileName=\"${tstFile.name}\" f=\"${f.name}\", tst=${tstFile.absoluteFile.path}")
		assertTrue(tstFile.name.startsWith("x.x"))

		println("FileJvmTest.fileName end")

	}

	@Test
	fun walkDirTest() {
		println("FileJvmTest.walkDirTest start")

		println("FileJvmTest.walkDirTest end")
	}

}