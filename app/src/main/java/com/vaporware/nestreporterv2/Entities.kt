package com.vaporware.nestreporterv2

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import java.sql.Date

data class Info(
        var headerName: String = "[Nest Unset]",
        var reportId: String = "0",
        //begin INFO Section
        var nestNumber: Int? = null,
        var falseCrawlNumber: Int? = null,
        var falseCrawl: Boolean = false,
        var verified: Boolean = false,
        var notVerified: Boolean = false,
        var possibleFalseCrawl: Boolean = false,
        var observers: String = "",
        var abandonedBodyPits: Boolean = false,
        var abandonedEggCavities: Boolean = false,
        var noDigging: Boolean = false,
        var species: Species = Species.None,
        var speciesOther: String = "",
        var nestRelocated: Boolean = false,
        var dateCrawlFound: java.util.Date = java.util.Date(0)
)

data class Report(

        var infoTab: Info = Info()
)

data class NestAppState(
        var currentId: String? = null,
        var highestFalseCrawl: Int = 1,
        var highestNest: Int = 1
)
enum class Species {
    Green,Loggerhead,Other, None
}
enum class NestType {
    Verified,Unverified,FalseCrawl,PossibleFalseCrawl,None
}
