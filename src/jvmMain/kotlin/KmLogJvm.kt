/*
 * Copyright 2021 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import java.io.FileOutputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*
import de.rdvsb.kmapi.*

/**
 * log messages to log file
 * @return true
 */

// enforce en_US locale. Always use consistent decimal point and grouping chars for system logs
private val locale = Locale.setDefault(Locale("en", "US"))

private var tzNameBuf: String = ""
private val tzName: String
	get() {
		if (tzNameBuf.isEmpty()) {
			val dateFormat = SimpleDateFormat("zZ") // see https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
			tzNameBuf = dateFormat.format(System.currentTimeMillis())?:""
		}
		return tzNameBuf
	}


public actual object logMessage {
	private val windowsLogDirs: Array<String> = arrayOf("C:\\Local\\Logs", "C:\\Local\\Log", "log", "", System.getenv("Temp") ?: "-", System.getenv("Tmp") ?: "-")
	private val unixLogDirs: Array<String> = arrayOf("/var/log", "${System.getenv("HOME") ?: "-"}/log", System.getenv("HOME") ?: "-", "log", "/tmp", "")
	private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") // see https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
	private fun nowTS() = dateFormat.format(System.currentTimeMillis()) ?: "now"
	private var logWriter: PrintWriter? = null
	private var isLogToFile: Boolean = false // false if logFileName is empty or logFileHandle could not be opened

	public actual var logDirs: List<String> = if (System.isWindows) windowsLogDirs.asList() else unixLogDirs.asList()
	public actual var logDir: String = "*" // try LogDirs
		get() = field
		set(value) {
			if (logWriter != null && field != value) {
				logWriter?.close()
				logWriter = null
			}
			field = value
		}
	public actual var logName: String = ""
		get() = field
		set(value) {
			isLogToFile = value.isNotEmpty()
			if (logWriter != null && field != value) {
				logWriter?.close()
				logWriter = null
			}
			field = value
		}
	public actual var maxLogSize: Long = 10 * 1024 * 1024
	public actual var maxLogVersion: Int = 9

	public actual var countWarning: Int = 0
	public actual var countError: Int = 0
	public actual var countFatal: Int = 0
	public actual var isLogToStdout: Boolean = false
	public actual var isStdWithTimestamp: Boolean = true
	public actual var isQuiet: Boolean = false

	private fun openLogFile(path: String, msgTS: String): PrintWriter? {
		//logMessage('D', "openLogFile(path:$path) logDir=$logDir")
		if (logDir != "*") {
			try {
				return PrintWriter(FileOutputStream(KmFile(path), true), false)
			} catch (e: Exception) {
				System.err.print("$msgTS E|logMessage(): cannot open log file $path(logDir=\"$logDir\")\n")
			}
			return null
		}

		logDirs.forEach { dirName ->
			if (dirName != "-") {
				logDir = dirName
				//System.err.print("$msgTS D|logMessage(): try to write to \"$logName\" in dir \"$logDir\"\n")
				try {
					return PrintWriter(FileOutputStream(KmFile(logPath), true), false)
				} catch (e: Exception) {
					// ignore
				}
			}
		}

		System.err.print("$msgTS E|logMessage(): cannot open log file $logName in any dir: $logDirs\n")
		return null
	}

	public actual operator fun invoke(ts: String?, id: String?, msgId: Char, msgSep: Char, msg: String): Boolean {
		var useMsgId = msgId
		when (msgId) {
			' ' -> {
				if (msg.isEmpty()) {
					logWriter?.println()
					if (!isQuiet) System.err.println("")
					return true
				}
				useMsgId = 'I'
			}

			'W' -> ++countWarning
			'E' -> ++countError

			'F' -> {
				++countFatal;
				++countError
			}
		}
		if (isQuiet && !isLogToFile) return true  // exit early if no output wanted

		val msgTS = ts ?: if (isStdWithTimestamp) nowTS() else ""

		if (isLogToFile && logWriter == null) {
			var path = logPath
			if (path.isNotEmpty()) {
				isLogToFile = false
				logWriter = openLogFile(path, msgTS)
				if (logWriter != null) {
					isLogToFile = true
					path = logPath // dir location may have changed (chosen from logDirs)

					var logFile = KmFile(path)
					if (logFile.length() > maxLogSize) {
						logWriter?.close()
						logFile.rotateRename(maxLogVersion)
						logWriter = openLogFile(path, msgTS)
					}

					if (!isQuiet) "${if (isStdWithTimestamp) "$msgTS I|" else ""}log to $path logDir=$logDir".let { m -> if (isLogToStdout) System.out.println(m) else System.err.println(m) }

				}
			}
		}

		val idStr = if (id.isNullOrEmpty()) "" else "$id|"
		val msgBuf = StringBuilder(msg.length + 128)
		for ((i, line) in msg.splitToSequence("\n").withIndex()) {
			val sep = if (i == 0) msgSep else '+'
			msgBuf.append(msgTS, ' ', useMsgId, sep, idStr, line, '\n')
		}

		val m = msgBuf.toString()
		if (!isQuiet) {
			if (isStdWithTimestamp) {
				if (isLogToStdout) System.out.print (m) else System.err.print(m)
			} else {
				if (isLogToStdout) System.out.println (msg) else System.err.println(msg)
			}
		}
		logWriter?.run {
			print(m)
			flush()
		}

		return true
	}

	public actual operator fun invoke(id: String?, msgId: Char, vararg msgs: String): Boolean {

		if (isQuiet && !isLogToFile) return true  // exit early if no output wanted
		val msgTS = if (isStdWithTimestamp) nowTS() else ""

		val msg = msgs.joinToString("")

		return invoke(msgTS, id, msgId, '|', msg)

	}

	public actual operator fun invoke(msgId: Char, vararg msgs: String): Boolean = invoke(null, msgId, *msgs)

}

// also handle prefix: logMessageNested('D', "cmd> 2022-03-30 17:51:00 I|getState Prod")
private val tsRe = "^(.*?)(20\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d)(\\.\\d*)?\\s([A-Za-z])([\\|+])(.*)".toRegex(option = RegexOption.DOT_MATCHES_ALL)

/**
 * try to extract a timestamp severity and separator from msg and use these to log the message
 */
public actual fun logMessageNestedLine(id: String?, msgId: Char, msg: String): Boolean {
	tsRe.matchEntire(msg)?.let { matchResult ->
		val (prefix, orgTsWhole, orgTsFrac, orgMsgId, orgSep, orgMsg) = matchResult.destructured
		val orgTs = orgTsWhole + if (orgTsFrac.isEmpty()) ".000" else orgTsFrac.padEnd(4, '0').take(4)
		return logMessage(orgTs, id, orgMsgId.firstOrNull()?:'I', orgSep.firstOrNull()?:'|', "$prefix$orgMsg")
	}
	return logMessage(id, msgId, msg)
}
