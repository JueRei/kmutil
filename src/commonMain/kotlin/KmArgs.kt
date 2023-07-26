/*
 * Copyright 2021-2023 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import de.rdvsb.kmapi.KmFile
import de.rdvsb.kmapi.System

/**
 * basic getArgs class.
 *
 * Only members which are needed in old_scriptUtil.kt are defined here
 *
 * **Note**: Must be inherited to create real getArgs object
 */
public open class BasicGetArgs {
	protected fun updateAppPath(getArgs: BasicGetArgs): String	{
		val appPath = de.rdvsb.kmapi.computeAppPath(getArgs)
		System.setProperty("app-name", appPath)
		return appPath
	}
	public var appPath: String = "_basicGetArgs_not_yet_overwritten_by_real_getArgs_object_"
		get() = field
		set(value)  {
			val changed =  field != value
			field = value
			if (changed) {
				appName = field

			}
		}
	public var appName: String = "app"
		get() = field
		set(value) {
			val v = value.substringAfterLast(KmFile.separatorChar).substringBeforeLast('.')
			val changed =  field != v
			field = v
			if (changed) {
				logMessage.logName = "$field.log"

			}
		}

	public var isTest: Boolean = false
	public val isNotTest: Boolean get() = !isTest

	public var isVerbose: Boolean = false
	public val isNotVerbose: Boolean get() = !isVerbose

	public var isQuiet: Boolean = false
	public val isNotQuiet: Boolean	get() = !isQuiet

	public var debugLevel: Int = 0
	public val isDebug: Boolean get() = debugLevel != 0
	public val isNotDebug: Boolean get() = debugLevel == 0
}

/**
 * basic getArgs object.
 *
 * **Note**: initialized with a placeholder. Must be overwritten in main() with real getArgs object
 */
public var basicGetArgs: BasicGetArgs = BasicGetArgs()

