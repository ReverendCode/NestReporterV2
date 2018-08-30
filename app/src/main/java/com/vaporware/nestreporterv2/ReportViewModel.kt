package com.vaporware.nestreporterv2

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.util.Log
import kotlinx.coroutines.experimental.launch
import java.util.*

class ReportViewModel(application: Application): AndroidViewModel(application) {
    private var currentReport: LiveData<Report>? = null
    private var reportRepository = ReportRepository(application)
    private var values: LiveData<Values>? = null


    fun getLiveReport(): LiveData<Report> {
        if (currentReport == null) {
            currentReport = reportRepository.getReport(1)
        }
        return currentReport!!
    }

    fun getCurrentReport():Report {
        if (currentReport == null) {
            currentReport = reportRepository.getReport(1)
        }
        return currentReport?.value!!
    }

    fun changeCurrentReport(reportId: Int) {
            currentReport = reportRepository.getReport(reportId)

    }

    fun getAllReports(): LiveData<List<Report>> {
        return reportRepository.getAllReports()
    }

    fun updateReport(updatedReport: Report) {
        reportRepository.updateReport(updatedReport)
    }

    fun addReport(report: Report) {
        reportRepository.addReport(report)
    }

    fun getValues(): LiveData<Values> {
        if (values == null) {
            values = reportRepository.getValues()
        }
        return values!!
    }

    fun addValues(values: Values) {
        reportRepository.addValues(values)
    }
    fun incrementNest(): Int {
        Log.d("incrementNest", "values: $values, Value: ${values?.value}")
//        val updatedValues = values.value!!.copy(highestNest = values.value!!.highestNest+1)
//        reportRepository.updateValues(updatedValues)
//        return updatedValues.highestNest
        return 1
    }
    fun incrementFalseCrawl(): Int {

        reportRepository.updateValues(values?.value!!.copy(highestFalseCrawl = values?.value!!.highestFalseCrawl+1))
        return values?.value!!.highestFalseCrawl

    }
}