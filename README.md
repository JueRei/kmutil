# Kotlin multi-platform utilities for scripts and command line utilities
Provide some useful classes and functions for Kotlin multiplatform utilities 
Based on de.rdvsb.kmapi

## Preliminary version 0.1.9
### parts working (using kmutil and kmapi):

| Module | Common | JVM | NativeLinux | NativeMingw |
| :---   | :---:  | :---: | :---: | :---: |
| Args | x | x | x | x |
| logMessage | x | x | x | x |
| File | x | x | x | x |
| InputStream | x | x | - | - |
| OutputStream | x | x | - | - |
| KmProcess | x | x | - | - |
| StringUtil | x | x | x | x |
| DateTime | x | x | x | x |

## Goals
 * Ability to write these utilities in a platform independent fashion
 * Facilitate the creation of script like utilities (e.g. alternative or replacement of Bash, Perl, Python scripts)
 * Develop using the JVM, deploy as native

## Non goals
 * Support mobile platforms
 * Support Web platforms

## Use for
 * Native
 * JVM
 * Server
 * Desktop

## Classes, singletons and extensions
 * BasicGetArgs
 * logMessage
 * String extensions 

## Note
 * This is a work in progress. Functionality will be added when needed
 * Prefer functionality of the Kotlin standard library, as long as it is implemented for the JVM and Native target

## Licence
Apache 2.0
