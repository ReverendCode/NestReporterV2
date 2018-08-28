package com.vaporware.nestreporterv2

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import java.sql.Date

@Entity(tableName = "reports")
data class Report(
        @PrimaryKey
        @NonNull
        @ColumnInfo(name = "report_id")
        val reportId: Int,
        //begin INFO Section
        @ColumnInfo(name = "nest_number")
        val nestNumber: Int,
        @ColumnInfo(name = "nest_type")
        val nestType: NestType,
        @ColumnInfo(name = "date_crawl_found")
        val dateCrawlFound: Date,
        val observers: String,
        @ColumnInfo(name = "abandoned_body_pits")
        val abandonedBodyPits: Boolean,
        @ColumnInfo(name = "abandoned_egg_cavities")
        val abandonedEggCavities: Boolean,
        @ColumnInfo(name = "no_digging")
        val noDigging: Boolean,
        val species: Species,
        @ColumnInfo(name = "species_other")
        val speciesOther: String
)
enum class Species {
    Green,Loggerhead,Other
}
enum class NestType {
    Verified,Unverified,FalseCrawl,PossibleFalseCrawl
}
