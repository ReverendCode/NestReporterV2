package com.vaporware.nestreporterv2

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import java.sql.Date

@Entity(tableName = "reports")
data class Report(
        @PrimaryKey(autoGenerate = true)
        @NonNull
        @ColumnInfo(name = "report_id")
        val reportId: Int,
        //begin INFO Section
        @ColumnInfo(name = "nest_number")
        var nestNumber: Int?,
        @ColumnInfo(name = "false_crawl_number")
        var falseCrawlNumber: Int?,
        @ColumnInfo(name = "nest_type")
        val nestType: NestType,
        val observers: String,
        @ColumnInfo(name = "abandoned_body_pits")
        val abandonedBodyPits: Boolean,
        @ColumnInfo(name = "abandoned_egg_cavities")
        val abandonedEggCavities: Boolean,
        @ColumnInfo(name = "no_digging")
        val noDigging: Boolean,
        val species: Species,
        @ColumnInfo(name = "species_other")
        val speciesOther: String,
        @ColumnInfo(name = "nest_relocated")
        val nestRelocated: Boolean,
        @ColumnInfo(name = "date_crawl_found")
        val dateCrawlFound: java.util.Date
)

@Entity(tableName = "value_file")
data class Values(
        @PrimaryKey
        val uid: Int,
        val current: Int,
        @ColumnInfo(name = "highest_nest")
        val highestNest: Int,
        @ColumnInfo(name = "highest_false_crawl")
        val highestFalseCrawl: Int
)

enum class Species {
    Green,Loggerhead,Other, None
}
enum class NestType {
    Verified,Unverified,FalseCrawl,PossibleFalseCrawl,None
}
