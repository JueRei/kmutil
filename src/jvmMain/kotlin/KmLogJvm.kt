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
	private val windowsLogDirs: Array<String> = arrayOf("C:\\Local\\Logs", "C:\\Local\\Log", "log", "", System.getenv("Temp")?:"-", System.getenv("Tmp")?:"-")
	private val unixLogDirs: Array<String> = arrayOf("/var/log", "${System.getenv("HOME")?:"-"}/log", System.getenv("HOME")?:"-", "log", "/tmp", "")
	private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") // see https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
	private fun nowTS() = dateFormat.format(System.currentTimeMillis())?:"now"
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
	public actual var maxLogSize: Long = 10*1024*1024
	public actual var maxLogVersion: Int = 9

	public actual var countWarning: Int = 0
	public actual var countError: Int = 0
	public actual var countFatal: Int =0
	public actual var isQuiet: Boolean = false

	private fun openLogFile(path: String, msgTS: String): PrintWriter? {
		logMessage('D', "openLogFile(path:$path) logDir=$logDir")
		if (logDir != "*") {
			try {
				return PrintWriter(FileOutputStream(File(path), true), false)
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
					return PrintWriter(FileOutputStream(File(logPath), true), false)
				} catch (e: Exception) {
					// ignore
				}
			}
		}

		System.err.print("$msgTS E|logMessage(): cannot open log file $logName in any dir: $logDirs\n")
		return null
	}

	public actual operator fun invoke(msgId: Char, vararg msgs: String): Boolean {
		var useMsgId = msgId
		when (msgId) {
			' ' -> {
				if (msgs.isEmpty()) {
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
		val msgTS = nowTS()

		if (isLogToFile && logWriter == null) {
			var path = logPath
			if (path.isNotEmpty()) {
				isLogToFile = false
				logWriter = openLogFile(path, msgTS)
				if (logWriter != null) {
					isLogToFile = true
					path = logPath // dir location may have changed (chosen from logDirs)

					var logFile = File(path)
					if (logFile.length() > maxLogSize) {
						logWriter?.close()
						logFile.rotateRename(maxLogVersion)
						logWriter = openLogFile(path, msgTS)
					}

					if (!isQuiet) System.err.print("$msgTS I|log to $path logDir=$logDir\n")

				}
			}
		}

		val msg = msgs.joinToString("")
		val msgBuf = StringBuilder(msg.length + 128)
		for ((i, line) in msg.splitToSequence("\n").withIndex()) {
			val sep = if (i == 0) '|' else '+'
			msgBuf.append(msgTS, ' ',  useMsgId, sep, line, '\n')
		}

		val m = msgBuf.toString()
		if (!isQuiet) System.err.print(m)
		logWriter?.run {
			print(m)
			flush()
		}

		return true
	}
}