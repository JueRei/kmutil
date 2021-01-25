package de.rdvsb.kmutil

import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration
import de.rdvsb.kmapi.*

/** mount a dirName already defined in /etc/fstab
 *
 * check if it is already mounted if not unmount it at end of script via onExitRunCmds list
 * @return mountStatus: Boolean
 */
@OptIn(ExperimentalTime::class)
public expect fun mountDir(dirName: String, userName: String = "", password: String = ""): Boolean

public fun File.rotateRename(lastOldVersion: Int = 9): Unit {
	var path = absolutePath
	var pathBase = path
	var ext = ""
	path.lastIndexOf('.').let {
		if (it >= 0) {
			pathBase = path.substring(0, it)
			ext = path.substring(it)
		}
	}

	for (n in lastOldVersion downTo 0) {
		val olderFile = File("$pathBase#${n.toFmt("%02d")}$ext")
		val newerFile = if (n == 0) File(path) else  File("$pathBase#${(n-1).toFmt("%02d")}$ext")

		olderFile.exists() && olderFile.delete()
		newerFile.exists() && newerFile.renameTo(olderFile)
	}
}