/*
 * Copyright 2021 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import kotlin.test.*

internal class IniPropertiesCommonTest {

	@Test
	fun iniReaderTest() {
		println("IniPropertiesCommonTest.iniReaderTest start")

		assertEquals("", IniProperties.valAsDisplay(null))
		assertEquals("'a\"b'", IniProperties.valAsDisplay("a\"b"))
		assertEquals("\"a'b\"", IniProperties.valAsDisplay("a\'b"))
		assertEquals("\"ab\"", IniProperties.valAsDisplay("ab"))
		assertEquals("12", IniProperties.valAsDisplay(12))
		assertEquals("1.2", IniProperties.valAsDisplay(1.2))

		///////////////////////////////////////////////////////////////
		// test basic ini reader
		val iniReader = IniProperties()

		val iniText = """
			|### test ini parsing ###
			|   ; also a comment
			| noVal = 
			|paramInt = 10
			|paramStr1 = "it's a string param hello" 
			| paramStr2 = 'this is a "string" too'
			|
			|#---------------------------
			| [Sect1]
			|    name = "section 1"
			|    val = 1.5
			|
			|#---------------------------
			| [Sect2]
			|    name = "section 2"
			|    val = 2
		""".trimMargin()
		logMessage('D', "parse ini data\n$iniText")
		iniReader.parse(iniText)

		var expectIniText = iniText.lineSequence()
			.map { it.trim() }
			.filterNot { it.isBlank() || it.startsWith('#') || it.startsWith(';') }
			.map { if (it.startsWith('[')) "\n$it" else it }
			.joinToString("\n") + '\n'

		var iniTextRetrieved = iniReader.asText()
		logMessage('I', "got ini data\n$iniTextRetrieved")
		logMessage('I', "expect ini data\n$expectIniText")
		assertEquals(expectIniText, iniTextRetrieved)


		iniReader.parse("[Extra]\nextraParam = 1.4")
		assertEquals("1.4", iniReader.section2PropertyMap["Extra"]?.get("extraParam"))

		///////////////////////////////////////////////////////////////
		// test customized ini reader
		data class TstIniData(
			var noVal: String? = null,
			var paramInt: Int = 0,
			var paramStr1: String = "",
			var paramStr2: String = "",
			var sect1_name: String = "",
			var sect1_val: Double = 0.0,
			var sect2_name: String = "",
			var sect2_val: Long = 0,
		) : IniProperties() {
			override fun asText(): String {
				return """
					|### test ini parsing ###
					|   ; also a comment
					| noVal = ${valAsDisplay(noVal)}
					|paramInt = ${valAsDisplay(paramInt)}
					|paramStr1 = ${valAsDisplay(paramStr1)} 
					| paramStr2 = ${valAsDisplay(paramStr2)}
					|
					|#---------------------------
					| [Sect1]
					|    name = ${valAsDisplay(sect1_name)}
					|    val = ${valAsDisplay(sect1_val)}
					|
					|#---------------------------
					| [Sect2]
					|    name = ${valAsDisplay(sect2_name)}
					|    val = ${valAsDisplay(sect2_val)}
				""".trimMargin()
			}
		}

		val tstIniData = TstIniData().apply {
			parse(iniText) { section, property, value, line ->
				when (section) {
					null    -> when (property) {
						"noVal"     -> noVal = value
						"paramInt"  -> paramInt = value?.toIntOrNull() ?: 0
						"paramStr1" -> paramStr1 = value ?: ""
						"paramStr2" -> paramStr2 = value ?: ""
						else        -> fail("unexpected global ini property \"$property\" line=\"$line\"")

					}

					"Sect1" -> when (property) {
						null -> return@parse // section start
						"name" -> sect1_name = value ?: ""
						"val"  -> sect1_val = value?.toDoubleOrNull() ?: 0.0
						else   -> fail("unexpected [Sect1] ini property \"$property\" line=\"$line\"")
					}

					"Sect2" -> when (property) {
						null -> return@parse // section start
						"name" -> sect2_name = value ?: ""
						"val"  -> sect2_val = value?.toLongOrNull() ?: 0
						else   -> fail("unexpected [Sect2] ini property \"$property\" line=\"$line\"")
					}

					else -> fail("unexpected [$section] ini line=\"$line\"")
				}
			}
		}

		iniTextRetrieved = tstIniData.asText()
		logMessage('I', "got custome ini data\n$iniTextRetrieved")
		assertEquals(iniText, iniTextRetrieved)

		var extra_extraParam: Double? = null

		iniReader.parse("[Extra]\nextraParam = 1.4")

		tstIniData.parse("[Extra]\nextraParam = 1.4") {  section, property, value, line ->
			if (section == "Extra" && property == "extraParam") extra_extraParam = value?.toDoubleOrNull()
		}
		assertEquals(1.4, extra_extraParam)

		println("IniPropertiesCommonTest.iniReaderTest end")
	}
}