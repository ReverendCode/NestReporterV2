package com.vaporware.nestreporterv2

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.util.Log
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class ReportRepository(context: Context) {
    private val db = ReportDatabase.getInstance(context)

    fun getReport(id: Int): LiveData<Report> {
        return db.reportDao.query(id)
    }
    fun getAllReports(): LiveData<List<Report>> {
        return db.reportDao.getReports()
    }
    fun updateReport(report: Report) {
        launch{
            db.reportDao.update(report)
        }
    }

    fun deleteReport(toDelete: Report) {
        launch{
            db.reportDao.delete(toDelete)
        }

    }

    fun getHighestId(): Deferred<Long> {
        return async{
            db.reportDao.getHighestId()
        }
    }
    fun addReport(report: Report): Deferred<Long> {
        return async {
            db.reportDao.create(report)}
    }
    fun getValues(): LiveData<Values> {

        return db.valuesDao.getValues(0)
    }

    fun addValues(values: Values) {
        launch {
            db.valuesDao.create(values)
        }

    }
    fun updateValues(values: Values) {
        launch{
            db.valuesDao.update(values)
            Log.d("updateValues","values: ${values}")
        }
    }
}