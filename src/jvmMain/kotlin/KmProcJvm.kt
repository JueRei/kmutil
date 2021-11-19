/*
 * Copyright 2021 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import java.io.InputStream
import java.util.concurrent.TimeUnit
import java.io.FileOutputStream
import java.nio.channels.FileLock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.DurationUnit
import de.rdvsb.kmapi.*

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * run a binary app with args
 * quoted args are supported (exactly like shell argument quoting)
 * no scripts supported no arg globbing done
 * @return the called process exit value
 */
public actual fun runBinary(cmd: String, timeout: kotlin.time.Duration): Int {
	logMessage('I', "run: $cmd")

	val processBuilder = ProcessBuilder(cmd.splitShellArgs())
	processBuilder.inheritIO() // stdout and stderr of called process goes to my stdout/stderr

	val process = processBuilder.start()

	if (timeout.isFinite()) {
		process.waitFor(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)
	} else {
		process.waitFor()
	}

	if (process.isAlive) process.destroyForcibly()
	return process.exitValue()

}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// run this in IO dispatcher context:
private suspend fun InputStream.processLineSuspending(process: KmProcess, readFrom: LineFrom, processLine: (line: String, lineFrom: LineFrom, process: KmProcess) -> Unit) {

	for (line in bufferedReader().lines()) {
		if (line == null) return
		processLine(line, readFrom, process)
	}
}

/**
 * Unix: run via shell so globbing and command grouping or chaining with semicolons is supported
 * Windows run via cmd.exe so globbing and command grouping or chaining with semicolons is **NOT** supported
 * stdin is inherited from current process
 * stderr and stdout are captured and a supplied callback function is called for each captured stdin/stderr line
 *
 * **Note**: the processLine callback is called concurrently from two different threads
 * @return the called process exit value
 */
public actual fun system(cmd: String, timeout: Duration, processLine: (line: String, lineFrom: LineFrom, process: KmProcess) -> Unit): Int {

	logMessage('I', "system: $cmd")

	val processBuilder = if (de.rdvsb.kmapi.System.isWindows) {
		ProcessBuilder("cmd.exe", "/c", "($cmd & exit)")
	} else {
		ProcessBuilder("sh", "-c", cmd)
	}

	processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT)

	val process = KmProcess(processBuilder.start())

	runBlocking {  // this expression blocks the main thread
		val awaits = listOf(
			//Exception in thread "main" java.lang.ClassCastException: java.lang.UNIXProcess cannot be cast to de.rdvsb.kmapi.Process
			//        at de.rdvsb.kmutil.KmProcJvmKt$system$1$awaits$1.invokeSuspend(KmProcJvm.kt:73)
			//
			async(Dispatchers.IO) { process.errorStream.processLineSuspending(process, LineFrom.ERR, processLine) },
			async(Dispatchers.IO) { process.inputStream.processLineSuspending(process, LineFrom.OUT, processLine) }
		)

		if (timeout.isFinite()) {
			process.waitFor(timeout.inWholeMilliseconds, DurationUnit.MILLISECONDS)
		} else {
			process.waitFor()
		}

		if (process.isAlive) process.destroyForcibly()
		awaits.awaitAll()
	}

	return process.exitValue()
}


/**
 * run via shell so globbing and command grouping or chaining with semicolons is supported
 * stdin is inherited from current process
 * stderr and stdout are written to current handles
 * @return the called process exit value
 */
public actual fun system(cmd: String, timeout: kotlin.time.Duration): Int {

	logMessage('I', "system: $cmd")

	val processBuilder = ProcessBuilder("sh", "-c", cmd)
	processBuilder.inheritIO()

	val process = processBuilder.start()

	if (timeout.isFinite()) {
		process.waitFor(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)
	} else {
		process.waitFor()
	}

	if (process.isAlive) process.destroyForcibly()
	return process.exitValue()

}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/** create a system wide named mutex (emulated by an exclusive lock on a file named lockName)
 *
 */
public actual class GlobalLock actual constructor(private val lockName: String, private val doLock: Boolean): Closeable {
	private val fileOutputStream  = FileOutputStream(lockName)
	private var fileLock: FileLock? = null

	init {
		if (doLock) lock()
	}

	public actual fun isLocked(): Boolean = fileLock != null

	public actual fun lock():GlobalLock {
		if (fileLock == null) {
			fileLock = fileOutputStream.channel.lock()
		}
		return this
	}

	public actual fun tryLock():GlobalLock? {
		if (fileLock == null) {
			fileLock = fileOutputStream.channel.tryLock()
		}
		return if (fileLock == null) null else this
	}

	public actual fun unlock(): GlobalLock {
		fileLock?.release()
		return this
	}

	public actual override fun close(): Unit {
		unlock()
	}
}