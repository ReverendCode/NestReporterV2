package com.vaporware.nestreporterv2

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.util.Log
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
    fun addReport(report: Report) {
        launch {
            db.reportDao.create(report)
        }
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
            Log.d("viewModel","values: ${values}")
        }
    }
}