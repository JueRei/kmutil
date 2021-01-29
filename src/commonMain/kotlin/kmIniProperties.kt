/*
 * Copyright 2021 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import de.rdvsb.kmapi.*

public open class IniProperties() {
	public var iniFile: File? = null
	/**
	 * values read from INI
	 *
	 * * comments start with # or ;
	 * * property lines are PROPERTY = VALUE or PROPERTY = "VALUE" or PROPERTY = 'VALUE'
	 *    * the quotes are removed
	 *    * if VALUE is missing it is entered as null
	 * * sections start with `[SECTION]`
	 * * lines before first section are entered into valueMap as PROPERTY => VALUE
	 * * lines after first section entered as SECTION.PROPERTY => VALUE
	*/
	public var propertyMap: MutableMap<String, String?> = mutableMapOf()
	public var section2PropertyMap: MutableMap<String, MutableMap<String, String?>> = mutableMapOf()

	/**
	 * current section
	 */
	protected var currentSection: String? = null
	protected var currentSectionPropertyMap: MutableMap<String, String?> = mutableMapOf()

	/**
	 * subclass this function to verify or handle properties differently
	 * * [section]: name of the current section, null if no section started
	 * * [property]: name of the property, null if a new section starts
	 * * [value]: value of the property, null if no value follows the "=" or no "=" seen
	 */
	public fun enterSectionAndProperty(section: String?, property: String?, value: String?): Unit {
		if (property == null) { // a new section starts
			currentSection = section
			if (section != null) {
				currentSectionPropertyMap = mutableMapOf()
				section2PropertyMap[section] = currentSectionPropertyMap
			}
			return
		}
		if (section == null) propertyMap[property] = value // no section started
		else currentSectionPropertyMap[property] = value // section property
	}

	/**
	 * called after ini file completely read
	 *
	 * subclass this function to verify or handle properties differently
	 */
	public fun finished() {
	}

	private val matchSection = """^\[\s*([a-zA-Z0-9_.:=-]+)\s*\]$""".toRegex()
	private val matchProperty = """^([a-zA-Z0-9_.:-]+)\s*?=\s*(.*)""".toRegex()

	public fun parseLine(iniLine: String, parsedLineAction: (section: String?, property: String?, value: String?, line: String) -> Unit): Unit {
		iniLine.trim().let { line ->
			when {
				line.isBlank()       -> return
				line.startsWith('#') -> return
				line.startsWith(';') -> return
				line.startsWith('[') -> {
					val matchGroups = matchSection.find(line)?.groupValues
					if (matchGroups == null || matchGroups.size != 2) {
						logMessage('E', "illegal section start \"$line\"") && return
					} else {
						if (currentSection == null || currentSection != matchGroups[1]) parsedLineAction(matchGroups[1], null, null, line)
						currentSection = matchGroups[1]
					}
				}
				else                 -> {
					val matchGroups = matchProperty.find(line)?.groupValues
					if (matchGroups == null || matchGroups.size != 3) {
						logMessage('E', "illegal property line \"$line\"") && return
					} else {
						val property = matchGroups[1]
						var value: String = matchGroups[2]
						if (value.isEmpty()) {
							parsedLineAction(currentSection, property, null, line)
						} else {
							if (value[0] == '\'') {
								value = value.removeSurrounding("'")
							} else if (value[0] == '"') {
								value = value.removeSurrounding("\"")
							}
							parsedLineAction(currentSection, property, value, line)
						}
					}
				}
			}
		}
	}

	public fun parse(iniText: String, parsedLineAction: ((section: String?, property: String?, value: String?, line: String) -> Unit)? = null): IniProperties {
		iniText.lineSequence().forEach { iniLine ->
			parseLine(iniLine) { section, property, value, line ->
				if (parsedLineAction == null) enterSectionAndProperty (section, property, value)
				else parsedLineAction (section, property, value, line)
			}
		}
		finished()
		return this
	}

	public fun parse(iniFile: File, parsedLineAction: ((section: String?, property: String?, value: String?, line: String) -> Unit)? = null): Unit {
		this.iniFile = iniFile
		iniFile.forEachLine { iniLine ->
			parseLine(iniLine) { section, property, value, line ->
				if (parsedLineAction == null) enterSectionAndProperty (section, property, value)
				else parsedLineAction (section, property, value, line)
			}
		}
	}

	public companion object {
		public fun valAsDisplay(value: Any?): String {
			if (value == null) return ""
			return value.toString().run {
				when {
					contains('"')            -> "'$this'"
					contains('\'')           -> "\"$this\""
					toLongOrNull() != null   -> this
					toDoubleOrNull() != null -> this
					else                     -> "\"$this\""
				}
			}
		}
	}


	public open fun asText(): String {
		val textBuilder = StringBuilder(1024)

		for ((property, value) in propertyMap.entries.sortedBy { it.key }) {
			textBuilder.append("$property = ${valAsDisplay(value)}".trimEnd()).append('\n')
		}
		for ((section, sectionPropertyMap) in section2PropertyMap.entries.sortedBy { it.key }) {
			textBuilder.append("\n[$section]\n")
			for ((property, value) in sectionPropertyMap.entries.sortedBy { it.key }) {
				textBuilder.append("$property = ${valAsDisplay(value)}".trimEnd()).append('\n')
			}
		}
		return textBuilder.toString()
	}

}


