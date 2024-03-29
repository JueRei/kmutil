/*
 * Copyright 2021-2023 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import de.rdvsb.kmapi.*
import kotlinx.coroutines.CoroutineScope

/**
 * run a binary app with args
 * quoted args are supported (exactly like shell argument quoting)
 * no scripts supported no arg globbing done
 * @return the called process exit value
 */
public actual fun runBinary(cmd: String, timeout: Duration): Int {
	TODO("Not yet implemented")
}

/**
 * run via shell so globbing and command grouping or chaining with semicolons is supported
 * stdin is inherited from current process
 * stderr and stdout are captured and a supplied callback function is called for each captured stdin/stderr line
 *
 * **Note**: the processLine callback is called concurrently from two different threads
 * @return the called process exit value
 */
public actual fun system(cmd: String, timeout: Duration, processLine: (line: String, lineFrom: LineFrom, process: KmProcess) -> Unit): Int {
	TODO("Not yet implemented")
}

/**
 * pipe data through a shell process so globbing and command grouping or chaining with semicolons is supported
 * stdin of called shell is connected to returned OutputStream
 * stderr and stdout are captured and a supplied callback function is called for each captured stdin/stderr line
 *
 * **Note**: the processLine callback is called concurrently from two different threads
 * @return an OutputStream which is connected to stdin of called shell
 */
public actual suspend fun CoroutineScope.pipeSystem(
	cmd: String,
	timeout: Duration,
	processLine: (line: String, lineFrom: LineFrom, process: KmProcess) -> Unit,
): OutputStream {
	TODO("Not yet implemented")
}

/**
 * run via shell so globbing and command grouping or chaining with semicolons is supported
 * stdin is inherited from current process
 * stderr and stdout are written to current handles
 * @return the called process exit value
 */
public actual fun system(cmd: String, timeout: Duration): Int {
	TODO("Not yet implemented")
}

public actual class GlobalLock actual constructor(private val lockName: String, private val doLock: Boolean) : Closeable {

	public actual fun isLocked(): Boolean {
		TODO("Not yet implemented")
	}

	public actual fun lock(): GlobalLock {
		TODO("Not yet implemented")
	}

	public actual fun tryLock(): GlobalLock? {
		TODO("Not yet implemented")
	}

	public actual fun unlock(): GlobalLock {
		TODO("Not yet implemented")
	}

	public actual override fun close() {
		TODO("Not yet implemented")
	}
}
