/*
 * Copyright 2021-2023 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import kotlin.test.Test
import kotlin.test.assertEquals

internal class UtilCommonTest {

	@Test
	fun collectionPadTest() {
		println("UtilCommonTest.collectionPadTest start")

		if (true) {
			val l = emptyList<String>().padEnd(3, ".")
			assertEquals(3, l.size)
			assertEquals("...", l.joinToString(""))
		}
		if (true) {
			val l = "a=b".split("=").padEnd(2, "")
			val (a, b) = l
			assertEquals(2, l.size)
			assertEquals("a=b", "$a=$b")
		}

		if (true) {
			val l = "a:b".split("=").padEnd(2, "")
			val (a, b) = l
			assertEquals(2, l.size)
			assertEquals("a:b=", "$a=$b")
		}

		if (true) {
			val l = "01:02:03".split(":").padStart(3, "0")
			val (hh, mm, ss) = l
			assertEquals(3, l.size)
			assertEquals("01:02:03", "$hh:$mm:$ss")
		}
		if (true) {
			val l = "01:02".split(":").padStart(3, "00")
			val (hh, mm, ss) = l
			assertEquals(3, l.size)
			assertEquals("00:01:02", "$hh:$mm:$ss")
		}

		println("UtilCommonTest.collectionPadTest end")
	}
}
