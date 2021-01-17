package de.rdvsb.kmutil

import de.rdvsb.kmapi.File

/**
 * basic getArgs class.
 *
 * Only members which are needed in old_scriptUtil.kt are defined here
 *
 * **Note**: Must be inherited to create real getArgs object
 */
public open class BasicGetArgs {
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
		set(value)  {
			val v = value.substringAfterLast(File.separatorChar).substringBeforeLast('.')
			val changed =  field != v
			field = v
			if (changed) {
				logMessage.logName = "$field.log"

			}
		}

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

