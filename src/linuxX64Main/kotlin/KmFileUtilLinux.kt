package de.rdvsb.kmutil

import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/** mount a dirName already defined in /etc/fstab
 *
 * check if it is already mounted if not unmount it at end of script via onExitRunCmds list
 * @return mountStatus: Boolean
 */
@OptIn(ExperimentalTime::class)
public actual fun mountDir(dirName: String): Boolean {
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
