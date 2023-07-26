/*
 * Copyright 2021-2023 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
@file:OptIn(ExperimentalForeignApi::class)

package de.rdvsb.kmutil

import kotlinx.cinterop.*
import platform.posix.LC_ALL
import platform.posix.setlocale
import platform.posix.*

private val setLocale = setlocale(LC_ALL, "en_US.UTF-8"); // allow thousands separators

// some C string util functions which share a common per thread C char buffer
private const val cstrBufLen = 2048

//@ThreadLocal // one C wchar string buffer for each thread
//var wcstrBuf = nativeHeap.allocArray<wchar_tVar>(wcstrBufLen)  // will create memory leaks when starting/stopping multiple threads

@ThreadLocal // one C variable for each thread
private val wcstrBuf = nativeHeap.allocArray<wchar_tVar>(cstrBufLen)

@ThreadLocal // one C variable for each thread
private val cstrBuf = nativeHeap.allocArray<ByteVar>(cstrBufLen)

public actual fun <T> String.sprintf(t: T): String {
	// e.g. "%'8.2g".sprintf(2.2)
	val bufLen = cstrBufLen.toULong()
	when (t) {
		is Byte   -> snprintf(cstrBuf, bufLen, this, t.toByte(), 0, 0, 0)
		is UByte  -> snprintf(cstrBuf, bufLen, this, t.toUByte(), 0, 0, 0)
		is Short  -> snprintf(cstrBuf, bufLen, this, t.toShort(), 0, 0, 0)
		is UShort -> snprintf(cstrBuf, bufLen, this, t.toUShort(), 0, 0, 0)
		is Int    -> snprintf(cstrBuf, bufLen, this, t.toInt(), 0, 0, 0)
		is UInt   -> snprintf(cstrBuf, bufLen, this, t.toUInt(), 0, 0, 0)
		is Long   -> snprintf(cstrBuf, bufLen, this, t.toLong(), 0, 0, 0)
		is ULong  -> snprintf(cstrBuf, bufLen, this, t.toULong(), 0, 0, 0)
		is Float  -> snprintf(cstrBuf, bufLen, this, t.toFloat(), 0, 0, 0)
		is Double -> snprintf(cstrBuf, bufLen, this, t.toDouble(), 0, 0, 0)
		else      -> return t.toString()
	}
	return cstrBuf.toKString()
}

public actual val kmStringFmtGroupingFlag: Char = '\''

