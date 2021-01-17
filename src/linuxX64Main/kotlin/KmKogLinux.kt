package de.rdvsb.kmutil

import kotlinx.cinterop.*
import platform.posix.LC_ALL
import platform.posix.setlocale
import platform.posix.*

private val setLocale = setlocale(LC_ALL, "en_US.UTF-8"); // allow thousands separators
private val tz = tzset()



// some C string util functions which share a common per thread C char buffer
private const val cstrBufLen = 2048

//@ThreadLocal // one C wchar string buffer for each thread
//var wcstrBuf = nativeHeap.allocArray<wchar_tVar>(wcstrBufLen)  // will create memory leaks when starting/stopping multiple threads

@ThreadLocal // one C variable for each thread
private val wcstrBuf = nativeHeap.allocArray<wchar_tVar>(cstrBufLen)

@ThreadLocal // one C variable for each thread
private val cstrBuf = nativeHeap.allocArray<ByteVar>(cstrBufLen)

public actual fun openFile(pathName: String): Int {
	val fh = open(pathName, O_WRONLY or O_APPEND or O_CREAT, S_IRUSR or S_IWUSR or S_IRGRP or S_IWGRP)
	if (fh < 0) perror("openFile cannot open \"$pathName\"")
	return fh
}

public actual fun closeFile(fileHandle: Int): Int {
	if (fileHandle >= 0) return close(fileHandle)
	return -1
}

public actual fun printFile(fileHandle: Int, text: String): Long {
	return write(fileHandle, text.cstr, text.length.toULong())
}

public actual  fun getTimeMillis(): Long = time(null) * 1000L // getTimeMillis()

@ThreadLocal // one C variable for each thread
private val time_tVar = nativeHeap.alloc<platform.posix.time_tVar>()

@ThreadLocal // one C struct or each thread
private val tmVar = nativeHeap.alloc<platform.posix.tm>()
//* return a string containing time in format "YYYY-MM-DD HH:MM:SS"
public actual  fun logDateFormatted(millis: Long): String {
	time_tVar.value = millis / 1000L
	platform.posix.localtime_r(time_tVar.ptr, tmVar.ptr)
	snprintf(cstrBuf, cstrBufLen.toULong(), "%04d-%02d-%02d %02d:%02d:%02d", tmVar.tm_year + 1900, tmVar.tm_mon + 1, tmVar.tm_mday, tmVar.tm_hour, tmVar.tm_min, tmVar.tm_sec)
	return cstrBuf.toKString()
}

private var tzNameBuf: String = ""
public actual val tzName: String
	get() {
		if (tzNameBuf.isEmpty()) {
			time_tVar.value = time(null) / 1000L
			platform.posix.localtime_r(time_tVar.ptr, tmVar.ptr)
			strftime(cstrBuf, cstrBufLen.toULong(), "%Z%z", tmVar.ptr)
			tzNameBuf = cstrBuf.toKString()
		}
		return tzNameBuf
	}

