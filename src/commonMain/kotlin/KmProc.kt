/*
 * Copyright 2021 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import kotlin.time.ExperimentalTime
import de.rdvsb.kmapi.*

/**
 * split a command line string into multiple arguments like the shell but without globbing support
 * @return list of args
 */
public fun String.splitShellArgs(): List<String> {
	val tokens = mutableListOf<String>()

	//println ("splitShellArgs |$this|")

	val token = StringBuilder(length)
	var delim: Char = '\u0000'  // if != chr(0): in a string with that delimiter (e.g. "a b")

	iterator().apply {
		var isAppendEmpty = false
		for (ch in this) {
			when (ch) {
				'\\' -> {
					val nch = if (delim != '\'' && this.hasNext()) this.next() else '\\'  // no escaping in '-delimited string
					if (delim == '"' && nch != '"' && nch != '\\') token.append(
						'\\') // 'a\"b' => a\"b | "a\'b" => a\'b (in delimited arg but escaped char is not the delimiter => keep backslash)
					token.append(
						nch)                                                 // "a\"b" => a"b | \' => ' | \" => "  \t => t (escaped delimiter in "-delimited arg or escaped delimiter out of delimited arg => remove backslash)
				}

				'"', '\'' -> {
					isAppendEmpty = true
					when (delim) {
						'\u0000' -> delim = ch // start a delimited arg
						ch       -> delim = '\u0000' // end a delimited arg but do not add token  "a b"x => a bx
						else     -> token.append(ch) // other delimiter in delimited arg is normal char
					}
				}

				' ', '\t', '\n', '\r' -> {
					if (delim == '\u0000') { // not in a delimited arg
						if (isAppendEmpty || token.isNotEmpty()) {
							tokens.add(token.toString())
							token.clear()
							isAppendEmpty = false
						}
					} else {
						token.append(ch)
					}
				}
				else                  -> token.append(ch)
			}
		}
		if (isAppendEmpty || token.length > 0) tokens.add(token.toString())
	}

	return tokens
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * run a binary app with args
 * quoted args are supported (exactly like shell argument quoting)
 * no scripts supported no arg globbing done
 * @return the called process exit value
 */
public expect fun runBinary(cmd: String, timeout: kotlin.time.Duration = kotlin.time.Duration.INFINITE): Int
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public enum class LineFrom {OUT, ERR}

/**
 * run via shell so globbing and command grouping or chaining with semicolons is supported
 * stdin is inherited from current process
 * stderr and stdout are captured and a supplied callback function is called for each captured stdin/stderr line
 *
 * **Note**: the processLine callback is called concurrently from two different threads
 * @return the called process exit value
 */
public expect fun system(cmd: String, timeout: kotlin.time.Duration = kotlin.time.Duration.INFINITE, processLine: (line: String, lineFrom: LineFrom, process: KmProcess) -> Unit): Int

/**
 * run via shell so globbing and command grouping or chaining with semicolons is supported
 * stdin is inherited from current process
 * stderr and stdout lines are written to log file tagged with tag
 * @return the called process exit value
 */
public fun systemWithLog(cmd: String, tag: String, timeout: kotlin.time.Duration = kotlin.time.Duration.INFINITE): Int {
	return system(cmd, timeout) { line, lineFrom, _ ->
		val msgId = if (lineFrom == LineFrom.OUT) 'I' else 'E'
		logMessage(msgId, "$tag$line")
	}
}

/**
 * run via shell so globbing and command grouping or chaining with semicolons is supported
 * stdin is inherited from current process
 * stderr and stdout are written to current handles
 * @return the called process exit value
 */
public expect fun system(cmd: String, timeout: kotlin.time.Duration = kotlin.time.Duration.INFINITE): Int

public fun die(errText: String? = null): Nothing {
	errText?.let { System.err.println("+++ $errText") }
	throw RuntimeException(errText)
}

public val onExitRunCmds: ArrayList<String> = arrayListOf<String>()

/**
 * log message and exit application
 * @param status return code to OS
 * @param msg optional message to log (msgId depends on status)
 *
 */
public fun exit(status: Int, msg: String? = null): Nothing {
	if (msg != null) {
		logMessage(
			when (status) {
				0 -> 'I'
				1 -> 'E'
				else -> 'F'
			},
			msg
		)
	}
	for (cmd in onExitRunCmds) {
		system(cmd)
	}

	System.exit(status)
}


public expect class GlobalLock(lockName: String, doLock: Boolean = true): Closeable {
	public fun isLocked(): Boolean

	public fun lock(): GlobalLock
	public fun tryLock(): GlobalLock?

	public fun unlock(): GlobalLock

	public override fun close(): Unit
}
