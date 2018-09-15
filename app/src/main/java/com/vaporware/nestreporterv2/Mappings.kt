package com.vaporware.nestreporterv2

import kotlin.reflect.KMutableProperty1

fun getButtonMap() = mapOf(
        R.id.bool_abandoned_body_pits to Info::abandonedBodyPits,
        R.id.bool_abandoned_egg_cavities to Info::abandonedEggCavities,
        R.id.bool_false_crawl to Info::falseCrawl,
        R.id.bool_possible_false_crawl to Info::possibleFalseCrawl,
        R.id.bool_nest_verified to Info::verified,
        R.id.bool_nest_not_verified to Info::notVerified,
        R.id.bool_nest_relocated to Info::nestRelocated,
        R.id.bool_no_digging to Info::noDigging
)

fun getVerificationRuleMap() = mapOf<Pair<Int,Any>,Collection<Pair<KMutableProperty1<Info,Boolean>, Any>>>(

        Pair(R.id.bool_nest_verified, true) to listOf(
                Pair(Info::falseCrawl,false),
                Pair(Info::possibleFalseCrawl,false),
                Pair(Info::notVerified, false)),
        Pair(R.id.bool_false_crawl, true) to listOf(
                Pair(Info::verified, false),
                Pair(Info::possibleFalseCrawl, false),
                Pair(Info::notVerified, false)),
        Pair(R.id.bool_possible_false_crawl, true) to listOf(
                Pair(Info::verified, false),
                Pair(Info::falseCrawl, true),
                Pair(Info::notVerified, false)),
        Pair(R.id.bool_nest_not_verified, true) to listOf(
                Pair(Info::verified, false),
                Pair(Info::falseCrawl, false),
                Pair(Info::possibleFalseCrawl, false)),
        Pair(R.id.bool_nest_relocated, true) to listOf(
                Pair(Info::verified, true),
                Pair(Info::notVerified, false),
                Pair(Info::falseCrawl, false),
                Pair(Info::possibleFalseCrawl, false)))