package de.rdvsb.kmutil

import de.rdvsb.kmapi.System
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.sign
import kotlin.time.Duration

//public expect fun ticAsDateTime(millis: Long? = null): String
//public expect fun Long.ticAsDateTime(): String
public fun ticAsDateTime(millis: Long? = null): String =Instant.fromEpochMilliseconds(millis ?: System.currentTimeMillis()).toLocalDateTime(TimeZone.currentSystemDefault()).toString().replace('T', ' ')
public fun Long.ticAsDateTime(): String = ticAsDateTime(this)
public fun ticAsDateTimeUtc(millis: Long? = null): String = Instant.fromEpochMilliseconds(millis ?: System.currentTimeMillis()).toLocalDateTime(TimeZone.UTC).toString().replace('T', ' ')
public fun Long.ticAsDateTimeUtc(): String = ticAsDateTimeUtc(this)

public fun String.parseUtcDateTime(): Instant = replace(' ', 'T').toLocalDateTime().toInstant(TimeZone.UTC)
public fun String.parseUtcDateTimeOrNull(): Instant? = try {
	parseUtcDateTime()
} catch (e: Exception) {
	null
}

public fun String.parseLocalDateTime(): Instant = replace(' ', 'T').toLocalDateTime().toInstant(TimeZone.currentSystemDefault())
public fun String.parseLocalDateTimeOrNull(): Instant? = try {
	parseLocalDateTime()
} catch (e: Exception) {
	null
}
public fun Instant.toDateTime(): String = toLocalDateTime(TimeZone.currentSystemDefault()).let { it.toString().replace('T', ' ') + if (it.second == 0) ":00" else "" }
public fun Instant.toUtcDateTime(): String = toLocalDateTime(TimeZone.UTC).let { it.toString().replace('T', ' ') + if (it.second == 0) ":00" else "" }

//TODO:
// add common LocalTime and implementation for native
//public fun String.parseTime(): LocalTime = LocalTime.parse(this)
//public fun String.parseTimeOrNull(): LocalTime? = try {
//	parseTime()
//} catch (e: Exception) {
//	null
//}

/**
 * Returns a string representation of this duration value
 * expressed as a combination of numeric components, each in its own unit
 * like [Duration.toString()]
 *
 * @param maxDecimals max number of decimal digits shown for fractional part
 */
public fun Duration.toString(maxDecimals: Int): String =
	this.toString().run {
		val ix = indexOf('.')
		val countDecimals = substring(ix + 1).count { it.isDigit() }
		if (ix > 0 && countDecimals > maxDecimals) {
			take(ix + maxDecimals + maxDecimals.sign) + substring(ix + countDecimals + 1)
		} else {
			this
		}
	}