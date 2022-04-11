/*
 * Copyright 2021 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import kotlin.test.Test
import kotlin.test.assertEquals

internal class UtilCommonTest {

	@Test
	fun collectionPadTest() {
		println("UtilCommonTest.collectionPadTest start")

		val l1 = emptyList<String>().padEnd(3, ".")
		assertEquals(3, l1.size)
		assertEquals("...", l1.joinToString(""))
		val l2 = "a=b".split("=").padEnd(2, "")
		val (a2, b2) = l2
		assertEquals(2, l2.size)
		assertEquals("a=b", "$a2=$b2")

		val l3 = "a:b".split("=").padEnd(2, "")
		val (a3, b3) = l3
		assertEquals(2, l3.size)
		assertEquals("a:b=", "$a3=$b3")

		println("UtilCommonTest.collectionPadTest start")
	}
}
