/*
 * Copyright 2021 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import kotlin.test.Test
import kotlin.test.assertTrue
//import java.io.File
import de.rdvsb.kmapi.*
import kotlin.time.ExperimentalTime

internal class FileJvmTest {
	private val tstFile = KmFile("/tmp/x.x")
	private val f = java.io.File("/tmp/x1.x")

//	val x = tstFile.bufferedWriter()



	@Test
	fun fileName() {
		withLogMessage('D', "fileName:") {
			println("FileJvmTest.fileName start")
		}

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