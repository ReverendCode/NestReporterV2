package com.vaporware.nestreporterv2

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.content.Context


@Dao
interface ReportDao : BaseDao<Report> {
    @Query("SELECT * FROM reports WHERE report_id = :id")
    fun query(id: Int): LiveData<Report>
    @Query("SELECT COUNT(*) FROM reports")
    fun reportCount(): Int
    @Query("SELECT * FROM reports")
    fun getReports(): LiveData<List<Report>>
}
@Dao
interface ValuesDao: BaseDao<Values> {
    @Query("SELECT * FROM value_file WHERE uid = :id")
    fun getValues(id: Int): LiveData<Values>
}
