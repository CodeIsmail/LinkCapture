package dev.codeismail.linkcapture.utils

fun Int.toMilliSecs(): Long{
    return (this * (60 * 60) * 1000).toLong()
}