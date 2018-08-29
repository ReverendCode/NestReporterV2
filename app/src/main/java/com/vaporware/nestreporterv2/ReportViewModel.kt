package com.vaporware.nestreporterv2

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.util.Log
import kotlinx.coroutines.experimental.launch

class ReportViewModel(application: Application): AndroidViewModel(application) {
    private var currentReport: LiveData<Report>? = null
    private var reportRepository = ReportRepository(application)

    fun getCurrentReport(id: Int): LiveData<Report> {
        if (currentReport == null) {
            reportRepository.getReport(id)
        }
        return currentReport!!
    }
    fun getAllReports(): LiveData<List<Report>> {
        Log.d("Report","getting all reports")
        return reportRepository.getAllReports()
    }
    fun updateReport(updatedReport: Report) {
        reportRepository.updateReport(updatedReport)
    }
    fun addReport(report: Report) {
        reportRepository.addReport(report)
    }
    fun newReport(){
        Log.d("newReport","attempting to create Report")
        launch {
            reportRepository.addReport(Report(0,
                    null,
                    null,
                    NestType.None,
                    "",
                    false,
                    false,
                    false,
                    Species.None,
                    "",
                    false))
        }

    }
}