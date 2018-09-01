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
    private var fireRepository = FireStoreRepository()

    fun getLiveReport(): LiveData<Report> {
        val reportId = getValues().current
        return fireRepository.getReport(reportId)
    }

    suspend fun getCurrentReport(): Report {
        val reportId = getValues().current
        Log.d("getCurrent",reportId)
        val reportData =  fireRepository.getStaticReport(reportId)
//        val reportData = currentReport
        Log.d("getCurrent", reportData.toString())
        return reportData
    }

    fun updateCurrentReport(reportId: String) {
//        reportRepository.updateValues(values?.value!!.copy(current = reportId))
        val updateMe = getValues().copy(current = reportId)
        fireRepository.putState(updateMe)
    }

    fun changeCurrentReport(reportId: String) {
        Log.d("changeReportPre",currentReport?.value.toString())
        currentReport = fireRepository.getReport(reportId)
        val vals = getValues()
        fireRepository.putState(vals.copy(current = reportId))
        Log.d("changeReportPost",currentReport?.value.toString())
    }

    fun deleteReport(toDelete: Report) {
        fireRepository.deleteReport(toDelete)
    }

    fun updateReport(updatedReport: Report) {
        fireRepository.updateReport(updatedReport)
    }

    fun createNewReport() {

        fireRepository.updateReport(Report())
    }

    private fun getValues(): Values {
        return fireRepository.getState()
    }

    fun getLiveValues(): LiveData<Values> {
        return fireRepository.getLiveState()
    }

    fun incrementNest(): Int {
        val incremented = getValues().highestNest
        Log.d("incrementNest", "Value: $incremented")
        val updatedValues = getValues().copy(highestNest = incremented + 1)
//        reportRepository.updateValues(values?.value!!.copy(highestNest = incremented + 1))
        fireRepository.putState(updatedValues)

        return incremented

    }

    fun incrementFalseCrawl(): Int {
        val incremented = getValues().highestFalseCrawl
        val updatedValues = getValues().copy(highestFalseCrawl = incremented+1)
//        reportRepository.updateValues(values?.value!!.copy(highestFalseCrawl = values?.value!!.highestFalseCrawl+1))
        fireRepository.putState(updatedValues)
        return incremented

    }

    fun getReportNameList(): LiveData<MutableList<Pair<String,String>>> {
        return Transformations.map(fireRepository.getAllReports()) {
            val names = mutableListOf<Pair<String, String>>()
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