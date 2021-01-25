package de.rdvsb.kmutil
import de.rdvsb.kmapi.*

/**
 * log messages to log file
 * @return true
 */
public expect object logMessage {
	// implemented in nativeCommon and jvmMain
	public var logDirs: List<String> // try these for logDir if logDir is set to "*"
	public var logDir: String  // dir name
	public var logName: String // file base name
	public var maxLogSize: Long// max size of log before renaming (default 10MB)
	public var maxLogVersion: Int // keep this much old log versions (*log.1 .. *log.maxLogVersion) (default 9)
	public var countWarning: Int
	public var countError: Int
	public var countFatal: Int
	public var isQuiet: Boolean // don't emit log messages to stderr
	public operator fun invoke(msgId: Char = ' ', vararg msgs: String): Boolean
}

public var logMessage.logPath: String  // full path
	get() = when {
		logName.isEmpty() -> ""
		logDir.isEmpty()  -> logName
		else              -> "${logDir}${File.separator}${logName}"
	}
	set(value) {
		val ix = value.lastIndexOf(File.separatorChar)
		logDir = when {
			ix < 0  -> ""
			ix == 0 -> File.separator
			else    -> value.substring(0, ix)
		}
		logName = value.substring(ix + 1)

	}
