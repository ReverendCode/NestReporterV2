package com.vaporware.nestreporterv2

import android.arch.persistence.room.*
import android.content.Context


@Dao
interface ReportDao : BaseDao<Report> {
    @Query("SELECT * FROM reports WHERE report_id = :id")
    fun query(id: Int): Report
    @Query("SELECT COUNT(*) FROM reports")
    fun reportCount(): Int
}

@Database(entities = [Report::class], version = 1)
abstract class ReportDatabase: RoomDatabase() {
    abstract val ReportDao: ReportDao
}