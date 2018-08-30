package com.vaporware.nestreporterv2

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import kotlinx.coroutines.experimental.launch
import java.util.*

class ReportViewModel(application: Application): AndroidViewModel(application) {
    private var currentReport: LiveData<Report>? = null
    private var reportRepository = ReportRepository(application)
    private var values: LiveData<Values>? = null


    fun getLiveReport(): LiveData<Report> {
        if (currentReport == null) {
            currentReport = reportRepository.getReport(values?.value!!.current)
        }
        return currentReport!!
    }

    fun getCurrentReport():Report {
        if (currentReport == null) {
            currentReport = reportRepository.getReport(values?.value!!.current)
        }
        return currentReport?.value!!
    }

    fun updateCurrentReport(reportId: Int) {
        reportRepository.updateValues(values?.value!!.copy(current = reportId))
    }

    fun changeCurrentReport(reportId: Int) {
        currentReport = reportRepository.getReport(reportId)
    }

    fun deleteReport(toDelete: Report) {
        val currentNest = toDelete.nestNumber
        val currentFC = toDelete.falseCrawlNumber
        val highest = getHighestNestNumber()
        var state = values!!.value!!

        if (currentNest != null && currentNest == highest - 1){
            state = state.copy(highestNest = highest-1)
        }
        if (currentFC != null && currentFC == getHighestFCNumber()-1) {
            state = state.copy(highestFalseCrawl = getHighestFCNumber()-1)
        }
        reportRepository.deleteReport(toDelete)
        launch{
            reportRepository.updateValues(state)
            updateToHighestReport()
        }
    }

    private suspend fun updateToHighestReport() {
        val newCurrent = reportRepository.getHighestId().await()
        Log.d("updateToHighest","new Current: $newCurrent")
        reportRepository.updateValues(values!!.value!!.copy(current = newCurrent.toInt()))
        if (newCurrent < 1) {
            createNewReport()
            Log.d("updateToHighest","new Report Created")
        }

    }

    fun updateReport(updatedReport: Report) {
        reportRepository.updateReport(updatedReport)
    }

    private fun getHighestNestNumber(): Int {
        return values?.value!!.highestNest
    }
    private fun getHighestFCNumber(): Int {
        return values?.value!!.highestFalseCrawl
    }

    suspend fun createNewReport() {
        addReport(Report(
                0,
                null,
                null,
                NestType.None,
                "",
                false,
                false,
                false,
                Species.None,
                "",
                false,
                Date(0)
        ))
    }

    private suspend fun addReport(report: Report) {
        val id = reportRepository.addReport(report).await()
        reportRepository.updateValues(values?.value!!.copy(current = id.toInt()))
    }

    fun getValues(): LiveData<Values> {
        if (values == null) {
            values = reportRepository.getValues()
        }
        return values!!
    }

    fun incrementNest(): Int {
        val incremented = values?.value!!.highestNest
        Log.d("incrementNest", "Value: $incremented")
        reportRepository.updateValues(values?.value!!.copy(highestNest = incremented + 1))
        return incremented
    }

    fun incrementFalseCrawl(): Int {
        reportRepository.updateValues(values?.value!!.copy(highestFalseCrawl = values?.value!!.highestFalseCrawl+1))
        return values?.value!!.highestFalseCrawl

    }

    fun getReportNameList(): LiveData<MutableList<Pair<String,Int>>> {
        return Transformations.map(reportRepository.getAllReports()) {
            val names = mutableListOf<Pair<String, Int>>()
            for (report in it) {
                val name = when (report.nestType) {
                    NestType.FalseCrawl,NestType.PossibleFalseCrawl -> "False Crawl ${report.falseCrawlNumber?:"unset"}"
                    else -> "Nest ${report.nestNumber?:"unset"}"
                }
                names.add(name to report.reportId)
            }
            return@map names
        }
    }
}