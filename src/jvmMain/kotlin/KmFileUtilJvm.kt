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
public actual fun mountDir(dirName: String, userName: String, password: String): Boolean {
	System.getProperty("os.name").let {
		if (it == null || !it.contains("Linux", true) && !it.contains("Unix", true) && !it.contains("BSD", true)) mountDir@return false
	}
	var ok = false
	system("mount | grep \"on $dirName \"", 5.toDuration(DurationUnit.SECONDS)) { line, lineFrom, _ ->
		val msgId = if (lineFrom == LineFrom.OUT) 'I' else 'E'
		logMessage(msgId, "mountDir1> $line")
		if (line.contains("on $dirName ")) ok = true
	}
	if (ok) return true // dirName is already mounted

	ok = false
	system("mount $dirName; sleep 1; mount | grep \"on $dirName \"", 300.toDuration(DurationUnit.SECONDS)) { line, lineFrom, _ ->
		val msgId = if (lineFrom == LineFrom.OUT) 'I' else 'E'
		logMessage(msgId, "mountDir2> $line")
		if (line.contains("on $dirName ")) ok = true
	}
	if (ok) onExitRunCmds.add("umount $dirName")

	return ok
}
