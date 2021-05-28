package de.rdvsb.kmutil

import de.rdvsb.kmapi.System
import java.text.SimpleDateFormat

private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // see https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html

public actual fun ticAsDateTime(millis: Long?): String = dateFormat.format(millis ?: System.currentTimeMillis())?:"now"

public actual fun Long.ticAsDateTime(): String = ticAsDateTime(this)
