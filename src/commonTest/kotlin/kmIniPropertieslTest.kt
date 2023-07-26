/*
 * Copyright 2021-2023 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import kotlin.test.*

internal class IniPropertiesCommonTest {

	@Test
	fun iniReaderTest() {
		println("IniPropertiesCommonTest.iniReaderTest start")

		assertEquals("val =", IniProperties.propertyAsDisplay("val", null))
		assertEquals("val = 'a\"b'", IniProperties.propertyAsDisplay("val", "a\"b"))
		assertEquals("val = \"a'b\"", IniProperties.propertyAsDisplay("val", "a\'b"))
		assertEquals("val = \"ab\"", IniProperties.propertyAsDisplay("val", "ab"))
		assertEquals("val = 12", IniProperties.propertyAsDisplay("val", 12))
		assertEquals("val = 1.2", IniProperties.propertyAsDisplay("val", 1.2))
		assertEquals("val = <HIDDEN>", IniProperties.propertyAsDisplay("val", "ab", true))

		///////////////////////////////////////////////////////////////
		// test generic ini reader
		val iniReader = IniProperties().apply {
			hidePropertiesMap["password"] = Unit
		}

		val iniText = """
			|### test ini parsing ###
			|   ; also a comment
			| noVal =
			|paramInt = 10
			|paramStr1 = "it's a string param hello"
			| paramStr2 = 'this is a "string" too'
			| password = "secret"
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
		//logMessage('D', "parse ini data\n$iniText")
		iniReader.parse(iniText)

		var expectIniText = iniText.lineSequence()
			.map { it.trim() }
			.filterNot { it.isBlank() || it.startsWith('#') || it.startsWith(';') }
			.map { if (it.startsWith('[')) "\n$it" else it }
			.map { if (it.startsWith("password")) "password = <HIDDEN>" else it }
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
			var password: String = "",
			var sect1_name: String = "",
			var sect1_val: Double = 0.0,
			var sect2_name: String = "",
			var sect2_val: Long = 0,
		) : IniProperties() {
			init {
				hidePropertiesMap["password"] = Unit
			}

			override fun asText(): String {
				return """
					|### test ini parsing ###
					|   ; also a comment
					| ${propertyAsDisplay("noVal", noVal)}
					|${propertyAsDisplay("paramInt", paramInt)}
					|${propertyAsDisplay("paramStr1", paramStr1)}
					| ${propertyAsDisplay("paramStr2", paramStr2)}
					| ${propertyAsDisplay("password", password)}
					|
					|#---------------------------
					| [Sect1]
					|    ${propertyAsDisplay("name", sect1_name)}
					|    ${propertyAsDisplay("val", sect1_val)}
					|
					|#---------------------------
					| [Sect2]
					|    ${propertyAsDisplay("name", sect2_name)}
					|    ${propertyAsDisplay("val", sect2_val)}
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
						"password" -> password = value ?: ""
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
		logMessage('I', "got custom ini data\n$iniTextRetrieved")

		expectIniText = iniText.replace("\"secret\"", "<HIDDEN>")

		assertEquals(expectIniText, iniTextRetrieved)

		var extra_extraParam: Double? = null

		iniReader.parse("[Extra]\nextraParam = 1.4")

		tstIniData.parse("[Extra]\nextraParam = 1.4") {  section, property, value, line ->
			if (section == "Extra" && property == "extraParam") extra_extraParam = value?.toDoubleOrNull()
		}
		assertEquals(1.4, extra_extraParam)

		println("IniPropertiesCommonTest.iniReaderTest end")
	}
}