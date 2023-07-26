/*
 * Copyright 2021-2023 Jürgen Reichmann, Jettingen, Germany
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package de.rdvsb.kmutil

import de.rdvsb.kmapi.*


/**
 * Pads the collection to the specified size [minSize] at the end with the specified [padWith] value.
 *
 * @param minSize the desired minimum List size.
 * @param padWith the value to pad collection with, if it has size less than the [minSize] specified.
 * @return Returns a List of size at least [minSize] consisting of the content of `this` collection appended with [padWith] as many times
 * as is necessary to reach that size.
 *
 * e.g. ` "a=b".split("=", 2).padEnd(2, "")`
 */
public fun <T> Collection<T>.padEnd(minSize: Int, padWith: T): List<T> {
	if (this.size >= minSize) return this.toList()
	val result = ArrayList<T>(minSize)
	if (this.size > 0) result.addAll(this)
	repeat (minSize - result.size) { result.add(padWith) }
	return result
}

/**
 * Pads the collection to the specified size [minSize] at the beginning with the specified [padWith] value.
 *
 * @param minSize the desired minimum List size.
 * @param padWith the value to pad collection with, if it has size less than the [minSize] specified.
 * @return Returns a List of size at least [minSize] consisting of the content of `this` collection prepended with [padWith] as many times
 * as is necessary to reach that size.
 *
 * e.g. ` "01:02:12".split(":", 3).padStart(3, "0")`
 */
public fun <T> Collection<T>.padStart(minSize: Int, padWith: T): List<T> {
	if (this.size >= minSize) return this.toList()
	val result = ArrayList<T>(minSize)
	repeat (minSize - this.size) { result.add(padWith) }
	if (this.size > 0) result.addAll(this)
	return result
}

