/*
 * Copyright 2021 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals


internal class KmLogKtTest {

	@Test
	fun logTest() {
		println("StringCommonTest.logTest start")

		logMessageNested("test", 'D', "2022-02-21 18:00:33 M|>  Available export files:")
		logMessageNested("test", 'D', "2022-02-21 18:00:33 M+>    test.xml")
		logMessageNested( 'D', "2022-02-21 18:00:33.12356 F|Error occurred:this is a test line")
		logMessageNested( 'D', "2022-02-21 18:00:33.124 F+  this is a test line\nnew line")
		logMessageNested('D', "cmd> 2022-03-30 17:51:00 I|getState Prod")

		println("StringCommonTest.logTest start warnings=${logMessage.countWarning} errors=${logMessage.countError} fatal=${logMessage.countFatal}")
	}
}