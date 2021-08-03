
/*
 * Copyright 2021 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import kotlin.test.Test
import kotlin.test.assertTrue
import de.rdvsb.kmapi.*
import kotlinx.datetime.*

internal class DateTimeCommonTest {
	@Test
	fun dateTime() {
		println("DateTimeCommonTest.dateTime start  ticAsDateTime=${ticAsDateTime()}  ticAsDateTimeUtc=${ticAsDateTimeUtc()}")

		val dateTime = "2021-05-12 00:00:00"
		val tUtc = dateTime.parseUtcDateTime()
		println ("$dateTime.parseUtcDateTime   => tUtc=  $tUtc toDateTime=${tUtc.toDateTime()} toUtcDateTime=${tUtc.toUtcDateTime()} toLocalDateTime(UTC)=${tUtc.toLocalDateTime(TimeZone.UTC)}")

		val tLocal = dateTime.parseLocalDateTime()
		println ("$dateTime.parseLocalDateTime => tLocal=$tLocal toDateTime=${tLocal.toDateTime()} toUtcDateTime=${tLocal.toUtcDateTime()} toLocalDateTime(UTC)=${tLocal.toLocalDateTime(TimeZone.UTC)}")

		val currentMoment: Instant = Clock.System.now()
		val datetimeInUtc: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.UTC)
		val datetimeInSystemZone: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
		println("currentMoment=$currentMoment., datetimeInUtc=$datetimeInUtc, datetimeInSystemZone=$datetimeInSystemZone")

//		println("  File: name=${tstFile.name} path=${tstFile.path} absolutePath=${tstFile.absolutePath}  canonicalPath=${tstFile.canonicalPath}")
//		assertTrue(tstFile.name.startsWith("x.x"))
//		assertTrue(tstFile.path.startsWith("..${File.separatorChar}"))

		println("DateTimeCommonTest.dateTime end")
	}

}
