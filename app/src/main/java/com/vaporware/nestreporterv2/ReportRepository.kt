package com.vaporware.nestreporterv2

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.kiwimob.firestore.livedata.livedata


class FireStoreRepository(email: String) {
    private val fireDb = FirebaseFirestore.getInstance()
            .collection("Sections")
            .document(email)
    private val reports =  fireDb.collection("Reports")
    private val state = fireDb.collection("StateCollection").document("State")

    fun getAllReports(): LiveData<List<Report>> {
        return reports.livedata(Report::class.java)
    }

    fun getReportLiveData(reportId: String): LiveData<Report> {
        return reports.document(reportId).livedata(Report::class.java)
    }

    fun saveReport(report: Report) {
        reports.document(report.infoTab.reportId).set(report)
    }

    fun getReport(reportId: String): Report {
        var report: Report? = null
        reports.document(reportId).get().addOnSuccessListener {
            report = it.toObject(Report::class.java)
        }
        return report!!
    }

    fun getState(): NestAppState {
        var appState: NestAppState? = null
        state.get().addOnSuccessListener {
            appState = it.toObject(NestAppState::class.java)
        }
        return appState!!
    }

    fun getLiveState(): LiveData<NestAppState> {
        return state.livedata(NestAppState::class.java)
    }

    fun putState(updatedState: NestAppState) {
        state.set(updatedState)
    }

    fun changeReport(nextId: String) {
        state.update("currentId", nextId)
    }

    fun updateReport(reportId: String, pairs: Array<out Pair<String, Any>>) {
            for (pair in pairs) {
                reports.document(reportId).update(pair.first, pair.second)
            }
    }

    fun getNameAndIdList(): LiveData<List<Pair<String, String>>> {
        return Transformations.map(reports.livedata(Report::class.java)) {
            val foo = mutableListOf<Pair<String, String>>()
            foo.addAll(it.map { report ->
               report.infoTab.reportId  to report.infoTab.headerName
            })
            foo
        }
    }

    fun deleteReport(reportId: String): String {
        var returnId = ""
        FirebaseFirestore.getInstance().runTransaction {
            it.delete(reports.document(reportId))
            val remainingReportsList = mutableListOf<String>()
            reports.get().addOnSuccessListener { snap ->
                snap.forEach { query ->
                    remainingReportsList.add(query.id)
                }
            }
            returnId = if (remainingReportsList.isEmpty()) {
                createReport()
            } else {
                remainingReportsList.last()
            }
        }
        return returnId
    }

    fun createReport(): String {
        val freshReport = reports.document()
        Log.d("createReport","creating with id: ${freshReport.id}")
        val freshId = freshReport.id
        freshReport.set(Report(infoTab = Info(reportId = freshId)))
        return freshId
    }
}

