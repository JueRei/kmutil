/*
 * Copyright 2021 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import java.text.NumberFormat
import java.util.*

// enforce en_US locale. Always use consistent decimal point and grouping chars for system logs
private val locale = Locale.setDefault(Locale("en", "US")).run { Locale.getDefault() }


public actual val kmStringFmtGroupingFlag: Char = ','
public actual fun <T> String.sprintf(t: T): String = this.format(t)

