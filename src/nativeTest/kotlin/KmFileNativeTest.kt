/*
 * Copyright 2021 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import kotlin.test.Test
import kotlin.test.assertTrue
import de.rdvsb.kmapi.*


internal class FileNativeTest {
	private val tstFile = File("/tmp/x.x")

	@Test
	fun fileName() {


		logMessage('I', "start\ntest on ${ticAsDateTime()}")
		println("FileNativeTest.fileName start")

		println("  fileName ${tstFile.name}")
		assertTrue(tstFile.name.startsWith("x.x"))

		println("FileNativeTest.fileName end")
	}
}

