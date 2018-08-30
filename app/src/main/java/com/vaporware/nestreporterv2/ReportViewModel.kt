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
    private var values: Values? = null

    fun getCurrentReport():LiveData<Report> {
        if (currentReport == null) {
            currentReport = reportRepository.getReport(1)
        }
        return currentReport!!
    }

    fun changeCurrentReport(reportId: Int) {
        if (currentReport != null) {
            reportRepository.updateValues(values!!.copy(current = reportId))
            currentReport = reportRepository.getReport(reportId)
        }
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


    fun getValues(): Values {
        if (values == null) {
            values = reportRepository.getValues()
        }
        Log.d("viewModel", "returning: ${values}")
        return values!!
    }

    fun addValues(values: Values) {
        reportRepository.addValues(values)
    }
}