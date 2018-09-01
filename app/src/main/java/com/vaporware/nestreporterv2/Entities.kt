package com.vaporware.nestreporterv2

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import java.sql.Date

data class Report(
        val reportId: String = "0",
        //begin INFO Section
        var nestNumber: Int? = null,
        var falseCrawlNumber: Int? = null,
        val nestType: NestType = NestType.None,
        val observers: String = "",
        val abandonedBodyPits: Boolean = false,
        val abandonedEggCavities: Boolean = false,
        val noDigging: Boolean = false,
        val species: Species = Species.None,
        val speciesOther: String = "",
        val nestRelocated: Boolean = false,
        val dateCrawlFound: java.util.Date = java.util.Date(0)
)


data class Values(
        val current: String = "0",
        val highestNest: Int = 1,
        val highestFalseCrawl: Int = 1
)

enum class Species {
    Green,Loggerhead,Other, None
}
enum class NestType {
    Verified,Unverified,FalseCrawl,PossibleFalseCrawl,None
}
