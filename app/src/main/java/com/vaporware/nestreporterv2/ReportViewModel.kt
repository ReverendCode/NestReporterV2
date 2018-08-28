package com.vaporware.nestreporterv2

import android.arch.lifecycle.ViewModel

class ReportViewModel(private val reportDao: ReportDao) : ViewModel() {
    var report: Report? = null
    fun getReport(reportId: Int): Report {
        if (report == null) {
            report = reportDao.query(reportId)
        }
        return report!!
    }
}