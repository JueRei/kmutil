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
public expect fun mountDir(dirName: String, userName: String = "", password: String = ""): Boolean
