package de.rdvsb.kmutil

import kotlin.test.Test
import kotlin.test.assertTrue
import de.rdvsb.kmapi.*


internal class FileUtilCommonTest {
	private val tstFile = File("..${File.separatorChar}x.x")

	@Test
	fun fileName() {
		println("FileUtilCommonTest.fileName start")

		println("  File: name=${tstFile.name} path=${tstFile.path} absolutePath=${tstFile.absolutePath}  canonicalPath=${tstFile.canonicalPath}")
		assertTrue(tstFile.name.startsWith("x.x"))
		assertTrue(tstFile.path.startsWith("..${File.separatorChar}"))

		println("FileUtilCommonTest.fileName end")
	}

}
