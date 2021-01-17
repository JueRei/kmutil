package de.rdvsb.kmutil

import kotlin.test.Test
import kotlin.test.assertTrue
import de.rdvsb.kmapi.*


internal class FileNativeTest {
	private val tstFile = File("/tmp/x.x")

	@Test
	fun fileName() {


		logMessage('I', "start\ntest")
		println("FileNativeTest.fileName start")

		println("  fileName ${tstFile.name}")
		assertTrue(tstFile.name.startsWith("x.x"))

		println("FileNativeTest.fileName end")
	}
}

