package de.rdvsb.kmutil

import java.time.LocalTime

//import de.rdvsb.kmapi.System
//import kotlinx.datetime.*
//import java.text.SimpleDateFormat

//private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // see https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html

//public actual fun ticAsDateTime(millis: Long?): String =Instant.fromEpochMilliseconds(millis ?: System.currentTimeMillis()).toLocalDateTime(TimeZone.currentSystemDefault()).toString()
//public fun ticAsDateTimeUtc(millis: Long?): String = Instant.fromEpochMilliseconds(millis ?: System.currentTimeMillis()).toLocalDateTime(TimeZone.UTC).toString()
//public actual fun Long.ticAsDateTime(): String = ticAsDateTime(this)



// 		val datetimeInUtc: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.UTC)
//		val datetimeInSystemZone: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
//		val instantInUtc = "2021-08-03T16:05:33.213".toLocalDateTime().toInstant(TimeZone.UTC)


public fun String.parseTime(): LocalTime = LocalTime.parse(this)
public fun String.parseTimeOrNull(): LocalTime? = try {
	parseTime()
} catch (e: Exception) {
	null
}
