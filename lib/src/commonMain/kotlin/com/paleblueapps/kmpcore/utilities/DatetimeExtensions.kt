package com.paleblueapps.kmpcore.utilities

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun LocalDate.Companion.now(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

@OptIn(ExperimentalTime::class)
fun LocalDateTime.Companion.now(): LocalDateTime =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

@OptIn(ExperimentalTime::class)
fun LocalDateTime.Companion.fromEpochMilliseconds(epochMillis: Long): LocalDateTime =
    Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())

@OptIn(ExperimentalTime::class)
fun LocalDate.Companion.fromEpochMilliseconds(epochMillis: Long): LocalDate =
    LocalDateTime.fromEpochMilliseconds(epochMillis).date

@OptIn(ExperimentalTime::class)
fun LocalDate.toEpochMilliseconds(): Long =
    atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()

fun LocalDate.isBefore(other: LocalDate): Boolean = this < other

fun LocalDate.isAfter(other: LocalDate): Boolean = this > other