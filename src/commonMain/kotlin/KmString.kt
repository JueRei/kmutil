/*
 * Copyright 2021-2023 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import kotlin.math.floor
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.ulp

public expect val kmStringFmtGroupingFlag: Char // JVM expects ",", native "'" for thousands sep

public expect fun <T> String.sprintf(t: T): String

public fun Byte.toFmt(fmt: String): String = fmt.sprintf(this)
public fun UByte.toFmt(fmt: String): String = fmt.sprintf(this)
public fun Short.toFmt(fmt: String): String = fmt.sprintf(this)
public fun UShort.toFmt(fmt: String): String = fmt.sprintf(this)
public fun Int.toFmt(fmt: String): String = fmt.sprintf(this)
public fun UInt.toFmt(fmt: String): String = fmt.sprintf(this)
public fun Long.toFmt(fmt: String): String = fmt.sprintf(this)
public fun ULong.toFmt(fmt: String): String = fmt.sprintf(this)
public fun Float.toFmt(fmt: String): String = fmt.sprintf(this)
public fun Double.toFmt(fmt: String): String = fmt.sprintf(this)

private val metricIndicatorsGe1 = charArrayOf(' ', 'K', 'M', 'G', 'T', 'P', 'E', 'Z', 'Y')
private val metricIndicatorsLt1 = charArrayOf('m', 'µ', 'n', 'p', 'f', 'a', 'z', 'y')
public fun <T> T.fmtMetric(unit: String, fWidth: Int = 0, prec: Int = 1, metricPrefix: String = " "): String {
	// format an number in a reasonable metric e.g. 1_234_456.fmtMetric("B") => "1.234 MB"
	// fWidth is only applied to the resulting number. The result is at least length(metricPrefix)+1 longer than fWidth
	var d: Double

	when (this) {
		is Int    -> d = this.toDouble()
		is Long   -> d = this.toDouble()
		is Float  -> d = this.toDouble()
		is Double -> d = this
		is Byte   -> d = this.toDouble()
		is Short  -> d = this.toDouble()
		is UInt   -> d = this.toLong().toDouble()

		is ULong  -> {
			if (this > Long.MAX_VALUE.toUInt()) {
				d = ((this - 1UL - Long.MAX_VALUE.toULong()).toLong()).toDouble() + Long.MAX_VALUE.toDouble() + 1.0
			} else {
				d = this.toLong().toDouble()
			}
		}

		is UByte  -> d = this.toInt().toDouble()
		is UShort -> d = this.toInt().toDouble()
		else      -> return this.toString()
	}
	// 1000Z000E000T000G000M000K000
	//
	// 0018Z446E744T073G709M551K615

	val isValNegative = if (d < 0.0) {
		d = -d;
		true
	} else {
		false
	}

	var metric = ' '

	when {
		d == 0.0 -> {}
		d < 1.0 -> {
			val ld = floor( log(d, 10.0))
			var engExpIx: Int = -(ld.toInt()+1) / 3 // compute engineering exponent index 0.0001 => 1
			//println("fmtMetric d=$d, ld=$ld, engExpIx=$engExpIx")
			if (engExpIx >= metricIndicatorsLt1.size) engExpIx = metricIndicatorsLt1.size - 1
			metric = metricIndicatorsLt1[engExpIx]
			d *= 10.0.pow((engExpIx+1)*3)
		}
		else -> {
			val ld = log(d, 10.0)
			val ldf = floor(ld + ld.ulp) // e.g. for d=1000.0, ld is 2.9999999999999996 and ldf would result in 2 but must be 3 here. Solution: add ld.udp and we get 3.0
			var engExpIx: Int = ldf.toInt() / 3 // compute engineering exponent index 1_000 => 1
			//println("\nfmtMetric d=$d, ld=$ld, ld+ld.ulp=${ld + ld.ulp}, ldf=$ldf, engExpIx=$engExpIx")
			if (engExpIx >= metricIndicatorsGe1.size) engExpIx = metricIndicatorsGe1.size - 1
			metric = metricIndicatorsGe1[engExpIx]
			d /= 10.0.pow(engExpIx*3)
		}
	}

	if (isValNegative) d = -d

	var fmt = StringBuilder("%")
	fmt.append (kmStringFmtGroupingFlag) // , or '
	if (fWidth != 0) fmt.append(fWidth)
	fmt.append('.'); fmt.append(prec)
	fmt.append('f')
	fmt.append(metricPrefix)
	if (metric != ' ') fmt.append(metric)
	fmt.append(unit)
	return fmt.toString().sprintf(d)
}

/**
 * Returns an abbreviated version of the String, of the specified [maxLen] and with [appendIfLonger] appended if the current String is longer than the specified [maxLen]
 * otherwise, returns the original String without [appendIfLonger].
 * The returned string will never be longer than [maxLen]
 *
 * @param maxLen maximum length of returned string.
 * @param appendIfLonger append this if string is longer.
 *
 */
public fun String.abbreviate(maxLen: Int, appendIfLonger: String = "..."): String {
	if (length <= maxLen) return this
	if (appendIfLonger.length >= maxLen) return appendIfLonger.substring(0, maxLen)
	return substring(0, maxLen - appendIfLonger.length) + appendIfLonger
}
