package com.vaporware.nestreporterv2

import android.arch.persistence.room.TypeConverter
import java.util.*


class Converters {
    @TypeConverter
    fun fromNestType(type: NestType?): String? {
        return when (type) {

            NestType.Verified -> "Verified"
            NestType.Unverified -> "Unverified"
            NestType.FalseCrawl -> "False Crawl"
            NestType.PossibleFalseCrawl -> "Possible False Crawl"
            NestType.None -> ""
            null -> null
        }
    }
    @TypeConverter
    fun toNestType(type: String?): NestType? {
        return when (type) {
            "Verified" -> NestType.Verified
            "Unverified" -> NestType.Unverified
            "False Crawl" -> NestType.FalseCrawl
            "Possible False Crawl" -> NestType.PossibleFalseCrawl
            else -> NestType.None
        }
    }
    @TypeConverter
    fun fromSpecies(species: Species?): String? {
        return when (species) {
            Species.Green -> "GREEN"
            Species.Loggerhead -> "LOGGERHEAD"
            Species.Other -> "OTHER"
            Species.None -> ""
            null -> null
        }
    }
    @TypeConverter
    fun toSpecies(species: String?): Species? {
        return when (species) {
            "GREEN" -> Species.Green
            "LOGGERHEAD" -> Species.Loggerhead
            else -> Species.Other
        }
    }
    @TypeConverter
    fun fromDate(date:Date): Long {
        return date.time
    }
    @TypeConverter
    fun toDate(millis: Long): Date {
        return Date(millis)
    }
}