package de.rdvsb.kmutil

import de.rdvsb.kmapi.System
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

//public expect fun ticAsDateTime(millis: Long? = null): String
//public expect fun Long.ticAsDateTime(): String
public fun ticAsDateTime(millis: Long? = null): String =Instant.fromEpochMilliseconds(millis ?: System.currentTimeMillis()).toLocalDateTime(TimeZone.currentSystemDefault()).toString().replace('T', ' ')
public fun Long.ticAsDateTime(): String = ticAsDateTime(this)
public fun ticAsDateTimeUtc(millis: Long? = null): String = Instant.fromEpochMilliseconds(millis ?: System.currentTimeMillis()).toLocalDateTime(TimeZone.UTC).toString().replace('T', ' ')
public fun Long.ticAsDateTimeUtc(): String = ticAsDateTimeUtc(this)

public fun String.parseUtcDateTime(): Instant = replace(' ', 'T').toLocalDateTime().toInstant(TimeZone.UTC)
public fun String.parseLocalDateTime(): Instant = replace(' ', 'T').toLocalDateTime().toInstant(TimeZone.currentSystemDefault())
public fun Instant.toDateTime(): String = toLocalDateTime(TimeZone.currentSystemDefault()).let { it.toString().replace('T', ' ') + if (it.second == 0) ":00" else "" }
public fun Instant.toUtcDateTime(): String = toLocalDateTime(TimeZone.UTC).let { it.toString().replace('T', ' ') + if (it.second == 0) ":00" else "" }

