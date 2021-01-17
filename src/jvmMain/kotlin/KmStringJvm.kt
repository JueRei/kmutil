package de.rdvsb.kmutil

public actual fun <T> String.sprintf(t: T): String = this.format(t)
public actual val kmStringFmtGroupingFlag: Char = ','
