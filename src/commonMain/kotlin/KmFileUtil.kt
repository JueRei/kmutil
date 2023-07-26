/*
 * Copyright 2021-2023 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

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
public expect fun mountDir(dirName: String, userName: String = "", password: String = ""): Boolean

public fun KmFile.rotateRename(lastOldVersion: Int = 9): Unit {
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
		val olderFile = KmFile("$pathBase#${n.toFmt("%02d")}$ext")
		val newerFile = if (n == 0) KmFile(path) else  KmFile("$pathBase#${(n-1).toFmt("%02d")}$ext")

		olderFile.exists() && olderFile.delete()
		newerFile.exists() && newerFile.renameTo(olderFile)
	}
}