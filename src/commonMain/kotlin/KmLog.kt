/*
 * Copyright 2021 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil
import de.rdvsb.kmapi.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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
	public var isLogToStdout: Boolean // emit log to stdout instead of stderr
	public var isQuiet: Boolean // don't emit log messages to stderr/stdout
	public var isStdWithTimestamp: Boolean // emit log to stdout/stderr with timestamp
	public operator fun invoke(ts: String?, id: String?, msgId: Char = ' ', msgSep: Char = '|', msg: String): Boolean
	public operator fun invoke(id: String?, msgId: Char = ' ', vararg msgs: String): Boolean
	public operator fun invoke(msgId: Char = ' ', vararg msgs: String): Boolean
}

public var logMessage.logPath: String  // full path
	get() = when {
		logName.isEmpty() -> ""
		logDir.isEmpty()  -> logName
		else              -> "${logDir}${KmFile.separator}${logName}"
	}
	set(value) {
		val ix = value.lastIndexOf(KmFile.separatorChar)
		logDir = when {
			ix < 0  -> ""
			ix == 0 -> KmFile.separator
			else    -> value.substring(0, ix)
		}
		logName = value.substring(ix + 1)
		with(logDir) { println(this)}

	}

public inline fun <R> withLogMessage(id: String?, msgId: Char = ' ', vararg msgs: String, block: ()->R ): R {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	logMessage(id, msgId, *msgs)
	return block()
}

public inline fun <R> withLogMessage(msgId: Char = ' ', vararg msgs: String, block: ()->R ): R {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	logMessage(null, msgId, *msgs)
	return block()
}


/**
 * try to extract a timestamp severity and separator from single line msg and use these to log the message
 */
public expect fun logMessageNestedLine(id: String?, msgId: Char, msg: String): Boolean

/**
 * try to extract a timestamp severity and separator from single line msg and use these to log the message
 */
public fun logMessageNestedLine(msgId: Char = ' ', msg: String): Boolean {
	return logMessageNestedLine(null, msgId, msg)
}

/**
 * try to extract a timestamp severity and separator from possibly multiline msg and use these to log the message
 */
public fun logMessageNested(id: String?, msgId: Char, msg: String): Boolean {
	msg.splitToSequence("\n").forEach {
		logMessageNestedLine(id, msgId, it)
	}
	return true
}

/**
 * try to extract a timestamp severity and separator from possibly multiline msg and use these to log the message
 */
public fun logMessageNested(msgId: Char, msg: String): Boolean {
	msg.splitToSequence("\n").forEach {
		logMessageNestedLine(null, msgId, it)
	}
	return true
}
