/*
 * Copyright 2021 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import kotlin.native.concurrent.AtomicInt
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.AtomicLong
import kotlin.native.concurrent.freeze

import de.rdvsb.kmapi.*

// some platform dependend Posix functions
public const val InvalidHandle: Int = -1
public const val Stdout: Int = 1
public const val Stderr: Int = 2
public expect fun openFile(pathName: String): Int
public expect fun closeFile(fileHandle: Int): Int
public expect fun printFile(fileHandle: Int, text: String): Long
public expect fun getTimeMillis(): Long
public expect fun logDateFormatted(millis: Long): String

public expect val tzName: String

/**
 * log messages to log file
 * @return true
 */
public actual object logMessage {
	private val windowsLogDirs: Array<String> = arrayOf("C:\\Local\\Logs", "C:\\Local\\Log", "log", "", System.getenv("Temp")?:"-", System.getenv("Tmp")?:"-")
	private val unixLogDirs: Array<String> = arrayOf("/var/log", "${System.getenv("HOME")?:"-"}/log", System.getenv("HOME")?:"-", "log", "/tmp", "")
	private val logFileHandle = AtomicInt(InvalidHandle)
	private val isLogToFile = AtomicInt(0) // false if logFileName is empty or logFileHandle could not be opened

	private val _logDirs = AtomicReference((if (System.isWindows) windowsLogDirs.asList() else unixLogDirs.asList()).freeze()) // cannot use backing filed (requested assignment in setter will fail since object is frozen)
	public actual var logDirs: List<String>
		get() = _logDirs.value
		set(value) {
			_logDirs.value = value
		}

	private val _logDir = AtomicReference("*") // cannot use backing filed (requested assignment in setter will fail since object is frozen)
	public actual var logDir: String
		get() = _logDir.value
		set(value) {
			if (isLogToFile.value != InvalidHandle && _logDir.value != value) {
				closeFile(logFileHandle.value)
				logFileHandle.value = InvalidHandle
			}
			_logDir.value = value
		}

	private val _logFileName = AtomicReference("/tmp/app.log") // cannot use backing filed (requested assignment in setter will fail since object is frozen)
	public actual var logName: String
		get() = _logFileName.value
		set(value) {
			isLogToFile.value = if (value.isNotEmpty()) 1 else 0
			if (isLogToFile.value != InvalidHandle && _logFileName.value != value) {
				closeFile(logFileHandle.value)
				logFileHandle.value = InvalidHandle
			}
			_logFileName.value = value
		}

	private val _maxLogSize = AtomicLong(10*1024*1024L)
	public actual var maxLogSize: Long
		get() = _maxLogSize.value
		set(value) {
			_maxLogSize.value = value
		}

	private val _maxLogVersion = AtomicInt(9)
	public actual var maxLogVersion: Int
		get() = _maxLogVersion.value
		set(value) {
			_maxLogVersion.value = value
		}

	private val _countWarning = AtomicInt(0)
	public actual var countWarning: Int
		get() = _countWarning.value
		set(value) {
			_countWarning.value = value
		}

	private val _countError = AtomicInt(0)
	public actual var countError: Int
		get() = _countError.value
		set(value) {
			_countError.value = value
		}

	private val _countFatal = AtomicInt(0)
	public actual var countFatal: Int
		get() = _countFatal.value
		set(value) {
			_countFatal.value = value
		}

	private val _isLogToStdout = AtomicInt(0)
	public actual var isLogToStdout: Boolean
		get() = _isLogToStdout.value != 0
		set(value) {
			_isLogToStdout.value = if (value) 1 else 0
		}

	private val _isQuiet = AtomicInt(0)
	public actual var isQuiet: Boolean
		get() = _isQuiet.value != 0
		set(value) {
			_isQuiet.value = if (value) 1 else 0
		}

	private fun openLogFile(path: String, msgTS: String): Int {
		if (logDir != "*") {
			val fh = openFile(path)
			if (fh == InvalidHandle) printFile(Stderr, "$msgTS E|logMessage(): cannot open log file $path\n")
			return fh
		}

		logDirs.forEach { dirName ->
			if (dirName != "-") {
				logDir = dirName
				val fh = openFile(logPath)
				if (fh != InvalidHandle) return fh
			}
		}

		printFile(Stderr, "$msgTS E|logMessage(): cannot open log file $logName  in any dir: $logDirs\n")
		return InvalidHandle
	}

	public actual operator fun invoke(id: String?, msgId: Char, vararg msgs: String): Boolean {
		var logFh = logFileHandle.value
		var isLogToFile: Boolean = (isLogToFile.value != 0)
		var useMsgId = msgId

		when (msgId) {
			' ' -> {
				if (msgs.isEmpty()) {
					if (!isQuiet) printFile(Stderr, "")
					if (logFh != InvalidHandle) printFile(logFh, "")
					return true
				}
				useMsgId = 'I'
			}

			'W' -> _countWarning.increment()
			'E' -> _countError.increment()
			'F' -> {
				_countError.increment()
				_countFatal.increment()
			}
		}

		if (isQuiet && !isLogToFile) return true  // exit early if no output wanted

		val nowMillis = getTimeMillis()
		val msgTS = logDateFormatted(nowMillis)

		if (isLogToFile && logFh == InvalidHandle) {
			var path = logPath
			if (path.isNotEmpty()) {
				isLogToFile = false
				this.isLogToFile.value = 0
				logFh = openLogFile(path, msgTS)
				if (logFh != InvalidHandle) {
					path = logPath // dir location may have changed (chosen from logDirs)
					var logFile = File(path)
					if (logFile.length() > maxLogSize) {
						closeFile(logFh)
						logFile.rotateRename(maxLogVersion)
						logFh = openFile(path)
					}
				}
				if (logFh != InvalidHandle) {
					logFileHandle.value = logFh
					isLogToFile = true
					this.isLogToFile.value = 1
					if (!isQuiet) printFile(Stderr,"$msgTS I|log to $logPath\n")
					if (!isQuiet) "$msgTS I|log to $path logDir=$logDir\n".let { m -> if (isLogToStdout) printFile(Stdout, m) else printFile(Stderr, m) }

					printFile(logFh, "\n$msgTS =|TZ=$tzName\n$msgTS =|ENCODING=UTF-8\n")
				}
			}
		}

		val idStr = if (id.isNullOrEmpty()) "" else "$id|"
		val msg = msgs.joinToString("")
		val msgBuf = StringBuilder(msg.length + 128)
		for ((i, line) in msg.splitToSequence("\n").withIndex()) {
			val sep = if (i == 0) '|' else '+'
			msgBuf.append(msgTS, ' ',  useMsgId, sep, idStr, line, '\n')
		}

		val m = msgBuf.toString()
		if (!isQuiet) if (isLogToStdout) printFile(Stdout, m) else printFile(Stderr, m)
		if (isLogToFile) printFile(logFh, m)

		return true;
	}

	public actual operator fun invoke(msgId: Char, vararg msgs: String): Boolean = invoke(null, msgId, *msgs)
}