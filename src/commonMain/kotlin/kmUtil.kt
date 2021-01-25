package de.rdvsb.kmutil

import de.rdvsb.kmapi.*


public fun File.rotateRename(lastOldVersion: Int = 9): Unit {
	var path = absolutePath
	for (n in lastOldVersion downTo 0) {
		val olderFile = File("${path}.${n.toFmt("%02d")}")
		val newerFile = if (n == 0) File(path) else  File("${path}.${(n-1).toFmt("%02d")}")
		olderFile.delete()
		newerFile.renameTo(olderFile)
	}
}