/*
 * Copyright 2021-2023 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals

internal class StringCommonTest {

	@Test
	fun fmtTest() {
		println("StringCommonTest.fmtTest start")

		assertEquals("1.2", 1.2345.toFmt("%1.1f"))
		assertEquals("1.3", 1.2645.toFmt("%1.1f"))

		println("StringCommonTest.fmtTest end")
	}

	@Test
	fun fmtMetricTest() {
		println("StringCommonTest.fmtMetricTest start")

		data class fmtMetricTest(val expectedResult: String, val d: Double, val unit: String, val width: Int = 6, val prec: Int = 3, val metricPrefix:String = " ") {
			fun genTest() {
				println("\t\t\tfmtMetricTest(\"${d.fmtMetric(unit, width, prec, metricPrefix)}\", $d, \"$unit\", $width, $prec, \"$metricPrefix\"),")
			}
			fun doTest() {
				assertEquals(expectedResult, d.fmtMetric(unit, width, prec, metricPrefix), "fmtMetricTest failed for $this")
				if (d != 0.0) {
					val expectNegativeResult = ("-"+expectedResult.trimStart()).padStart(expectedResult.length,' ')
					assertEquals(expectNegativeResult, (-d).fmtMetric(unit, width, prec, metricPrefix), "fmtMetricTest failed for negative $this")
				}
			}
		}
		val fmtMetricTests  = arrayListOf(
			fmtMetricTest(" 0.000 m", 0.0, "m", 6, 3, " "),
			fmtMetricTest("0.0m", 0.0, "m", 3, 1, ""),
			fmtMetricTest(" 0.000 m", 0.0, "m", 6, 3, " "),
			fmtMetricTest("0.0m", 0.0, "m", 3, 1, ""),

			fmtMetricTest(" 0.100 ym", 1.0E-25, "m", 6, 3, " "),
			fmtMetricTest(" 0.500 ym", 5.0E-25, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 ym", 9.99999E-25, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 ym", 1.0E-24, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 ym", 5.0E-24, "m", 6, 3, " "),
			fmtMetricTest("10.000 ym", 9.99999E-24, "m", 6, 3, " "),
			fmtMetricTest("10.000 ym", 1.0E-23, "m", 6, 3, " "),
			fmtMetricTest("50.000 ym", 5.0E-23, "m", 6, 3, " "),
			fmtMetricTest("100.000 ym", 9.99999E-23, "m", 6, 3, " "),
			fmtMetricTest("100.000 ym", 1.0E-22, "m", 6, 3, " "),
			fmtMetricTest("500.000 ym", 5.0E-22, "m", 6, 3, " "),
			fmtMetricTest("999.999 ym", 9.99999E-22, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 zm", 1.0E-21, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 zm", 5.0E-21, "m", 6, 3, " "),
			fmtMetricTest("10.000 zm", 9.99999E-21, "m", 6, 3, " "),
			fmtMetricTest("10.000 zm", 1.0E-20, "m", 6, 3, " "),
			fmtMetricTest("50.000 zm", 5.0E-20, "m", 6, 3, " "),
			fmtMetricTest("100.000 zm", 9.99999E-20, "m", 6, 3, " "),
			fmtMetricTest("100.000 zm", 1.0E-19, "m", 6, 3, " "),
			fmtMetricTest("500.000 zm", 5.0E-19, "m", 6, 3, " "),
			fmtMetricTest("999.999 zm", 9.99999E-19, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 am", 1.0E-18, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 am", 5.0E-18, "m", 6, 3, " "),
			fmtMetricTest("10.000 am", 9.99999E-18, "m", 6, 3, " "),
			fmtMetricTest("10.000 am", 1.0E-17, "m", 6, 3, " "),
			fmtMetricTest("50.000 am", 5.0E-17, "m", 6, 3, " "),
			fmtMetricTest("100.000 am", 9.99999E-17, "m", 6, 3, " "),
			fmtMetricTest("100.000 am", 1.0E-16, "m", 6, 3, " "),
			fmtMetricTest("500.000 am", 5.0E-16, "m", 6, 3, " "),
			fmtMetricTest("999.999 am", 9.99999E-16, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 fm", 1.0E-15, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 fm", 5.0E-15, "m", 6, 3, " "),
			fmtMetricTest("10.000 fm", 9.99999E-15, "m", 6, 3, " "),
			fmtMetricTest("10.000 fm", 1.0E-14, "m", 6, 3, " "),
			fmtMetricTest("50.000 fm", 5.0E-14, "m", 6, 3, " "),
			fmtMetricTest("100.000 fm", 9.99999E-14, "m", 6, 3, " "),
			fmtMetricTest("100.000 fm", 1.0E-13, "m", 6, 3, " "),
			fmtMetricTest("500.000 fm", 5.0E-13, "m", 6, 3, " "),
			fmtMetricTest("999.999 fm", 9.99999E-13, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 pm", 1.0E-12, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 pm", 5.0E-12, "m", 6, 3, " "),
			fmtMetricTest("10.000 pm", 9.99999E-12, "m", 6, 3, " "),
			fmtMetricTest("10.000 pm", 1.0E-11, "m", 6, 3, " "),
			fmtMetricTest("50.000 pm", 5.0E-11, "m", 6, 3, " "),
			fmtMetricTest("100.000 pm", 9.99999E-11, "m", 6, 3, " "),
			fmtMetricTest("100.000 pm", 1.0E-10, "m", 6, 3, " "),
			fmtMetricTest("500.000 pm", 5.0E-10, "m", 6, 3, " "),
			fmtMetricTest("999.999 pm", 9.99999E-10, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 nm", 1.0E-9, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 nm", 5.0E-9, "m", 6, 3, " "),
			fmtMetricTest("10.000 nm", 9.99999E-9, "m", 6, 3, " "),
			fmtMetricTest("10.000 nm", 1.0E-8, "m", 6, 3, " "),
			fmtMetricTest("50.000 nm", 5.0E-8, "m", 6, 3, " "),
			fmtMetricTest("100.000 nm", 9.99999E-8, "m", 6, 3, " "),
			fmtMetricTest("100.000 nm", 1.0E-7, "m", 6, 3, " "),
			fmtMetricTest("500.000 nm", 5.0E-7, "m", 6, 3, " "),
			fmtMetricTest("999.999 nm", 9.99999E-7, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 µm", 1.0E-6, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 µm", 5.0E-6, "m", 6, 3, " "),
			fmtMetricTest("10.000 µm", 9.99999E-6, "m", 6, 3, " "),
			fmtMetricTest("10.000 µm", 1.0E-5, "m", 6, 3, " "),
			fmtMetricTest("50.000 µm", 5.0E-5, "m", 6, 3, " "),
			fmtMetricTest("100.000 µm", 9.99999E-5, "m", 6, 3, " "),
			fmtMetricTest("100.000 µm", 1.0E-4, "m", 6, 3, " "),
			fmtMetricTest("500.000 µm", 5.0E-4, "m", 6, 3, " "),
			fmtMetricTest("999.999 µm", 9.99999E-4, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 mm", 0.001, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 mm", 0.005, "m", 6, 3, " "),
			fmtMetricTest("10.000 mm", 0.00999999, "m", 6, 3, " "),
			fmtMetricTest("10.000 mm", 0.01, "m", 6, 3, " "),
			fmtMetricTest("50.000 mm", 0.05, "m", 6, 3, " "),
			fmtMetricTest("100.000 mm", 0.0999999, "m", 6, 3, " "),
			fmtMetricTest("100.000 mm", 0.1, "m", 6, 3, " "),
			fmtMetricTest("500.000 mm", 0.5, "m", 6, 3, " "),
			fmtMetricTest("999.999 mm", 0.999999, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 m", 1.0, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 m", 5.0, "m", 6, 3, " "),
			fmtMetricTest("10.000 m", 9.99999, "m", 6, 3, " "),
			fmtMetricTest("10.000 m", 10.0, "m", 6, 3, " "),
			fmtMetricTest("50.000 m", 50.0, "m", 6, 3, " "),
			fmtMetricTest("100.000 m", 99.9999, "m", 6, 3, " "),
			fmtMetricTest("100.000 m", 100.0, "m", 6, 3, " "),
			fmtMetricTest("500.000 m", 500.0, "m", 6, 3, " "),
			fmtMetricTest("999.999 m", 999.999, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 Km", 1000.0, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 Km", 5000.0, "m", 6, 3, " "),
			fmtMetricTest("10.000 Km", 9999.99, "m", 6, 3, " "),
			fmtMetricTest("10.000 Km", 10000.0, "m", 6, 3, " "),
			fmtMetricTest("50.000 Km", 50000.0, "m", 6, 3, " "),
			fmtMetricTest("100.000 Km", 99999.9, "m", 6, 3, " "),
			fmtMetricTest("100.000 Km", 100000.0, "m", 6, 3, " "),
			fmtMetricTest("500.000 Km", 500000.0, "m", 6, 3, " "),
			fmtMetricTest("999.999 Km", 999999.0, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 Mm", 1000000.0, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 Mm", 5000000.0, "m", 6, 3, " "),
			fmtMetricTest("10.000 Mm", 9999990.0, "m", 6, 3, " "),
			fmtMetricTest("10.000 Mm", 1.0E7, "m", 6, 3, " "),
			fmtMetricTest("50.000 Mm", 5.0E7, "m", 6, 3, " "),
			fmtMetricTest("100.000 Mm", 9.99999E7, "m", 6, 3, " "),
			fmtMetricTest("100.000 Mm", 1.0E8, "m", 6, 3, " "),
			fmtMetricTest("500.000 Mm", 5.0E8, "m", 6, 3, " "),
			fmtMetricTest("999.999 Mm", 9.99999E8, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 Gm", 1.0E9, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 Gm", 5.0E9, "m", 6, 3, " "),
			fmtMetricTest("10.000 Gm", 9.99999E9, "m", 6, 3, " "),
			fmtMetricTest("10.000 Gm", 1.0E10, "m", 6, 3, " "),
			fmtMetricTest("50.000 Gm", 5.0E10, "m", 6, 3, " "),
			fmtMetricTest("100.000 Gm", 9.99999E10, "m", 6, 3, " "),
			fmtMetricTest("100.000 Gm", 1.0E11, "m", 6, 3, " "),
			fmtMetricTest("500.000 Gm", 5.0E11, "m", 6, 3, " "),
			fmtMetricTest("999.999 Gm", 9.99999E11, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 Tm", 1.0E12, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 Tm", 5.0E12, "m", 6, 3, " "),
			fmtMetricTest("10.000 Tm", 9.99999E12, "m", 6, 3, " "),
			fmtMetricTest("10.000 Tm", 1.0E13, "m", 6, 3, " "),
			fmtMetricTest("50.000 Tm", 5.0E13, "m", 6, 3, " "),
			fmtMetricTest("100.000 Tm", 9.99999E13, "m", 6, 3, " "),
			fmtMetricTest("100.000 Tm", 1.0E14, "m", 6, 3, " "),
			fmtMetricTest("500.000 Tm", 5.0E14, "m", 6, 3, " "),
			fmtMetricTest("999.999 Tm", 9.99999E14, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 Pm", 1.0E15, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 Pm", 5.0E15, "m", 6, 3, " "),
			fmtMetricTest("10.000 Pm", 9.99999E15, "m", 6, 3, " "),
			fmtMetricTest("10.000 Pm", 1.0E16, "m", 6, 3, " "),
			fmtMetricTest("50.000 Pm", 5.0E16, "m", 6, 3, " "),
			fmtMetricTest("100.000 Pm", 9.99999E16, "m", 6, 3, " "),
			fmtMetricTest("100.000 Pm", 1.0E17, "m", 6, 3, " "),
			fmtMetricTest("500.000 Pm", 5.0E17, "m", 6, 3, " "),
			fmtMetricTest("999.999 Pm", 9.99999E17, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 Em", 1.0E18, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 Em", 5.0E18, "m", 6, 3, " "),
			fmtMetricTest("10.000 Em", 9.99999E18, "m", 6, 3, " "),
			fmtMetricTest("10.000 Em", 1.0E19, "m", 6, 3, " "),
			fmtMetricTest("50.000 Em", 5.0E19, "m", 6, 3, " "),
			fmtMetricTest("100.000 Em", 9.99999E19, "m", 6, 3, " "),
			fmtMetricTest("100.000 Em", 1.0E20, "m", 6, 3, " "),
			fmtMetricTest("500.000 Em", 5.0E20, "m", 6, 3, " "),
			fmtMetricTest("999.999 Em", 9.99999E20, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 Zm", 1.0E21, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 Zm", 5.0E21, "m", 6, 3, " "),
			fmtMetricTest("10.000 Zm", 9.99999E21, "m", 6, 3, " "),
			fmtMetricTest("10.000 Zm", 1.0E22, "m", 6, 3, " "),
			fmtMetricTest("50.000 Zm", 5.0E22, "m", 6, 3, " "),
			fmtMetricTest("100.000 Zm", 9.99999E22, "m", 6, 3, " "),
			fmtMetricTest("100.000 Zm", 1.0E23, "m", 6, 3, " "),
			fmtMetricTest("500.000 Zm", 5.0E23, "m", 6, 3, " "),
			fmtMetricTest("999.999 Zm", 9.99999E23, "m", 6, 3, " "),
			fmtMetricTest(" 1.000 Ym", 1.0E24, "m", 6, 3, " "),
			fmtMetricTest(" 5.000 Ym", 5.0E24, "m", 6, 3, " "),
			fmtMetricTest("10.000 Ym", 9.99999E24, "m", 6, 3, " "),
			fmtMetricTest("10.000 Ym", 1.0E25, "m", 6, 3, " "),
			fmtMetricTest("50.000 Ym", 5.0E25, "m", 6, 3, " "),
			fmtMetricTest("100.000 Ym", 9.99999E25, "m", 6, 3, " "),
			fmtMetricTest("100.000 Ym", 1.0E26, "m", 6, 3, " "),
			fmtMetricTest("500.000 Ym", 5.0E26, "m", 6, 3, " "),
			fmtMetricTest("999.999 Ym", 9.99999E26, "m", 6, 3, " "),
			fmtMetricTest("1,000.000 Ym", 1.0E27, "m", 6, 3, " "),
			fmtMetricTest("5,000.000 Ym", 5.0E27, "m", 6, 3, " "),
			fmtMetricTest("9,999.990 Ym", 9.99999E27, "m", 6, 3, " "),

			fmtMetricTest("  187.654 MByte", 187_654_321.0, "Byte", 9, 3, " "),
			fmtMetricTest("  687.654 MByte", 687_654_321.0, "Byte", 9, 3, " "),
			fmtMetricTest("  987.654 MByte", 987_654_321.0, "Byte", 9, 3, " "),
			fmtMetricTest("  999.999 MByte", 999_999_321.0, "Byte", 9, 3, " "),
		)

//      fmtMetricTest("", 0.0, "m").genTest()
//		fmtMetricTest("", 0.0, "m", 3, 1, "").genTest()
//
//		for (e in -25 .. 27) {
//			val d = 10.0.pow(e)
//			fmtMetricTest("", d, "m").genTest()
//			fmtMetricTest("", d*5.0, "m").genTest()
//			fmtMetricTest("", d*9.99999, "m").genTest()
//		}

		for (fmTst in fmtMetricTests) {
			//continue
			//fmTst.d > 1.0 && continue
			//fmTst.genTest()
			fmTst.doTest()
		}

		println("StringCommonTest.fmtMetricTest end")
	}

	@Test
	fun strTest() {
		println("StringCommonTest.strTest start")

		val hello = "Hello World Of Sol"
		assertEquals(hello, hello.abbreviate(100))
		assertEquals(hello, hello.abbreviate(hello.length))
		assertEquals( "Hello...", hello.abbreviate(8))
		assertEquals( "Hello (cut)", hello.abbreviate(11, " (cut)"))
		assertEquals( "...", hello.abbreviate(3))
		assertEquals( "..", hello.abbreviate(2))
		assertEquals( ".", hello.abbreviate(1))
		assertEquals( "", hello.abbreviate(0))

		println("StringCommonTest.strTest end")
	}

}